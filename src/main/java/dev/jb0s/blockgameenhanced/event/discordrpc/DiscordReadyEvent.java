package dev.jb0s.blockgameenhanced.event.discordrpc;

import com.jagrosh.discordipc.IPCClient;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface DiscordReadyEvent {
    Event<DiscordReadyEvent> EVENT = EventFactory.createArrayBacked(DiscordReadyEvent.class, (listeners) -> (client) -> {
        for (DiscordReadyEvent listener : listeners) {
            listener.ready(client);
        }
    });

    void ready(IPCClient client);
}
