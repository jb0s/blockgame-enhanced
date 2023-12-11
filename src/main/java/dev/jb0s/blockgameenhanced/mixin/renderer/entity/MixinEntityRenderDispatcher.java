package dev.jb0s.blockgameenhanced.mixin.renderer.entity;

import dev.jb0s.blockgameenhanced.gui.screen.title.FakePlayer;
import dev.jb0s.blockgameenhanced.renderer.MenuPlayerRenderers;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {

    @Inject(method = "getRenderer", at = @At("RETURN"), cancellable = true)
    public <T extends Entity> void onGetRenderer(T pEntity, CallbackInfoReturnable<EntityRenderer<? super T>> cir) {
        if (!(pEntity instanceof FakePlayer))
            return;

        String s = ((AbstractClientPlayerEntity) pEntity).getModel();
        PlayerEntityRenderer playerrenderer = null;

        if (s.equals("default"))
            playerrenderer = MenuPlayerRenderers.fakePlayerRenderer;
        else if (s.equals("slim"))
            playerrenderer = MenuPlayerRenderers.fakePlayerRendererSlim;

        cir.setReturnValue((EntityRenderer<? super T>) playerrenderer);
    }

    @Inject(method = "getSquaredDistanceToCamera(Lnet/minecraft/entity/Entity;)D", at = @At("HEAD"), cancellable = true)
    public void getSquaredDistanceToCamera(Entity entity, CallbackInfoReturnable<Double> cir) {
        if (entity instanceof FakePlayer)
        {
            cir.setReturnValue(20.0);
            cir.cancel();
        }
    }
}
