package dev.jb0s.blockgameenhanced.event.gamefeature.mmostats;

import dev.jb0s.blockgameenhanced.gamefeature.mmostats.MMOStatsGameFeature;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface MMOStatsUpdatedEvent {
    Event<MMOStatsUpdatedEvent> EVENT = EventFactory.createArrayBacked(MMOStatsUpdatedEvent.class, (listeners) -> (gameFeature) -> {
        for (MMOStatsUpdatedEvent listener : listeners) {
            listener.mmoStatsUpdated(gameFeature);
        }
    });

    void mmoStatsUpdated(MMOStatsGameFeature gameFeature);
}
