package dev.jb0s.blockgameenhanced.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ActionResult;

public interface ClientScreenChanged {
    Event<ClientScreenChanged> EVENT = EventFactory.createArrayBacked(ClientScreenChanged.class, (listeners) -> (client, newScreen) -> {
        for (ClientScreenChanged listener : listeners) {
            listener.onScreenChanged(client, newScreen);
        }
    });

    void onScreenChanged(MinecraftClient client, Screen newScreen);
}
