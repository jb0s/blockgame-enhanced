package dev.jb0s.blockgameenhanced.gui.screen.title;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;

public class FakeClientPlayNetHandler extends ClientPlayNetworkHandler {
    public FakeClientPlayNetHandler() {
        super(MinecraftClient.getInstance(), MinecraftClient.getInstance().currentScreen, new ClientConnection(NetworkSide.CLIENTBOUND), MinecraftClient.getInstance().getSession().getProfile(), null);
    }
}