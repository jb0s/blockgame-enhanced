package dev.jb0s.blockgameenhanced.mixin.client;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Keyboard.class)
public class MixinKeyboard {
    @Inject(method = "processF3", at = @At("HEAD"), cancellable = true)
    public void prevent(int key, CallbackInfoReturnable<Boolean> cir) {
        if(key == 84) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
