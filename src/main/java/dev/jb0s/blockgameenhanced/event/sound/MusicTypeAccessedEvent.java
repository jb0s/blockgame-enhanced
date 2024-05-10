package dev.jb0s.blockgameenhanced.event.sound;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.sound.MusicSound;

public interface MusicTypeAccessedEvent {
    Event<MusicTypeAccessedEvent> EVENT = EventFactory.createArrayBacked(MusicTypeAccessedEvent.class, (listeners) -> () -> {
        for (MusicTypeAccessedEvent listener : listeners) {
            var sound = listener.musicTypeAccessed();
            if (sound != null) return sound;
        }

        return null;
    });

    MusicSound musicTypeAccessed();
}
