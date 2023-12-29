package dev.jb0s.blockgameenhanced.event.chat;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;

public interface SendChatMessageEvent {
    Event<SendChatMessageEvent> EVENT = EventFactory.createArrayBacked(SendChatMessageEvent.class, (listeners) -> (networkHandler, message) -> {
        for (SendChatMessageEvent listener : listeners) {
            listener.sendChatMessage(networkHandler, message);
        }
    });

    void sendChatMessage(ClientPlayNetworkHandler networkHandler, String message);
}
