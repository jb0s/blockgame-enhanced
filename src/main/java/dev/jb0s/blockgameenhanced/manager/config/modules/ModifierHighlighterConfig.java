package dev.jb0s.blockgameenhanced.manager.config.modules;

import dev.jb0s.blockgameenhanced.manager.config.structure.MMOItemModifiers;
import dev.jb0s.blockgameenhanced.manager.config.structure.MMOItemsItemTypes;
import lombok.Getter;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "modifier_highlighter")
public class ModifierHighlighterConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean modifiersEnabled;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public MMOItemsItemTypes itemTypes;

    @ConfigEntry.Gui.Tooltip
    public MMOItemModifiers runeTag0;
    @ConfigEntry.Gui.Tooltip
    public float rRuneValue0;
    @ConfigEntry.Gui.Tooltip
    public MMOItemModifiers runeTag1;
    @ConfigEntry.Gui.Tooltip
    public float rRuneValue1;
    @ConfigEntry.Gui.Tooltip
    public MMOItemModifiers runeTag2;
    @ConfigEntry.Gui.Tooltip
    public float rRuneValue2;
    @ConfigEntry.Gui.Tooltip
    public MMOItemModifiers runeTag3;
    @ConfigEntry.Gui.Tooltip
    public float rRuneValue3;
    @ConfigEntry.Gui.Tooltip
    public MMOItemModifiers runeTag4;
    @ConfigEntry.Gui.Tooltip
    public float rRuneValue4;

    @Getter
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = false)
    AdvancedModifierHighlighterConfig advancedModifierHighlighterConfig = new AdvancedModifierHighlighterConfig();


    public ModifierHighlighterConfig() {
        modifiersEnabled = true;
        itemTypes = MMOItemsItemTypes.RUNES;
        runeTag0 = MMOItemModifiers.NONE;
        rRuneValue0 = 0f;
        runeTag1 = MMOItemModifiers.NONE;
        rRuneValue1 = 0f;
        runeTag2 = MMOItemModifiers.NONE;
        rRuneValue2 = 0f;
        runeTag3 = MMOItemModifiers.NONE;
        rRuneValue3 = 0f;
        runeTag4 = MMOItemModifiers.NONE;
        rRuneValue4 = 0f;
    }
}
