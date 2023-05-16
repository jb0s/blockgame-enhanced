package dev.jb0s.blockgameenhanced.event.chat;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

public interface ReceiveChatMessageEvent {
    Event<ReceiveChatMessageEvent> EVENT = EventFactory.createArrayBacked(ReceiveChatMessageEvent.class, (listeners) -> (client, message) -> {
        for (ReceiveChatMessageEvent listener : listeners) {
            listener.receiveChatMessage(client, message);
        }
    });

    void receiveChatMessage(MinecraftClient client, String message);
}
