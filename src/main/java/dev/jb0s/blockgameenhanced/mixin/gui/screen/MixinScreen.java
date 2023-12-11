package dev.jb0s.blockgameenhanced.mixin.gui.screen;

import dev.jb0s.blockgameenhanced.event.screen.ScreenInitEvent;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MixinScreen {
    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        Screen thisScreen = (Screen) (Object) this;
        ScreenInitEvent.BEGIN.invoker().screenInit(thisScreen);
    }

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("RETURN"))
    public void postInit(CallbackInfo ci) {
        Screen thisScreen = (Screen) (Object) this;
        ScreenInitEvent.END.invoker().screenInit(thisScreen);
    }
}
