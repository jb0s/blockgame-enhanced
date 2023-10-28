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

    @ConfigEntry.Gui.Tooltip
    public boolean enableUpdateChecker;

    @ConfigEntry.Gui.Tooltip
    public boolean enableLootAllButton;

    @ConfigEntry.Gui.Tooltip
    public boolean enableAutofillDeposit;

    @ConfigEntry.Gui.Tooltip
    public boolean immersiveHud; // debug

    public AccessibilityConfig() {
        enableAutoRespawn = true;
        enableItemLabels = true;
        enableCustomTitleScreen = true;
        enableUpdateChecker = true;
        enableLootAllButton = true;
        enableAutofillDeposit = true;
        immersiveHud = true; // debug
    }
}