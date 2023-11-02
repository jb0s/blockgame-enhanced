package dev.jb0s.blockgameenhanced.mixin.gui.hud;

import dev.jb0s.blockgameenhanced.gui.hud.FadeHud;
import dev.jb0s.blockgameenhanced.gui.hud.PartyHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at = @At("RETURN"))
    public void postRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        // Render HUDs
        PartyHud.render(context, tickDelta);

        // Fade-in effect in the first second of lifetime
        if(client.player != null && client.player.age <= 20) {
            FadeHud.render(context, client);
        }
    }
}
