package dev.jb0s.blockgameenhanced.event.adventurezone;

import dev.jb0s.blockgameenhanced.gamefeature.zone.Zone;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public interface PlayerExitedZoneEvent {
    Event<PlayerExitedZoneEvent> EVENT = EventFactory.createArrayBacked(PlayerExitedZoneEvent.class, (listeners) -> (client, playerEntity, zone) -> {
        for (PlayerExitedZoneEvent listener : listeners) {
            listener.exitedZone(client, playerEntity, zone);
        }
    });

    void exitedZone(MinecraftClient client, ClientPlayerEntity playerEntity, Zone zone);
}
