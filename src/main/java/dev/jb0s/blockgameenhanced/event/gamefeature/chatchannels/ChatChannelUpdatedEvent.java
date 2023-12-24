package dev.jb0s.blockgameenhanced.event.gamefeature.chatchannels;

import dev.jb0s.blockgameenhanced.gamefeature.chatchannels.ChatChannelsGameFeature;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ChatChannelUpdatedEvent {
    Event<ChatChannelUpdatedEvent> EVENT = EventFactory.createArrayBacked(ChatChannelUpdatedEvent.class, (listeners) -> (gameFeature) -> {
        for (ChatChannelUpdatedEvent listener : listeners) {
            listener.chatChannelUpdatedEvent(gameFeature);
        }
    });

    void chatChannelUpdatedEvent(ChatChannelsGameFeature gameFeature);
}
