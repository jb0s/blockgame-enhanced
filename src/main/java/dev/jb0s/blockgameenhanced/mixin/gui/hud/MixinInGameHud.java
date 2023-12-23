package dev.jb0s.blockgameenhanced.mixin.gui.hud;

import dev.jb0s.blockgameenhanced.gui.hud.FadeHud;
import dev.jb0s.blockgameenhanced.gui.hud.PartyHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud extends DrawableHelper {
    @Shadow @Final private MinecraftClient client;
    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("RETURN"))
    public void postRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        // Render HUDs
        PartyHud.render(matrices, tickDelta);

        // Fade-in effect in the first second of lifetime
        if(client.player != null && client.player.age <= 20) {
            FadeHud.render(matrices, client);
        }
    }
}
