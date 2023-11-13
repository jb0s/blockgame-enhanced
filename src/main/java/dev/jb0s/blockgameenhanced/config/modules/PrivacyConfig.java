package dev.jb0s.blockgameenhanced.config.modules;

import dev.jb0s.blockgameenhanced.config.structure.DiscordRPCPrivacy;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "privacy")
public class PrivacyConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean enableDiscordRpc;

    @ConfigEntry.Gui.Tooltip
    public boolean discordRpcShowIngameStatus;

    @ConfigEntry.Gui.Tooltip
    public boolean discordRpcShowPartyStatus;

    public PrivacyConfig() {
        enableDiscordRpc = true;
        discordRpcShowIngameStatus = true;
        discordRpcShowPartyStatus = true;
    }

    /**
     * Gets the privacy level for the Discord Rich Presence.
     * Okay, hear me out, I know this is scuffed as hell, but Cloth Config will literally not play nice if I do this any other way.
     * I really don't feel like restructuring my system because the config lib is shit. Too bad!
     * @return Privacy level for Discord RPC, determined by the 3 booleans: enableDiscordRpc, discordRpcShowIngameStatus, discordRpcShowPartyStatus
     */
    public DiscordRPCPrivacy getDiscordRPCPrivacy() {
        if(!enableDiscordRpc) {
            return DiscordRPCPrivacy.HIDE_COMPLETELY;
        }

        if(!discordRpcShowIngameStatus) {
            return DiscordRPCPrivacy.PLAYING_ONLY;
        }

        if(!discordRpcShowPartyStatus) {
            return DiscordRPCPrivacy.PLAYING_AND_INGAME;
        }

        return DiscordRPCPrivacy.PLAYING_INGAME_AND_PARTY;
    }
}