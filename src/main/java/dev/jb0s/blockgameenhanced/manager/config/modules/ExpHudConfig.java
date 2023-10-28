package dev.jb0s.blockgameenhanced.manager.config.modules;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "exp_hud")
public class ExpHudConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean expHudEnabled;

    @ConfigEntry.Gui.Tooltip
    public boolean coinEnabledExpHud;

    @ConfigEntry.Gui.Tooltip
    public boolean chatExpEnabled;

    @ConfigEntry.Gui.Tooltip
    public boolean chatCoinEnabled;

    public ExpHudConfig() {
        expHudEnabled = true; // Should the Hud Render
        coinEnabledExpHud = true; // Show Coin In Hud
        chatExpEnabled = true; // remove Exp from Chat?
        chatCoinEnabled = true; // remove Coins from chat?
    }
}
