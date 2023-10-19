package dev.jb0s.blockgameenhanced.event.screen;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;

public interface ScreenOpenedEvent {
    Event<ScreenOpenedEvent> EVENT = EventFactory.createArrayBacked(ScreenOpenedEvent.class, (listeners) -> (packet) -> {
        for (ScreenOpenedEvent listener : listeners) {
            listener.screenOpened(packet);
        }
    });

    void screenOpened(OpenScreenS2CPacket packet);
}
