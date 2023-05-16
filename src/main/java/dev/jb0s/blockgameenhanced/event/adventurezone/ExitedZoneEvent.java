package dev.jb0s.blockgameenhanced.event.adventurezone;

import dev.jb0s.blockgameenhanced.manager.adventure.AdventureZone;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public interface ExitedZoneEvent {
    Event<ExitedZoneEvent> EVENT = EventFactory.createArrayBacked(ExitedZoneEvent.class, (listeners) -> (client, playerEntity, zone) -> {
        for (ExitedZoneEvent listener : listeners) {
            listener.exitedZone(client, playerEntity, zone);
        }
    });

    void exitedZone(MinecraftClient client, ClientPlayerEntity playerEntity, AdventureZone zone);
}
