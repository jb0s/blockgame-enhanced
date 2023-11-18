package dev.jb0s.blockgameenhanced.config.modules;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "ingame_hud")
public class IngameHudConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart(value = true)
    public boolean enableCustomHud;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart(value = false)
    public boolean showAdvancedStats;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart(value = false)
    public boolean showCooldownsInHotbar;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart(value = false)
    public boolean showProfessionExpInChat;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart(value = false)
    public boolean enablePickupStream;

    public IngameHudConfig() {
        enableCustomHud = true;
        showAdvancedStats = false;
        showCooldownsInHotbar = true;
        showProfessionExpInChat = false;
        enablePickupStream = true;
    }
}