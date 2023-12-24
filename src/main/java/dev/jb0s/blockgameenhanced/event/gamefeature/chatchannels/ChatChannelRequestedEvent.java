package dev.jb0s.blockgameenhanced.event.gamefeature.chatchannels;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ChatChannelRequestedEvent {
    Event<ChatChannelRequestedEvent> EVENT = EventFactory.createArrayBacked(ChatChannelRequestedEvent.class, (listeners) -> () -> {
        for (ChatChannelRequestedEvent listener : listeners) {
            listener.chatChannelRequested();
        }
    });

    void chatChannelRequested();
}
