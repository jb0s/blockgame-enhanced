package dev.jb0s.blockgameenhanced;

import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.bettergui.BetterGUIGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.challenges.ChallengesGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.dayphase.DayPhaseGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.discordrpc.DiscordRPCGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.hotkey.HotkeyGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.immersivehud.ImmersiveHUDGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.joingreeting.JoinGreetingGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.jukebox.JukeboxGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.mmoitems.MMOItemsGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.mmostats.MMOStatsGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.party.PartyGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.titlescreen.TitleScreenGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.updateprompter.UpdatePrompterGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.zone.ZoneGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.zoneboss.ZoneBossGameFeature;
import dev.jb0s.blockgameenhanced.gui.screen.title.TitleScreen;
import dev.jb0s.blockgameenhanced.config.ConfigManager;
import dev.jb0s.blockgameenhanced.update.GitHubRelease;
import dev.jb0s.blockgameenhanced.update.UpdateManager;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockgameEnhancedClient implements ClientModInitializer {
    @Getter
    private static final ArrayList<GameFeature> loadedGameFeatures = new ArrayList<>();

    @Getter
    private static ConfigManager configManager;

    @Getter
    private static UpdateManager updateManager;

    @Getter
    @Setter
    private static GitHubRelease availableUpdate;

    @Getter
    @Setter
    private static boolean isRunningCompatibilityServer;

    @Getter
    @Setter
    private static boolean isCompatibilityServerReady;

    @Getter
    @Setter
    private static int preloginLatency;

    @Getter
    @Setter
    private static int latency;

    @Getter
    private List<String> userDisabledGameFeatureNames;

    @Override
    public void onInitializeClient() {
        BlockgameEnhanced.LOGGER.info("Welcome to Blockgame Enhanced");
        BlockgameEnhanced.LOGGER.info("Run directory: " + MinecraftClient.getInstance().runDirectory.getAbsolutePath());
        BlockgameEnhanced.LOGGER.info("Debug Mode: " + BlockgameEnhanced.DEBUG);

        configManager = new ConfigManager();
        updateManager = new UpdateManager();

        // Load all game features
        parseUserDisabledGameFeatures();
        loadGameFeatures();

        // Greet the player when they join the server
        // todo: move this
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {

            // Custom routines for finishing the initialization of an OptiFine Compat server
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
        });
    }

    /**
     * Extracts list of disabled game features from commandline.
     */
    private void parseUserDisabledGameFeatures() {
        String x = System.getProperty("nullgf");
        if(x == null) {
            userDisabledGameFeatureNames = new ArrayList<>();
            return;
        }

        userDisabledGameFeatureNames = Arrays.stream(x.toLowerCase().split(",")).toList();
    }

    /**
     * Loads all game features.
     */
    private void loadGameFeatures() {
        BlockgameEnhanced.LOGGER.info("Loading game features");
        loadGameFeature(new ImmersiveHUDGameFeature());
        loadGameFeature(new TitleScreenGameFeature());
        loadGameFeature(new ZoneGameFeature());
        loadGameFeature(new ZoneBossGameFeature());
        loadGameFeature(new DayPhaseGameFeature());
        loadGameFeature(new BetterGUIGameFeature());
        loadGameFeature(new DiscordRPCGameFeature());
        loadGameFeature(new HotkeyGameFeature());
        loadGameFeature(new MMOStatsGameFeature());
        loadGameFeature(new MMOItemsGameFeature());
        loadGameFeature(new PartyGameFeature());
        loadGameFeature(new UpdatePrompterGameFeature());
        loadGameFeature(new JukeboxGameFeature());
        loadGameFeature(new ChallengesGameFeature());
        loadGameFeature(new JoinGreetingGameFeature());

        // Tick all game features after client ticks
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            client.getProfiler().push("tickGameFeatures");

            for (GameFeature gameFeature : getLoadedGameFeatures()) {
                client.getProfiler().push(gameFeature.getClass().getSimpleName());
                gameFeature.tick();
                client.getProfiler().pop();
            }

            client.getProfiler().pop();
        });
    }

    /**
     * Loads a specific game feature.
     * @param gameFeature Instance of the game feature to load
     */
    private void loadGameFeature(GameFeature gameFeature) {
        String name = gameFeature.getClass().getSimpleName().replace("GameFeature", "").toLowerCase();
        boolean userDisabled = getUserDisabledGameFeatureNames().contains(name);

        if(!gameFeature.isEnabled() || userDisabled) {
            BlockgameEnhanced.LOGGER.info("Skipping load of {} because it's disabled", gameFeature.getClass().getSimpleName().replace("GameFeature", " game feature"));
            return;
        }

        BlockgameEnhanced.LOGGER.info("Loading {}", gameFeature.getClass().getSimpleName().replace("GameFeature", " game feature"));
        gameFeature.init(MinecraftClient.getInstance(), this);
        loadedGameFeatures.add(gameFeature);
    }
}
