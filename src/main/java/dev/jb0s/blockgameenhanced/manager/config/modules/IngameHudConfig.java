package dev.jb0s.blockgameenhanced.manager.config.modules;

import dev.jb0s.blockgameenhanced.manager.config.structure.IngameHudStyle;
import dev.jb0s.blockgameenhanced.manager.config.structure.PartyHudPosition;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "ingame_hud")
public class IngameHudConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean enableCustomHud;

    @ConfigEntry.Gui.Tooltip
    public IngameHudStyle style;

    public IngameHudConfig() {
        enableCustomHud = true;
        style = IngameHudStyle.MODERN;
    }
}