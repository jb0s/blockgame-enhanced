package dev.jb0s.blockgameenhanced.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;

public interface ClientInitEvent {
    Event<ClientInitEvent> EVENT = EventFactory.createArrayBacked(ClientInitEvent.class, (listeners) -> (client, args) -> {
        for (ClientInitEvent listener : listeners) {
            listener.onClientInit(client, args);
        }
    });

    void onClientInit(MinecraftClient client, RunArgs args);
}
