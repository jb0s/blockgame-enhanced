package dev.jb0s.blockgameenhanced;

import dev.jb0s.blockgameenhanced.manager.Manager;
import dev.jb0s.blockgameenhanced.manager.adventure.AdventureZoneManager;
import dev.jb0s.blockgameenhanced.manager.bossbattle.BossBattleManager;
import dev.jb0s.blockgameenhanced.manager.config.ConfigManager;
import dev.jb0s.blockgameenhanced.manager.dayphase.DayPhaseManager;
import dev.jb0s.blockgameenhanced.manager.discordrpc.DiscordRichPresenceManager;
import dev.jb0s.blockgameenhanced.manager.hotkey.HotkeyManager;
import dev.jb0s.blockgameenhanced.manager.music.MusicManager;
import dev.jb0s.blockgameenhanced.manager.party.PartyManager;
import dev.jb0s.blockgameenhanced.manager.update.GitHubRelease;
import dev.jb0s.blockgameenhanced.manager.update.UpdateManager;
import dev.jb0s.blockgameenhanced.module.*;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;

public class BlockgameEnhancedClient implements ClientModInitializer {
    @Getter
    private static ArrayList<Manager> allManagers = new ArrayList<>();

    @Getter
    private static AdventureZoneManager adventureZoneManager;

    @Getter
    private static BossBattleManager bossBattleManager;

    @Getter
    private static MusicManager musicManager;

    @Getter
    private static DayPhaseManager dayPhaseManager;

    @Getter
    private static PartyManager partyManager;

    @Getter
    private static HotkeyManager hotkeyManager;

    @Getter
    private static ConfigManager configManager;

    @Getter
    private static DiscordRichPresenceManager discordRichPresenceManager;

    @Getter
    private static UpdateManager updateManager;

    @Getter
    private static GitHubRelease availableUpdate;

    @Override
    public void onInitializeClient() {
        // Bind generic events
        // TODO: Use managers for these
        UseBlockCallback.EVENT.register(MMOItems::preventIllegalMMOItemsInteraction);
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            client.player.sendMessage(new TranslatableText("hud.blockgame.message.welcome.1"), false);
            client.player.sendMessage(new TranslatableText("hud.blockgame.message.welcome.2"), false);
        });

        // Bind Managers
        musicManager = new MusicManager();
        bossBattleManager = new BossBattleManager();
        dayPhaseManager = new DayPhaseManager();
        adventureZoneManager = new AdventureZoneManager();
        partyManager = new PartyManager();
        hotkeyManager = new HotkeyManager();
        configManager = new ConfigManager();
        discordRichPresenceManager = new DiscordRichPresenceManager();
        updateManager = new UpdateManager();

        // Check for updates.
        // If you're looking for the actual "There's an update" GUI prompt, it's in MixinTitleScreen.java.
        if(BlockgameEnhanced.getConfig().getAccessibilityConfig().enableUpdateChecker) {
            availableUpdate = updateManager.checkForUpdates();
            if(getAvailableUpdate() != null) {
                BlockgameEnhanced.LOGGER.info("New update available: " + getAvailableUpdate().tag_name);
            }
            else {
                BlockgameEnhanced.LOGGER.info("Mod is up-to-date");
            }
        }
        else {
            BlockgameEnhanced.LOGGER.info("Update checking is disabled by user");
        }

        // Tick all managers after client ticks
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            for (Manager manager : getAllManagers()) {
                manager.tick(client);
            }
        });
    }
}
