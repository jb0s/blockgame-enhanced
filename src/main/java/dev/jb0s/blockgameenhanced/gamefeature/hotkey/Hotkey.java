package dev.jb0s.blockgameenhanced.gamefeature.hotkey;

import lombok.Getter;
import net.minecraft.client.option.KeyBinding;

public record Hotkey(@Getter KeyBinding keyBinding, @Getter HotkeyAction action) {
}
