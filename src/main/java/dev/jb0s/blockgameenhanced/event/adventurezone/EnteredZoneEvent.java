package dev.jb0s.blockgameenhanced.event.adventurezone;

import dev.jb0s.blockgameenhanced.manager.adventure.AdventureZone;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public interface EnteredZoneEvent {
    Event<EnteredZoneEvent> EVENT = EventFactory.createArrayBacked(EnteredZoneEvent.class, (listeners) -> (client, playerEntity, zone) -> {
        for (EnteredZoneEvent listener : listeners) {
            listener.enteredZone(client, playerEntity, zone);
        }
    });

    void enteredZone(MinecraftClient client, ClientPlayerEntity playerEntity, AdventureZone zone);
}
