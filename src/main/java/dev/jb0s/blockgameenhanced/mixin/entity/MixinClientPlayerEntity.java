package dev.jb0s.blockgameenhanced.mixin.entity;

import dev.jb0s.blockgameenhanced.event.entity.player.PlayerTickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        ClientPlayerEntity thisPlayer = (ClientPlayerEntity) (Object) this;
        PlayerTickEvent.EVENT.invoker().playerTick(MinecraftClient.getInstance(), thisPlayer);
    }
}
