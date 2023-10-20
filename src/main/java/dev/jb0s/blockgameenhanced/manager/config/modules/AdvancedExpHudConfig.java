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
    public MessageType MESSAGE_TYPE;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public String EXP_CHAT_TAG;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public String COIN_CHAT_TAG;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public String COIN_QUEST_CHAT_TAG;

    public AdvancedExpHudConfig() {
        MESSAGE_TYPE = MessageType.SYSTEM; // Message types: CHAT, SYSTEM, GAME_INFO
        EXP_CHAT_TAG = "[EXP]"; // Chat Message Contains Text
        COIN_CHAT_TAG = " Coin."; // Chat Message Ends With Text
        COIN_QUEST_CHAT_TAG = " Coin!"; // Chat Message Ends With Text
    }
}