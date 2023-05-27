package dev.jb0s.blockgameenhanced.manager.config.modules;

import dev.jb0s.blockgameenhanced.manager.config.structure.DiscordRPCPrivacy;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "privacy")
public class PrivacyConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public DiscordRPCPrivacy discordRPCPrivacy;

    public PrivacyConfig() {
        discordRPCPrivacy = DiscordRPCPrivacy.PLAYING_INGAME_AND_PARTY;
    }
}