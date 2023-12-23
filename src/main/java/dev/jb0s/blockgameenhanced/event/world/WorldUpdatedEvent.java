package dev.jb0s.blockgameenhanced.event.world;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.World;

public interface WorldUpdatedEvent {
    Event<WorldUpdatedEvent> EVENT = EventFactory.createArrayBacked(WorldUpdatedEvent.class, (listeners) -> (world) -> {
        for (WorldUpdatedEvent listener : listeners) {
            listener.worldUpdated(world);
        }
    });

    void worldUpdated(World world);
}
