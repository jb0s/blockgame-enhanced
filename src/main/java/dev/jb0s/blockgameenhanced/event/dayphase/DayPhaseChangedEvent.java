package dev.jb0s.blockgameenhanced.event.dayphase;

import dev.jb0s.blockgameenhanced.gamefeature.dayphase.DayPhase;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface DayPhaseChangedEvent {
    Event<DayPhaseChangedEvent> EVENT = EventFactory.createArrayBacked(DayPhaseChangedEvent.class, (listeners) -> (dayPhase) -> {
        for (DayPhaseChangedEvent listener : listeners) {
            listener.dayPhaseChanged(dayPhase);
        }
    });

    void dayPhaseChanged(DayPhase dayPhase);
}
