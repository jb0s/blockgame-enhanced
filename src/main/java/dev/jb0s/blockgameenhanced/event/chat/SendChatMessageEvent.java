package dev.jb0s.blockgameenhanced.event.chat;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;

public interface SendChatMessageEvent {
    Event<SendChatMessageEvent> EVENT = EventFactory.createArrayBacked(SendChatMessageEvent.class, (listeners) -> (player, message) -> {
        for (SendChatMessageEvent listener : listeners) {
            listener.sendChatMessage(player, message);
        }
    });

    void sendChatMessage(ClientPlayerEntity client, String message);
}
