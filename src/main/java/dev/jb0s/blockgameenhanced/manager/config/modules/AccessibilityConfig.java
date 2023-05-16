package dev.jb0s.blockgameenhanced.manager.config.modules;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "accessibility")
public class AccessibilityConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean enableAutoRespawn;

    @ConfigEntry.Gui.Tooltip
    public boolean enableItemLabels;

    @ConfigEntry.Gui.Tooltip
    public boolean enableCustomTitleScreen;

    public AccessibilityConfig() {
        enableAutoRespawn = true;
        enableItemLabels = true;
        enableCustomTitleScreen = true;
    }
}