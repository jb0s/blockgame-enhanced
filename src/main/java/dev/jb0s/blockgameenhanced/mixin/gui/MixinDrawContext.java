package dev.jb0s.blockgameenhanced.mixin.gui;

import dev.jb0s.blockgameenhanced.event.renderer.item.ItemRendererDrawEvent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class MixinDrawContext {

    @Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.AFTER), cancellable = true)
    public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        DrawContext thisContext = (DrawContext) (Object) this;
        ActionResult result = ItemRendererDrawEvent.EVENT.invoker().drawItem(thisContext, renderer, stack, x, y, countLabel);

        if(result != ActionResult.PASS) {
            ci.cancel();
        }
    }
}
