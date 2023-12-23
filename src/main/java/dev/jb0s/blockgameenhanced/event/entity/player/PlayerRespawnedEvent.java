package dev.jb0s.blockgameenhanced.event.entity.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

public interface PlayerRespawnedEvent {
    Event<PlayerRespawnedEvent> EVENT = EventFactory.createArrayBacked(PlayerRespawnedEvent.class, (listeners) -> (client) -> {
        for (PlayerRespawnedEvent listener : listeners) {
            listener.playerRespawned(client);
        }
    });

    void playerRespawned(MinecraftClient client);
}
