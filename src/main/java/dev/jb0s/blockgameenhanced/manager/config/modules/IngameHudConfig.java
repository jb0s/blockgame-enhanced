package dev.jb0s.blockgameenhanced.manager.config.modules;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "ingame_hud")
public class IngameHudConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public boolean enableCustomHud;

    @ConfigEntry.Gui.Tooltip
    public boolean showAdvancedStats;

    public IngameHudConfig() {
        enableCustomHud = true;
    }
}