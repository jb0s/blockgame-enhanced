package dev.jb0s.blockgameenhanced.event.chat;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public interface ReceiveChatMessageEvent {
    Event<ReceiveChatMessageEvent> EVENT = EventFactory.createArrayBacked(ReceiveChatMessageEvent.class, (listeners) -> (client, message) -> {
        for (ReceiveChatMessageEvent listener : listeners) {
            ActionResult result = listener.receiveChatMessage(client, message);

            if(result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    ActionResult receiveChatMessage(MinecraftClient client, String message);
}
