package dev.jb0s.blockgameenhanced.event.screen;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;

public interface ScreenInitEvent {
    Event<ScreenInitEvent> BEGIN = EventFactory.createArrayBacked(ScreenInitEvent.class, (listeners) -> (screen) -> {
        for (ScreenInitEvent listener : listeners) {
            listener.screenInit(screen);
        }
    });

    Event<ScreenInitEvent> END = EventFactory.createArrayBacked(ScreenInitEvent.class, (listeners) -> (screen) -> {
        for (ScreenInitEvent listener : listeners) {
            listener.screenInit(screen);
        }
    });

    void screenInit(Screen screen);
}
