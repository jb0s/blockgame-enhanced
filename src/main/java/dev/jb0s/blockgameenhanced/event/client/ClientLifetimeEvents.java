package dev.jb0s.blockgameenhanced.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;

public class ClientLifetimeEvents {

    /**
     * An event for client initialization.
     */
    public static final Event<Init> INIT = EventFactory.createArrayBacked(Init.class, (listeners) -> (client, args) -> {
        for (Init listener : listeners) {
            listener.onClientInit(client, args);
        }
    });

    /**
     * An event for the client getting stopped.
     */
    public static final Event<Stop> STOP = EventFactory.createArrayBacked(Stop.class, (listeners) -> (client) -> {
        for (Stop listener : listeners) {
            listener.onClientStop(client);
        }
    });

    /**
     * An event for the client being closed.
     */
    public static final Event<Close> CLOSE = EventFactory.createArrayBacked(Close.class, (listeners) -> (client) -> {
        for (Close listener : listeners) {
            listener.onClientClose(client);
        }
    });

    @FunctionalInterface
    public interface Init {
        void onClientInit(MinecraftClient client, RunArgs args);
    }

    @FunctionalInterface
    public interface Stop {
        void onClientStop(MinecraftClient client);
    }

    @FunctionalInterface
    public interface Close {
        void onClientClose(MinecraftClient client);
    }
}
