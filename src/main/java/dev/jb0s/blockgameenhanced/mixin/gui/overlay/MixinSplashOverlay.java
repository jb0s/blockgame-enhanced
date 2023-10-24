package dev.jb0s.blockgameenhanced.mixin.gui.overlay;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public class MixinSplashOverlay {
    @Shadow private long reloadCompleteTime;
    private boolean hasDelayedStartupFinishingBefore;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceReload;isComplete()Z"), cancellable = true)
    public void renderCancelling(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        boolean isDoingServerOnStartup = BlockgameEnhancedClient.isRunningCompatibilityServer() && !BlockgameEnhancedClient.isCompatibilityServerReady();

        // Don't let the startup finish if we personally are not done yet
        if(isDoingServerOnStartup) {
            ci.cancel();
            hasDelayedStartupFinishingBefore = true;
        }
        else if(hasDelayedStartupFinishingBefore) {
            reloadCompleteTime = Util.getMeasuringTimeMs();
        }
    }
}
