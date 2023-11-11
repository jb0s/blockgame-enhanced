package dev.jb0s.blockgameenhanced.manager.config.modules;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "chat")
public class ChatConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart(value = false)
    public String mentionAliases;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart(value = false)
    public boolean enableMentions;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart(value = false)
    public boolean enableMentionSound;

    public ChatConfig() {
        mentionAliases = "";
        enableMentions = true;
        enableMentionSound = true;
    }
}