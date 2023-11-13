package dev.jb0s.blockgameenhanced.event.gamefeature.party;

import dev.jb0s.blockgameenhanced.gamefeature.party.PartyGameFeature;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PartyPingEvent {
    Event<PartyPingEvent> EVENT = EventFactory.createArrayBacked(PartyPingEvent.class, (listeners) -> (gameFeature) -> {
        for (PartyPingEvent listener : listeners) {
            listener.partyPingEvent(gameFeature);
        }
    });

    void partyPingEvent(PartyGameFeature gameFeature);
}
