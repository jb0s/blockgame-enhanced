package dev.jb0s.blockgameenhanced.mixin.client;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.helper.ExpHudDataHelper;
import dev.jb0s.blockgameenhanced.manager.config.modules.AdvancedExpHudConfig;
import dev.jb0s.blockgameenhanced.manager.config.modules.ExpHudConfig;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "addChatMessage", at = @At("HEAD"), cancellable = true)
    public void addChatMessage(MessageType messageType, Text text, UUID uUID, CallbackInfo ci) {

        ExpHudConfig hudConfig = BlockgameEnhanced.getConfig().getExpHudConfig();

        String expTag = "[EXP]";
        String coinTag = " Coin.";
        String coinQuestTag = " Coin!";

        MessageType type = MessageType.SYSTEM;

        boolean enabled = hudConfig.expHudEnabled;
        boolean hideExp = hudConfig.chatExpEnabled;
        boolean hideCoin = hudConfig.chatCoinEnabled;

        if (enabled && messageType == type){
            String message = text.getString();

            if (message.endsWith(coinTag) || message.endsWith(coinQuestTag)) {
                ExpHudDataHelper.addCoin(message);
                if (hideCoin) ci.cancel();
            }

            if (message.contains(expTag)) {
                ExpHudDataHelper.addExp(message);
                if (hideExp) ci.cancel();
            }
        }

    }
}
