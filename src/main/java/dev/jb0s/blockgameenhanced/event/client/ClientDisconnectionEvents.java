package dev.jb0s.blockgameenhanced.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

public class ClientDisconnectionEvents {

    /**
     * An event fired right before the client handles disconnection.
     */
    public static final Event<PreDisconnect> PRE_DISCONNECT = EventFactory.createArrayBacked(PreDisconnect.class, (listeners) -> (client) -> {
        for (PreDisconnect listener : listeners) {
            listener.onClientPreDisconnect(client);
        }
    });

    /**
     * An event fired right after the client has handled disconnection.
     */
    public static final Event<PostDisconnect> POST_DISCONNECT = EventFactory.createArrayBacked(PostDisconnect.class, (listeners) -> (client) -> {
        for (PostDisconnect listener : listeners) {
            listener.onClientPostDisconnect(client);
        }
    });

    @FunctionalInterface
    public interface PreDisconnect {
        void onClientPreDisconnect(MinecraftClient client);
    }

    @FunctionalInterface
    public interface PostDisconnect {
        void onClientPostDisconnect(MinecraftClient client);
    }
}
