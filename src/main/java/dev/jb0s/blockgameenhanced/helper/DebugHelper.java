package dev.jb0s.blockgameenhanced.helper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class DebugHelper {
    public static void debugMessage(String message) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player == null) {
            return;
        }

        String prefix = "[§b§lDEBUG§r] §7";
        Text textMsg = Text.of(prefix + message);
        player.sendMessage(textMsg, false);
    }
}
