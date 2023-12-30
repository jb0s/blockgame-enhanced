package dev.jb0s.blockgameenhanced.gui.screen.title;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.session.telemetry.WorldSession;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;

import java.time.Duration;

public class FakeClientPlayNetHandler extends ClientPlayNetworkHandler {
    public FakeClientPlayNetHandler(ClientConnection con) {
        super(MinecraftClient.getInstance(), con,
                new ClientConnectionState(MinecraftClient.getInstance().getGameProfile(),
                        new WorldSession(MinecraftClient.getInstance().getTelemetryManager().getSender(), true, Duration.ZERO, "The jakening"),
                        null, null, "", null, null));
    }
}