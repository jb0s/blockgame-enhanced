package dev.jb0s.blockgameenhanced.event.bossbattle;

import dev.jb0s.blockgameenhanced.manager.adventure.AdventureZoneBoss;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface BossBattleCommencedEvent {
    Event<BossBattleCommencedEvent> EVENT = EventFactory.createArrayBacked(BossBattleCommencedEvent.class, (listeners) -> (boss) -> {
        for (BossBattleCommencedEvent listener : listeners) {
            listener.bossBattleCommenced(boss);
        }
    });

    void bossBattleCommenced(AdventureZoneBoss boss);
}
