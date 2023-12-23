package dev.jb0s.blockgameenhanced.mixin.renderer.entity;

import dev.jb0s.blockgameenhanced.renderer.MenuPlayerRenderers;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityRenderers.class)
public class MixinEntityRenderers {
    @Inject(method = "reloadPlayerRenderers", at = @At("HEAD"))
    private static void onCreatePlayerRenderers(EntityRendererFactory.Context p_174052_, CallbackInfoReturnable<Map<String, EntityRenderer<? extends PlayerEntity>>> cir) {
        MenuPlayerRenderers.createPlayerRenderers(p_174052_);
    }
}
