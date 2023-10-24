package dev.jb0s.blockgameenhanced.mixin.client;


import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.helper.MMOItemHelper;
import dev.jb0s.blockgameenhanced.manager.config.modules.AdvancedModifierHighlighterConfig;
import dev.jb0s.blockgameenhanced.manager.config.modules.ModifierHighlighterConfig;
import dev.jb0s.blockgameenhanced.manager.config.structure.MMOItemsItemTypes;
import dev.jb0s.blockgameenhanced.manager.config.structure.MMOItemModifiers;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow public float zOffset;


    @Inject(at = @At("HEAD"), method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {

        ModifierHighlighterConfig config = BlockgameEnhanced.getConfig().getModifierHighlighterConfig();
        AdvancedModifierHighlighterConfig advancedConig = config.getAdvancedModifierHighlighterConfig();
        // Check if the config is available and Enabled and stack size is 1
        if (config.modifiersEnabled && !stack.isEmpty() && stack.getCount() == 1) {
            NbtCompound nbt = stack.getOrCreateNbt();

            // Checks which item Type to compare
            if (config.itemTypes == MMOItemsItemTypes.ALL ||
                nbt.getString(MMOItemHelper.NBT_ITEM_TYPE).equals(config.itemTypes.tag())) {

                byte[] results = updateTagMatches(nbt, config.runeTag0, config.rRuneValue0,
                                    updateTagMatches(nbt, config.runeTag1, config.rRuneValue1,
                                        updateTagMatches(nbt, config.runeTag2, config.rRuneValue2,
                                            updateTagMatches(nbt, config.runeTag3, config.rRuneValue3,
                                                updateTagMatches(nbt, config.runeTag4, config.rRuneValue4, new byte[2])))));

                // [0]Tags to match | [1]Tags Matched
                if (results[0] == results[1] && results[0] > 0 ) {
                    String string = advancedConig.modifierHighlight;
                    MatrixStack matrixStack = new MatrixStack();
                    float scale = advancedConig.modifierHighlightScale;

                    matrixStack.scale(scale, scale, 1);
                    matrixStack.translate(0.0D, 0.0D, (this.zOffset + 200.0F));

                    VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
                    renderer.drawWithOutline(new LiteralText(string).asOrderedText(),
                            (x / scale) + 17 - renderer.getWidth(string) + advancedConig.modifierHighlightXOffset,
                            (y / scale) + 9 + advancedConig.modifierHighlightYOffset,
                            advancedConig.modifierHighlightColor,
                            0,
                            matrixStack.peek().getPositionMatrix(),
                            immediate,
                            15728880
                            );
                    immediate.draw();
                }
            }
        }
    }

    private byte[] updateTagMatches(NbtCompound nbt, MMOItemModifiers mmoItemModifiers, float minValue, byte[] result) {
        // NONE = ignore
        if (mmoItemModifiers == MMOItemModifiers.NONE) return result;
        // Tag Present -> TagsToMatch++
        result[0]++;
        // Attribute not found on Item
        if (!nbt.contains(mmoItemModifiers.tag())) return result;
        // Match - Value found and over/equal Threshold -> TagsMatched++
        if (nbt.getFloat(mmoItemModifiers.tag()) >= minValue)  result[1]++;
        // Match found but under Threshold
        return result;
    }
}
