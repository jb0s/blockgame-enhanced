package dev.jb0s.blockgameenhanced.gamefeature;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;

public abstract class GameFeature {
    @Getter
    private MinecraftClient minecraftClient;

    @Getter
    private BlockgameEnhancedClient blockgameClient;

    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        this.minecraftClient = minecraftClient;
        this.blockgameClient = blockgameClient;
    }

    public void tick() {
    }

    public boolean isEnabled() {
        return true;
    }
}
