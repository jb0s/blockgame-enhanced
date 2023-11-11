package dev.jb0s.blockgameenhanced.event.chat;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public interface ReceiveFormattedChatMessageEvent {
    Event<ReceiveFormattedChatMessageEvent> EVENT = EventFactory.createArrayBacked(ReceiveFormattedChatMessageEvent.class, (listeners) -> (client, formattedMessage) -> {
        for (ReceiveFormattedChatMessageEvent listener : listeners) {
            ActionResult result = listener.receiveFormattedChatMessage(client, formattedMessage);

            if(result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    ActionResult receiveFormattedChatMessage(MinecraftClient client, Text formattedMessage);
}
