package dev.jb0s.blockgameenhanced.event.screen;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;

public interface ScreenReceivedInventoryEvent {
    Event<ScreenReceivedInventoryEvent> EVENT = EventFactory.createArrayBacked(ScreenReceivedInventoryEvent.class, (listeners) -> (packet) -> {
        for (ScreenReceivedInventoryEvent listener : listeners) {
            listener.screenReceivedInventory(packet);
        }
    });

    void screenReceivedInventory(InventoryS2CPacket packet);
}
