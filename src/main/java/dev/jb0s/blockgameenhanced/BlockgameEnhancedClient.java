package dev.jb0s.blockgameenhanced;

import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.bettergui.BetterGUIGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.challenges.ChallengesGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.dayphase.DayPhaseGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.discordrpc.DiscordRPCGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.hotkey.HotkeyGameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.immersivehud.ImmersiveHUDGameFeature;
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
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;

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

    @Override
    public void onInitializeClient() {
        BlockgameEnhanced.LOGGER.info("Welcome to Blockgame Enhanced");

        configManager = new ConfigManager();
        updateManager = new UpdateManager();

        // Load all game features
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

            // We're normally joining a game, send a welcome message in chat.
            else {
                client.player.sendMessage(new TranslatableText("hud.blockgame.message.welcome.1"), false);
                client.player.sendMessage(new TranslatableText("hud.blockgame.message.welcome.2"), false);
            }
        });
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

        // Tick all game features after client ticks
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            for (GameFeature gameFeature : getLoadedGameFeatures()) {
                gameFeature.tick();
            }
        });
    }

    /**
     * Loads a specific game feature.
     * @param gameFeature Instance of the game feature to load
     */
    private void loadGameFeature(GameFeature gameFeature) {
        if(!gameFeature.isEnabled()) {
            BlockgameEnhanced.LOGGER.info("Skipping load of {} because it's disabled", gameFeature.getClass().getSimpleName().replace("GameFeature", " game feature"));
            return;
        }

        BlockgameEnhanced.LOGGER.info("Loading {}", gameFeature.getClass().getSimpleName().replace("GameFeature", " game feature"));
        gameFeature.init(MinecraftClient.getInstance(), this);
        loadedGameFeatures.add(gameFeature);
    }
}
