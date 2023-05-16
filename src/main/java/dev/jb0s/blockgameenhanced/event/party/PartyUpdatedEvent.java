package dev.jb0s.blockgameenhanced.event.party;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PartyUpdatedEvent {
    Event<PartyUpdatedEvent> EVENT = EventFactory.createArrayBacked(PartyUpdatedEvent.class, (listeners) -> (partySize) -> {
        for (PartyUpdatedEvent listener : listeners) {
            listener.partyUpdated(partySize);
        }
    });

    void partyUpdated(int partySize);
}
