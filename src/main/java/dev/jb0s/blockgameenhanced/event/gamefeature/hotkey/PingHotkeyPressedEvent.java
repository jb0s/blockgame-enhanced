package dev.jb0s.blockgameenhanced.event.gamefeature.hotkey;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

public interface PingHotkeyPressedEvent {
    Event<PingHotkeyPressedEvent> EVENT = EventFactory.createArrayBacked(PingHotkeyPressedEvent.class, (listeners) -> (client) -> {
        for (PingHotkeyPressedEvent listener : listeners) {
            listener.pingHotkeyPressed(client);
        }
    });

    void pingHotkeyPressed(MinecraftClient client);
}
