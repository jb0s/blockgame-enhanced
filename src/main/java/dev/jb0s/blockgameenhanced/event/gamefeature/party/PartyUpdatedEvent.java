package dev.jb0s.blockgameenhanced.event.gamefeature.party;

import dev.jb0s.blockgameenhanced.gamefeature.party.PartyGameFeature;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PartyUpdatedEvent {
    Event<PartyUpdatedEvent> EVENT = EventFactory.createArrayBacked(PartyUpdatedEvent.class, (listeners) -> (gameFeature) -> {
        for (PartyUpdatedEvent listener : listeners) {
            listener.partyUpdatedEvent(gameFeature);
        }
    });

    void partyUpdatedEvent(PartyGameFeature gameFeature);
}
