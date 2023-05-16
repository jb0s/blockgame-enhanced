package dev.jb0s.blockgameenhanced.mixin.items;

import dev.jb0s.blockgameenhanced.helper.MMOItemHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @Inject(method = "isDamageable", at = @At("HEAD"), cancellable = true)
    public void isDamageable(CallbackInfoReturnable<Boolean> cir) {
        ItemStack thisItemStack = (ItemStack) (Object) this;

        if(MMOItemHelper.hasMMODurability(thisItemStack)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    public void getMaxDamage(CallbackInfoReturnable<Integer> cir) {
        ItemStack thisItemStack = (ItemStack) (Object) this;

        if(MMOItemHelper.hasMMODurability(thisItemStack)) {
            cir.setReturnValue(MMOItemHelper.getMMOMaxDurability(thisItemStack));
            cir.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getDamage", cancellable = true)
    public void getDamage(CallbackInfoReturnable<Integer> cir) {
        ItemStack thisItemStack = (ItemStack) (Object) this;

        if(MMOItemHelper.hasMMODurability(thisItemStack)) {
            cir.setReturnValue(MMOItemHelper.getMMODamage(thisItemStack));
            cir.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "isDamaged", cancellable = true)
    public void isDamaged(CallbackInfoReturnable<Boolean> cir) {
        ItemStack thisItemStack = (ItemStack) (Object) this;

        if(MMOItemHelper.hasMMODurability(thisItemStack)) {
            cir.setReturnValue(MMOItemHelper.getMMODamage(thisItemStack) > 0);
            cir.cancel();
        }
    }
}
