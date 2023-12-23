package dev.jb0s.blockgameenhanced.event.entity.otherplayer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;

public interface OtherPlayerTickEvent {
    Event<OtherPlayerTickEvent> EVENT = EventFactory.createArrayBacked(OtherPlayerTickEvent.class, (listeners) -> (client, otherPlayer) -> {
        for (OtherPlayerTickEvent listener : listeners) {
            listener.otherPlayerTick(client, otherPlayer);
        }
    });

    void otherPlayerTick(MinecraftClient client, OtherClientPlayerEntity otherPlayer);
}
