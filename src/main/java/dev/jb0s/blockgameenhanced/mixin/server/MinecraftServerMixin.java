package dev.jb0s.blockgameenhanced.mixin.server;

import dev.jb0s.blockgameenhanced.event.server.ServerPrepareStartRegionEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(method = "prepareStartRegion", at = @At("HEAD"), cancellable = true)
    void prepareStartRegion(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        MinecraftServer thisMinecraft = (MinecraftServer) (Object) this;
        ServerPrepareStartRegionEvent.EVENT.invoker().prepareStartRegion(thisMinecraft, worldGenerationProgressListener);
    }
}
