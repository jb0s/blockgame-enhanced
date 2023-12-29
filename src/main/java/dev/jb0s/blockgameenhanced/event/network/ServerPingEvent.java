package dev.jb0s.blockgameenhanced.event.network;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ServerPingEvent {
    Event<ServerPingEvent> EVENT = EventFactory.createArrayBacked(ServerPingEvent.class, (listeners) -> (id) -> {
        for (ServerPingEvent listener : listeners) {
            listener.serverPing(id);
        }
    });

    void serverPing(int id);
}
