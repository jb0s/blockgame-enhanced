package dev.jb0s.blockgameenhanced.event.adventurezone;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public interface EnteredWildernessEvent {
    Event<EnteredWildernessEvent> EVENT = EventFactory.createArrayBacked(EnteredWildernessEvent.class, (listeners) -> (client, playerEntity) -> {
        for (EnteredWildernessEvent listener : listeners) {
            listener.enteredWilderness(client, playerEntity);
        }
    });

    void enteredWilderness(MinecraftClient client, ClientPlayerEntity playerEntity);
}
