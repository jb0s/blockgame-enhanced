package dev.jb0s.blockgameenhanced.mixin.renderer.item;

import com.google.gson.Gson;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.manager.mmoitems.MMOItemsAbility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
    //@Shadow public float zOffset;

    //@Shadow protected abstract void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

    private static final Gson gson = new Gson();

    /*@Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        NbtCompound nbt = stack.getOrCreateNbt();

        // Render MMOItems cooldown
        String tag = nbt.getString("MMOITEMS_ABILITY");
        if(tag != null) {
            MMOItemsAbility[] itemAbilities = gson.fromJson(tag, MMOItemsAbility[].class);
            if(itemAbilities != null && itemAbilities.length > 0) {
                float cooldownProgressForThisStack = BlockgameEnhancedClient.getMmoItemsManager().getCooldownProgress(itemAbilities[0].Id, MinecraftClient.getInstance().getTickDelta());
                if (cooldownProgressForThisStack > 0.0f) {
                    RenderSystem.disableDepthTest();
                    RenderSystem.disableTexture();
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    Tessellator tessellator2 = Tessellator.getInstance();
                    BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
                    renderGuiQuad(bufferBuilder2, x, y + MathHelper.floor(16.0f * (1.0f - cooldownProgressForThisStack)), 16, MathHelper.ceil(16.0f * cooldownProgressForThisStack), 255, 255, 255, 127);
                    RenderSystem.enableTexture();
                    RenderSystem.enableDepthTest();
                }
            }
        }

        if(BlockgameEnhanced.isNotkerMmoPresent()) {
            // Compatibility with Notker's McMMO Item Durability viewer.
            return;
        }


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
    }*/
}
