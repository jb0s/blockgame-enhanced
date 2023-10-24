package dev.jb0s.blockgameenhanced.manager.config.modules;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.network.MessageType;

@Config(name = "advanced_exp_hud")
public class AdvancedExpHudConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public MessageType chatMessageType;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public String expChatTag;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public String coinChatTag;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public String coinQuestChatTag;

    public AdvancedExpHudConfig() {
        chatMessageType = MessageType.SYSTEM; // Message types: CHAT, SYSTEM, GAME_INFO
        expChatTag = "[EXP]"; // Chat Message Contains Text
        coinChatTag = " Coin."; // Chat Message Ends With Text
        coinQuestChatTag = " Coin!"; // Chat Message Ends With Text
    }
}