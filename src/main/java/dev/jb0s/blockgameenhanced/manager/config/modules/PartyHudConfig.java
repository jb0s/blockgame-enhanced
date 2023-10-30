package dev.jb0s.blockgameenhanced.manager.config.modules;

import dev.jb0s.blockgameenhanced.manager.config.structure.PartyHudPosition;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "party_hud")
public class PartyHudConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean showHud;

    @ConfigEntry.Gui.Tooltip
    public boolean showSelf;

    @ConfigEntry.Gui.Excluded // this setting is not implemented yet
    @ConfigEntry.Gui.Tooltip
    public PartyHudPosition position;

    @ConfigEntry.Gui.Tooltip
    public boolean deathNotify;

    @ConfigEntry.Gui.Tooltip
    public boolean markNotify;

    @ConfigEntry.Gui.Tooltip
    public boolean outlineMembers;

    public PartyHudConfig() {
        showHud = true;
        showSelf = true;
        position = PartyHudPosition.TOP_LEFT;
        deathNotify = true;
        markNotify = true;
        outlineMembers = true;
    }
}