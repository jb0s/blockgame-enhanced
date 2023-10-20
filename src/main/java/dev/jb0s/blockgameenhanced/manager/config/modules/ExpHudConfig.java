package dev.jb0s.blockgameenhanced.manager.config.modules;

import lombok.Getter;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "exp_hud")
public class ExpHudConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean expHudEnabled;

    @ConfigEntry.Gui.Tooltip
    public float xPosExpHud;

    @ConfigEntry.Gui.Tooltip
    public float yPosExpHud;

    @ConfigEntry.Gui.Tooltip
    public float lineSpacingExpHud;

    @ConfigEntry.Gui.Tooltip
    public boolean coinEnabledExpHud;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.ColorPicker
    public Integer textColorExpHud;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.ColorPicker
    public Integer coinColorExpHud;

    @ConfigEntry.Gui.Tooltip
    public float expHudScale;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
    public float expHudOpacity;

    @ConfigEntry.Gui.Tooltip
    public boolean chatExpEnabled;

    @ConfigEntry.Gui.Tooltip
    public boolean chatCoinEnabled;

    @Getter
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
    AdvancedExpHudConfig advancedExpHudConfig = new AdvancedExpHudConfig();


    public ExpHudConfig() {
        expHudEnabled = true; // Should the Hud Render
        xPosExpHud = 3f; // Horizontal Hud position
        yPosExpHud = 30f; // Vertical Hud position
        lineSpacingExpHud = 10f; // Line Spacing
        coinEnabledExpHud = true; // Show Coin In Hud
        textColorExpHud = 0xAAAAAA; // The EXP Text Color
        coinColorExpHud = 0xFFAA00; // The Coin Text Color
        expHudScale = 0.9f; // HUD Scale
        expHudOpacity = 0.3f; // HUD Opacity
        chatExpEnabled = true; // remove Exp from Chat?
        chatCoinEnabled = true; // remove Coins from chat?
    }
}
