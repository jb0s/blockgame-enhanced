package dev.jb0s.blockgameenhanced.gamefeature.hotkey;

import net.minecraft.client.MinecraftClient;

public interface HotkeyAction {
    void pressed(MinecraftClient client);
}