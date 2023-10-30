package dev.jb0s.blockgameenhanced.mixin.client;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.world.WorldUpdatedEvent;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.ImmersiveIngameHud;
import dev.jb0s.blockgameenhanced.gui.screen.title.TitleScreen;
import dev.jb0s.blockgameenhanced.manager.config.modules.IngameHudConfig;
import dev.jb0s.blockgameenhanced.renderer.debug.BlockgameDebugRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.Session;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.resource.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.QueueingWorldGenerationProgressListener;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.UserCache;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Shadow public abstract LevelStorage getLevelStorage();
    @Shadow public abstract SaveLoader createSaveLoader(ResourcePackManager dataPackManager, boolean safeMode, SaveLoader.DataPackSettingsSupplier dataPackSettingsSupplier, SaveLoader.SavePropertiesSupplier savePropertiesSupplier) throws InterruptedException, ExecutionException;
    @Shadow public abstract void disconnect();
    @Shadow public abstract Session getSession();
    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Shadow private @Nullable IntegratedServer server;
    @Shadow @Final private AtomicReference<WorldGenerationProgressTracker> worldGenProgressTracker;
    @Shadow private boolean integratedServerRunning;
    @Shadow private Profiler profiler;
    @Shadow private ClientConnection integratedServerConnection;
    @Shadow @Final private Proxy networkProxy;
    @Shadow @Final public File runDirectory;
    @Shadow @Final private Queue<Runnable> renderTaskQueue;
    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Nullable public ClientWorld world;
    @Mutable @Shadow @Final public InGameHud inGameHud;
    @Mutable
    @Shadow @Final public DebugRenderer debugRenderer;
    private static final MusicSound MUSIC_SILENCE = new MusicSound(new SoundEvent(new Identifier("blockgame", "silence")), 999999, 999999, false);

    @Inject(method = "getMusicType", at = @At("RETURN"), cancellable = true)
    public void getMusicType(CallbackInfoReturnable<MusicSound> cir) {
        World world = MinecraftClient.getInstance().world;
        if(world != null) {
            cir.setReturnValue(MUSIC_SILENCE);
        }
    }

    @Inject(method = "setWorld", at = @At("RETURN"))
    public void setWorld(ClientWorld world, CallbackInfo ci) {
        WorldUpdatedEvent.EVENT.invoker().worldUpdated(world);
    }

    @Inject(method = "setScreen", at = @At("RETURN"))
    public void setScreen(Screen screen, CallbackInfo ci) {
        if(screen == null && BlockgameEnhancedClient.isRunningCompatibilityServer()) {
            setScreen(new TitleScreen());
        }
    }

    //////////////////////////////////
    ///   OPTIFINE COMPATIBILITY   ///
    //////////////////////////////////

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(RunArgs args, CallbackInfo ci) {
        MinecraftClient thisMinecraft = (MinecraftClient) (Object) this;

        if(BlockgameEnhanced.isOptifinePresent()) {
            startDummyServer("Empty", SaveLoader.DataPackSettingsSupplier::loadFromWorld, SaveLoader.SavePropertiesSupplier::loadFromWorld);
        }

        // Apply Custom DebugRenderer
        //debugRenderer = new BlockgameDebugRenderer(thisMinecraft);

        // Apply Custom HUD
        IngameHudConfig ingameHudConfig = BlockgameEnhanced.getConfig().getIngameHudConfig();
        if(ingameHudConfig.enableCustomHud) {
            inGameHud = new ImmersiveIngameHud(thisMinecraft);
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    public void preDisconnect(Screen screen, CallbackInfo ci) {
        if(BlockgameEnhancedClient.isRunningCompatibilityServer()) {
            if(server != null) {
                server.stop(false);
            }

            if(world != null) {
                world.disconnect();
            }
        }
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("RETURN"))
    public void postDisconnect(Screen screen, CallbackInfo ci) {
        if(BlockgameEnhancedClient.isRunningCompatibilityServer()) {
            BlockgameEnhancedClient.setRunningCompatibilityServer(false);
            BlockgameEnhancedClient.setCompatibilityServerReady(false);
        }
        else if(BlockgameEnhanced.isOptifinePresent()) {
            startDummyServer("Empty", SaveLoader.DataPackSettingsSupplier::loadFromWorld, SaveLoader.SavePropertiesSupplier::loadFromWorld);
        }
    }

    private void startDummyServer(String worldName, Function<LevelStorage.Session, SaveLoader.DataPackSettingsSupplier> dataPackSettingsSupplierGetter, Function<LevelStorage.Session, SaveLoader.SavePropertiesSupplier> savePropertiesSupplierGetter) {
        MinecraftClient thisMinecraft = (MinecraftClient) (Object) this;
        SaveLoader saveLoader;
        LevelStorage.Session session;

        try {
            session = getLevelStorage().createSession("Empty");
        }
        catch (IOException exception) {
            BlockgameEnhanced.LOGGER.warn("Failed to read level {} data", worldName, exception);
            SystemToast.addWorldAccessFailureToast(thisMinecraft, worldName);
            return;
        }

        ResourcePackManager resourcePackManager = new ResourcePackManager(ResourceType.SERVER_DATA, new VanillaDataPackProvider(), new FileResourcePackProvider(session.getDirectory(WorldSavePath.DATAPACKS).toFile(), ResourcePackSource.PACK_SOURCE_WORLD));

        try {
            saveLoader = createSaveLoader(resourcePackManager, false, dataPackSettingsSupplierGetter.apply(session), savePropertiesSupplierGetter.apply(session));
        }
        catch (Exception exception) {
            BlockgameEnhanced.LOGGER.warn("Failed to read level {} data", worldName, exception);
            SystemToast.addWorldAccessFailureToast(thisMinecraft, worldName);
            return;
        }

        SaveProperties saveProperties = saveLoader.saveProperties();
        //disconnect();

        try {
            DynamicRegistryManager.Immutable immutable = saveLoader.dynamicRegistryManager();
            session.backupLevelDataFile(immutable, saveProperties);
            saveLoader.refresh();

            YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(networkProxy);
            MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
            GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
            UserCache userCache = new UserCache(gameProfileRepository, new File(runDirectory, MinecraftServer.USER_CACHE_FILE.getName()));
            userCache.setExecutor(thisMinecraft);
            UserCache.setUseRemote(false);

            this.server = MinecraftServer.startServer(thread -> new IntegratedServer(thread, thisMinecraft, session, resourcePackManager, saveLoader, minecraftSessionService, gameProfileRepository, userCache, spawnChunkRadius -> {
                WorldGenerationProgressTracker worldGenerationProgressTracker = new WorldGenerationProgressTracker(spawnChunkRadius + 0);
                worldGenProgressTracker.set(worldGenerationProgressTracker);
                return QueueingWorldGenerationProgressListener.create(worldGenerationProgressTracker, renderTaskQueue::add);
            }));
            integratedServerRunning = true;
        }
        catch(Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Starting OptiFine Compatibility server");
            CrashReportSection crashReportSection = crashReport.addElement("Starting OptiFine Compatibility server");
            crashReportSection.add("Level ID", worldName);
            crashReportSection.add("Level Name", saveProperties.getLevelName());
            throw new CrashException(crashReport);
        }

        profiler.push("waitForOptiFineCompatibilityServer");

        while(server.isLoading()) {
            try {
                Thread.sleep(16L);
            }
            catch (InterruptedException crashReport) {
                // empty catch block
            }
        }

        profiler.pop();
        BlockgameEnhancedClient.setRunningCompatibilityServer(true);

        SocketAddress socketAddress = this.server.getNetworkIo().bindLocal();
        ClientConnection clientConnection = ClientConnection.connectLocal(socketAddress);
        clientConnection.setPacketListener(new ClientLoginNetworkHandler(clientConnection, thisMinecraft, null, text -> {}));
        clientConnection.send(new HandshakeC2SPacket(socketAddress.toString(), 0, NetworkState.LOGIN));
        clientConnection.send(new LoginHelloC2SPacket(getSession().getProfile()));
        integratedServerConnection = clientConnection;
    }
}
