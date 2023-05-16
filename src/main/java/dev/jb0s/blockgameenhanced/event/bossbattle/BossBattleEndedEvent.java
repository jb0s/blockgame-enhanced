package dev.jb0s.blockgameenhanced.event.bossbattle;

import dev.jb0s.blockgameenhanced.manager.adventure.AdventureZoneBoss;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface BossBattleEndedEvent {
    Event<BossBattleEndedEvent> EVENT = EventFactory.createArrayBacked(BossBattleEndedEvent.class, (listeners) -> (boss) -> {
        for (BossBattleEndedEvent listener : listeners) {
            listener.bossBattleEnded(boss);
        }
    });

    void bossBattleEnded(AdventureZoneBoss boss);
}
