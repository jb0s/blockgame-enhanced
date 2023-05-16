package dev.jb0s.blockgameenhanced.mixin.gui.screen;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class MixinDeathScreen extends Screen {
    protected MixinDeathScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    public void init(CallbackInfo ci) {
        // todo: Ideally the custom death screen should also show with this setting turned off. We should add a respawn button to it.
        boolean shouldAutoRespawn = BlockgameEnhanced.getConfig().getAccessibilityConfig().enableAutoRespawn;
        if(shouldAutoRespawn) {
            client.setScreen(new dev.jb0s.blockgameenhanced.gui.screen.DeathScreen());
            ci.cancel();
        }
    }
}
