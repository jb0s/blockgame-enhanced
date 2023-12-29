package dev.jb0s.blockgameenhanced.gamefeature.optifinecompat;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.client.ClientDisconnectionEvents;
import dev.jb0s.blockgameenhanced.event.client.ClientLifetimeEvents;
import dev.jb0s.blockgameenhanced.event.client.ClientScreenChanged;
import dev.jb0s.blockgameenhanced.event.server.ServerPrepareStartRegionEvent;
import dev.jb0s.blockgameenhanced.event.splash.SplashRenderEvent;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.gui.screen.title.TitleScreen;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.resource.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.QueueingWorldGenerationProgressListener;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;

import java.io.File;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.function.Function;

/**
 * This entire game feature is likely made specifically for Thor LMAO
 */
public class OptiFineCompatGameFeature extends GameFeature {

    @Getter
    @Setter
    private static boolean isRunningCompatibilityServer;

    @Getter
    @Setter
    private static boolean isCompatibilityServerReady;

    @Getter
    @Setter
    private boolean hasDelayedStartupFinishingBefore;

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);

        // Subscribe to events
        ClientLifetimeEvents.INIT.register(this::handleClientInit);
        ClientLifetimeEvents.STOP.register(this::handleClientStop);
        ClientLifetimeEvents.CLOSE.register(this::handleClientClose);
        ClientPlayConnectionEvents.JOIN.register(this::handleWorldJoin);
        ClientScreenChanged.EVENT.register(this::handleScreenChanged);
        ClientDisconnectionEvents.PRE_DISCONNECT.register(this::handleClientPreDisconnect);
        ClientDisconnectionEvents.POST_DISCONNECT.register(this::handleClientPostDisconnect);
        ServerPrepareStartRegionEvent.EVENT.register(this::handleServerPrepareStartRegion);
        SplashRenderEvent.EVENT.register(this::handleSplashRender);
    }

    /**
     * Prevents MOJANG STUDIOS splash from disappearing until we've finished our own initialization
     */
    private ActionResult handleSplashRender(SplashOverlay splashOverlay, MatrixStack matrices, int mouseX, int mouseY, float delta) {
        boolean isDoingServerOnStartup = isRunningCompatibilityServer() && !isCompatibilityServerReady();

        // Don't let the startup finish if we personally are not done yet
        if(isDoingServerOnStartup) {
            setHasDelayedStartupFinishingBefore(true);
            return ActionResult.CONSUME;
        }
        else if(hasDelayedStartupFinishingBefore) {
            splashOverlay.reloadCompleteTime = Util.getMeasuringTimeMs();
            return ActionResult.PASS;
        }

        return ActionResult.PASS;
    }

    /**
     * Cuts down load time by approximately 7s when running OptiFine compatibility server
     */
    private ActionResult handleServerPrepareStartRegion(MinecraftServer minecraftServer, WorldGenerationProgressListener progressListener) {
        if(isRunningCompatibilityServer()) {
            minecraftServer.updateMobSpawnOptions();
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }

    /**
     * Stop
     */
    private void handleClientPostDisconnect(MinecraftClient client) {
        if(isRunningCompatibilityServer()) {
            setRunningCompatibilityServer(false);
            setCompatibilityServerReady(false);
        }
        else if(BlockgameEnhanced.isOptifinePresent()) {
            startDummyServer("Empty", SaveLoader.DataPackSettingsSupplier::loadFromWorld, SaveLoader.SavePropertiesSupplier::loadFromWorld);
        }
    }

    /**
     * Stop any dummy servers when the client tries to disconnect
     */
    private void handleClientPreDisconnect(MinecraftClient client) {
        if(isRunningCompatibilityServer()) {

            // If we're running a server, stop that server now
            if(client.server != null) {
                client.server.stop(false);
            }

            // If we're in a world, disconnect from whatever that world belongs to
            if(client.world != null) {
                client.world.disconnect();
            }
        }
    }

    /**
     * Prevent user from escaping into the dummy world by switching to the Title Screen instead of a null screen.
     */
    private void handleScreenChanged(MinecraftClient client, Screen screen) {
        if(screen == null && isRunningCompatibilityServer()) {
            client.setScreen(new TitleScreen());
        }
    }

    /**
     * Spin up a dummy server on client init
     */
    private void handleClientInit(MinecraftClient client, RunArgs runArgs) {
        startDummyServer("Empty", SaveLoader.DataPackSettingsSupplier::loadFromWorld, SaveLoader.SavePropertiesSupplier::loadFromWorld);
    }

    /**
     * Stop any running dummy servers when client is being stopped
     */
    private void handleClientStop(MinecraftClient client) {
        if(isRunningCompatibilityServer() && client.server != null) {
            client.server.stop(false);
        }
    }

    /**
     * Stop any running dummy servers when client is closing
     */
    private void handleClientClose(MinecraftClient client) {
        if(isRunningCompatibilityServer() && client.server != null) {
            client.server.stop(false);
        }
    }

    /**
     * Custom routines for finishing the initialization of an OptiFine Compat server
     */
    private void handleWorldJoin(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient client) {
        if(isRunningCompatibilityServer()) {
            MinecraftClient.getInstance().setScreen(new TitleScreen());

            // Wait a sec before allowing game to finish loading.
            // This is to work around a dumb hitch I can't get around otherwise.
            new Thread(() -> {
                try { Thread.sleep(1000); }
                catch (Exception e) { /* too bad */ }

                setCompatibilityServerReady(true);
                BlockgameEnhanced.LOGGER.info("~~ OPTIFINE COMPAT SERVER IS READY ~~");
            }).start();
        }
    }

    /**
     * Spins up an empty server to sit in while on the Title Screen so that we can render a player
     * @param worldName Name of the world to load on server
     * @param dataPackSettingsSupplierGetter Data pack settings supplier
     * @param savePropertiesSupplierGetter Save properties supplier
     */
    private void startDummyServer(String worldName, Function<LevelStorage.Session, SaveLoader.DataPackSettingsSupplier> dataPackSettingsSupplierGetter, Function<LevelStorage.Session, SaveLoader.SavePropertiesSupplier> savePropertiesSupplierGetter) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        SaveLoader saveLoader;
        LevelStorage.Session session;

        try {
            session = minecraft.getLevelStorage().createSession("Empty");
        }
        catch (IOException exception) {
            BlockgameEnhanced.LOGGER.warn("Failed to read level {} data", worldName, exception);
            SystemToast.addWorldAccessFailureToast(minecraft, worldName);
            return;
        }

        ResourcePackManager resourcePackManager = new ResourcePackManager(ResourceType.SERVER_DATA, new VanillaDataPackProvider(), new FileResourcePackProvider(session.getDirectory(WorldSavePath.DATAPACKS).toFile(), ResourcePackSource.PACK_SOURCE_WORLD));

        try {
            saveLoader = minecraft.createSaveLoader(resourcePackManager, false, dataPackSettingsSupplierGetter.apply(session), savePropertiesSupplierGetter.apply(session));
        }
        catch (Exception exception) {
            BlockgameEnhanced.LOGGER.warn("Failed to read level {} data", worldName, exception);
            SystemToast.addWorldAccessFailureToast(minecraft, worldName);
            return;
        }

        SaveProperties saveProperties = saveLoader.saveProperties();
        //disconnect();

        try {
            DynamicRegistryManager.Immutable immutable = saveLoader.dynamicRegistryManager();
            session.backupLevelDataFile(immutable, saveProperties);
            saveLoader.refresh();

            YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(minecraft.networkProxy);
            MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
            GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
            UserCache userCache = new UserCache(gameProfileRepository, new File(minecraft.runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
            userCache.setExecutor(minecraft);
            UserCache.setUseRemote(false);

            minecraft.server = MinecraftServer.startServer(thread -> new IntegratedServer(thread, minecraft, session, resourcePackManager, saveLoader, minecraftSessionService, gameProfileRepository, userCache, spawnChunkRadius -> {
                WorldGenerationProgressTracker worldGenerationProgressTracker = new WorldGenerationProgressTracker(spawnChunkRadius + 0);
                minecraft.worldGenProgressTracker.set(worldGenerationProgressTracker);
                return QueueingWorldGenerationProgressListener.create(worldGenerationProgressTracker, minecraft.renderTaskQueue::add);
            }));
            minecraft.integratedServerRunning = true;
        }
        catch(Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Starting OptiFine Compatibility server");
            CrashReportSection crashReportSection = crashReport.addElement("Starting OptiFine Compatibility server");
            crashReportSection.add("Level ID", worldName);
            crashReportSection.add("Level Name", saveProperties.getLevelName());
            throw new CrashException(crashReport);
        }

        minecraft.profiler.push("waitForOptiFineCompatibilityServer");

        while(minecraft.server.isLoading()) {
            try {
                Thread.sleep(16L);
            }
            catch (InterruptedException crashReport) {
                // empty catch block
            }
        }

        minecraft.profiler.pop();
        setRunningCompatibilityServer(true);

        SocketAddress socketAddress = minecraft.server.getNetworkIo().bindLocal();
        ClientConnection clientConnection = ClientConnection.connectLocal(socketAddress);
        clientConnection.setPacketListener(new ClientLoginNetworkHandler(clientConnection, minecraft, null, text -> {}));
        clientConnection.send(new HandshakeC2SPacket(socketAddress.toString(), 0, NetworkState.LOGIN));
        clientConnection.send(new LoginHelloC2SPacket(minecraft.getSession().getProfile()));
        minecraft.integratedServerConnection = clientConnection;
    }

    /**
     * This GameFeature is only enabled if the mod was loaded alongside OptiFine
     */
    @Override
    public boolean isEnabled() {
        return BlockgameEnhanced.isOptifinePresent();
    }
}
