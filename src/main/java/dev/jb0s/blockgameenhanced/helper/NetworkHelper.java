package dev.jb0s.blockgameenhanced.helper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;

public class NetworkHelper {
    public static int getNetworkLatency(PlayerEntity playerEntity) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerListEntry entry = Objects.requireNonNull(client.getNetworkHandler()).getPlayerListEntry(playerEntity.getUuid());

        if(entry != null) {
            return entry.getLatency();
        }

        return -1;
    }
}
