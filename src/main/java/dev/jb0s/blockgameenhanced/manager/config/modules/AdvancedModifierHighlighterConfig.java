package dev.jb0s.blockgameenhanced.manager.config.modules;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "advanced_modifier_highlighter")
public class AdvancedModifierHighlighterConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public String modifierHighlight;
    @ConfigEntry.Gui.Tooltip
    public float modifierHighlightScale;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.ColorPicker
    public Integer modifierHighlightColor;
    @ConfigEntry.Gui.Tooltip
    public float modifierHighlightXOffset;
    @ConfigEntry.Gui.Tooltip
    public float modifierHighlightYOffset;


    public AdvancedModifierHighlighterConfig() {
        modifierHighlight = "★"; // Highlight Text"
        modifierHighlightScale = 1.0f; // Highlight Text Scale
        modifierHighlightColor = 0xFFEE00; // Highlight Color
        modifierHighlightXOffset = -11.0f; // Horizontal Offset For The Attribute Highlight
        modifierHighlightYOffset = -11.0f; // Vertical Offset For The Attribute Highlight
    }
}