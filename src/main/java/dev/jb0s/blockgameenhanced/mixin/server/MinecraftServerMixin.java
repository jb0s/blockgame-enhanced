package dev.jb0s.blockgameenhanced.mixin.server;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow protected abstract void updateMobSpawnOptions();

    /**
     * Cuts down load time by approximately 7s when running OptiFine compatibility server
     */
    @Inject(method = "prepareStartRegion", at = @At("HEAD"), cancellable = true)
    void prepareStartRegion(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        if (BlockgameEnhancedClient.isRunningCompatibilityServer()) {
            ci.cancel();
            updateMobSpawnOptions();
        }
    }
}
