package dev.jb0s.blockgameenhanced.mixin.items;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixinItem {
    private static final String NBT_DURABILITY = "MMOITEMS_DURABILITY";
    private static final String NBT_MAX_DURABILITY = "MMOITEMS_MAX_DURABILITY";

    @Inject(method = "getItemBarStep", at = @At("HEAD"), cancellable = true)
    public void getItemBarStep(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if(BlockgameEnhanced.isNotkerMmoPresent()) {
            // Compatibility with Notker's McMMO Item Durability viewer.
            return;
        }

        NbtCompound nbt = stack.getNbt();

        if(nbt != null && nbt.getInt(NBT_MAX_DURABILITY) > 0) {
            cir.setReturnValue(Math.round(13.0f - (float)stack.getDamage() * 13.0f / (float)stack.getMaxDamage()));
            cir.cancel();
        }
    }

    @Inject(method = "getItemBarColor", at = @At("HEAD"), cancellable = true)
    public void getItemBarColor(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if(BlockgameEnhanced.isNotkerMmoPresent()) {
            // Compatibility with Notker's McMMO Item Durability viewer.
            return;
        }

        NbtCompound nbt = stack.getOrCreateNbt();

        if(nbt != null && nbt.getInt(NBT_MAX_DURABILITY) > 0) {
            float maxDamage = (float) nbt.getInt(NBT_MAX_DURABILITY);
            float damage = (float)(nbt.getInt(NBT_MAX_DURABILITY) - nbt.getInt(NBT_DURABILITY));

            float f = Math.max(0.0f, (maxDamage - damage) / maxDamage);
            cir.setReturnValue(MathHelper.hsvToRgb(f / 3.0f, 1.0f, 1.0f));
            cir.cancel();
        }
    }
}
