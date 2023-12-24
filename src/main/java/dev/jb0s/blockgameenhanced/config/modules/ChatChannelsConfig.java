package dev.jb0s.blockgameenhanced.config.modules;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "chat_channels")
public class ChatChannelsConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart(value = false)
    public boolean enable;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart(value = false)
    public boolean showPartyMessageInChat;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart(value = false)
    public boolean closeChatAfterMessage;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart(value = false)
    public boolean compactButton;

    public ChatChannelsConfig() {
        enable = true;
        showPartyMessageInChat = true;
        closeChatAfterMessage = true;
        compactButton = false;
    }
}
