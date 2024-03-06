package dev.jb0s.blockgameenhanced.config.modules;

import lombok.Getter;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(name = "blockgameenhanced")
public class ModConfig extends PartitioningSerializer.GlobalData {
    @Getter
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    IngameHudConfig ingameHudConfig = new IngameHudConfig();

    @Getter
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    PartyHudConfig partyHudConfig = new PartyHudConfig();

    @Getter
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    PrivacyConfig privacyConfig = new PrivacyConfig();

    @Getter
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    AccessibilityConfig accessibilityConfig = new AccessibilityConfig();

    @Getter
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    ChatChannelsConfig chatChannelsConfig = new ChatChannelsConfig();

    @Getter
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    ServerConfig serverConfig = new ServerConfig();
}
