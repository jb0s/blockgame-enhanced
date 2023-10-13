package dev.jb0s.blockgameenhanced.mixin.renderer.item;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @Shadow public float zOffset;

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        if(BlockgameEnhanced.isNotkerMmoPresent()) {
            // Compatibility with Notker's McMMO Item Durability viewer.
            return;
        }

        NbtCompound nbt = stack.getOrCreateNbt();

        MatrixStack matrixStack = new MatrixStack();
        if(nbt.getInt("MMOITEMS_MAX_CONSUME") != 0 && stack.getCount() == 1) {
            String chargeCountString = countLabel == null ? String.valueOf(nbt.getInt("MMOITEMS_MAX_CONSUME")) : countLabel;
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

            matrixStack.translate(0.0, 0.0, (zOffset + 200.0f));

            // Shitty outline (Notch did it first!)
            renderer.draw(chargeCountString, (float)(x + 19 - 2 - renderer.getWidth(chargeCountString)) + 1, (float)(y + 6 + 3), 0x000000, false, matrixStack.peek().getPositionMatrix(), immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            renderer.draw(chargeCountString, (float)(x + 19 - 2 - renderer.getWidth(chargeCountString)) - 1, (float)(y + 6 + 3), 0x000000, false, matrixStack.peek().getPositionMatrix(), immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            renderer.draw(chargeCountString, (float)(x + 19 - 2 - renderer.getWidth(chargeCountString)), (float)(y + 6 + 3) + 1, 0x000000, false, matrixStack.peek().getPositionMatrix(), immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            renderer.draw(chargeCountString, (float)(x + 19 - 2 - renderer.getWidth(chargeCountString)), (float)(y + 6 + 3) - 1, 0x000000, false, matrixStack.peek().getPositionMatrix(), immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);

            matrixStack.translate(0.0, 0.0, 0.001f);
            renderer.draw(chargeCountString, (float)(x + 19 - 2 - renderer.getWidth(chargeCountString)), (float)(y + 6 + 3), 0x7EFC20, false, matrixStack.peek().getPositionMatrix(), immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);

            immediate.draw();
            ci.cancel();
        }
    }
}
