package dev.jb0s.blockgameenhanced.event.adventurezone;

import dev.jb0s.blockgameenhanced.gamefeature.zone.Zone;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public interface PlayerEnteredZoneEvent {
    Event<PlayerEnteredZoneEvent> EVENT = EventFactory.createArrayBacked(PlayerEnteredZoneEvent.class, (listeners) -> (client, playerEntity, zone) -> {
        for (PlayerEnteredZoneEvent listener : listeners) {
            listener.enteredZone(client, playerEntity, zone);
        }
    });

    void enteredZone(MinecraftClient client, ClientPlayerEntity playerEntity, Zone zone);
}
