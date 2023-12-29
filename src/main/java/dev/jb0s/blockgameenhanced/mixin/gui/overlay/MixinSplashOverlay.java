package dev.jb0s.blockgameenhanced.mixin.gui.overlay;

import dev.jb0s.blockgameenhanced.event.splash.SplashRenderEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public class MixinSplashOverlay {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceReload;isComplete()Z"), cancellable = true)
    public void renderCancelling(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        SplashOverlay thisSplash = (SplashOverlay) (Object) this;
        ActionResult res = SplashRenderEvent.EVENT.invoker().render(thisSplash, context, mouseX, mouseY, delta);

        if(res != ActionResult.PASS) {
            ci.cancel();
        }
    }
}
