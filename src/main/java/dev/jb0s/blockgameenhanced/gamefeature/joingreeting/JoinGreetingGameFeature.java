package dev.jb0s.blockgameenhanced.gamefeature.joingreeting;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.TranslatableText;

public class JoinGreetingGameFeature extends GameFeature {

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);

        ClientPlayConnectionEvents.JOIN.register(this::handleJoined);
    }

    private void handleJoined(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient client) {
        if(client.player != null) {
            client.player.sendMessage(new TranslatableText("hud.blockgame.message.welcome.1"), false);
            client.player.sendMessage(new TranslatableText("hud.blockgame.message.welcome.2"), false);
        }
    }
}
