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
import dev.jb0s.blockgameenhanced.gamefeature.latency.LatencyGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.mmoitems.MMOItemsGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.mmostats.MMOStatsGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.optifinecompat.OptiFineCompatGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.party.PartyGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.recipetracker.RecipeTrackerGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.titlescreen.TitleScreenGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.updateprompter.UpdatePrompterGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.zone.ZoneGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.zoneboss.ZoneBossGameFeature;
import dev.jb0s.blockgameenhanced.config.ConfigManager;
import dev.jb0s.blockgameenhanced.update.GitHubRelease;
import dev.jb0s.blockgameenhanced.update.UpdateManager;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

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
    private static int preLoginLatency;

    @Getter
    @Setter
    private static int latency;

    @Getter
    private List<String> userDisabledGameFeatureNames;

    @Getter
    @Setter
    private static int errors;

    @Getter
    private static final int maxErrorsBeforeCrash = 5;

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
        loadGameFeature(new LatencyGameFeature());
        loadGameFeature(new RecipeTrackerGameFeature());
        loadGameFeature(new OptiFineCompatGameFeature());

        // Tick all game features after client ticks
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            client.getProfiler().push("tickGameFeatures");

            for (GameFeature gameFeature : getLoadedGameFeatures()) {
                client.getProfiler().push(gameFeature.getClass().getSimpleName());

                // Try to tick and don't crash if it fails
                try {
                    gameFeature.tick();
                }
                catch (Exception e) {
                    // Crash if there's been too many errors
                    if(errors > maxErrorsBeforeCrash) {
                        throw e;
                    }

                    ClientPlayerEntity player = client.player;
                    if(player != null) {
                        player.sendMessage(Text.of("§4§l=== PLEASE REPORT THIS AS A BUG ==="), false);
                        player.sendMessage(Text.of(String.format("§cAn error occurred in %s!", gameFeature.getClass().getSimpleName())), false);
                        player.sendMessage(Text.of(e.getClass().getName() + ": §7" + e.getMessage()), false);
                        player.sendMessage(Text.of("§4§l================================="), false);
                        errors++;
                    }
                }

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

        String featureName = gameFeature.getClass().getSimpleName().replace("GameFeature", " game feature");
        BlockgameEnhanced.LOGGER.info("Loading {}", featureName);

        try {
            gameFeature.init(MinecraftClient.getInstance(), this);
            loadedGameFeatures.add(gameFeature);
        }
        catch (Exception e) {
            BlockgameEnhanced.LOGGER.error("Failed to load {} game feature: {}", featureName, e.getMessage());
        }
    }
}
