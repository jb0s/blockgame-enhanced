package dev.jb0s.blockgameenhanced.event.screen;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.util.ActionResult;

public interface ScreenOpenedEvent {
    Event<ScreenOpenedEvent> EVENT = EventFactory.createArrayBacked(ScreenOpenedEvent.class, (listeners) -> (packet) -> {
        for (ScreenOpenedEvent listener : listeners) {
            ActionResult x = listener.screenOpened(packet);
            if(x != ActionResult.PASS) return x;
        }
        return ActionResult.PASS;
    });

    ActionResult screenOpened(OpenScreenS2CPacket packet);
}
