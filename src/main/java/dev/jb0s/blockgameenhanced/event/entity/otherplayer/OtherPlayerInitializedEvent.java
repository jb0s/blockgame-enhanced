package dev.jb0s.blockgameenhanced.event.entity.otherplayer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;

public interface OtherPlayerInitializedEvent {
    Event<OtherPlayerInitializedEvent> EVENT = EventFactory.createArrayBacked(OtherPlayerInitializedEvent.class, (listeners) -> (client, otherPlayer) -> {
        for (OtherPlayerInitializedEvent listener : listeners) {
            listener.otherPlayerInitialized(client, otherPlayer);
        }
    });

    void otherPlayerInitialized(MinecraftClient client, OtherClientPlayerEntity otherPlayer);
}
