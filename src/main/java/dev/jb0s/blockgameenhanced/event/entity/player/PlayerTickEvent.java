package dev.jb0s.blockgameenhanced.event.entity.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public interface PlayerTickEvent {
    Event<PlayerTickEvent> EVENT = EventFactory.createArrayBacked(PlayerTickEvent.class, (listeners) -> (client, player) -> {
        for (PlayerTickEvent listener : listeners) {
            listener.playerTick(client, player);
        }
    });

    void playerTick(MinecraftClient client, ClientPlayerEntity player);
}
