package dev.jb0s.blockgameenhanced.event.gamefeature.chatchannels;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ChatChannelToggledEvent {
    enum Direction {
        NEXT,
        PREV,
    }

    Event<ChatChannelToggledEvent> EVENT = EventFactory.createArrayBacked(ChatChannelToggledEvent.class, (listeners) -> (direction) -> {
        for (ChatChannelToggledEvent listener : listeners) {
            listener.chatChannelToggled(direction);
        }
    });

    void chatChannelToggled(Direction direction);
}
