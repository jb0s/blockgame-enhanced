package dev.jb0s.blockgameenhanced.mixin.entity;

import dev.jb0s.blockgameenhanced.event.entity.otherplayer.OtherPlayerInitializedEvent;
import dev.jb0s.blockgameenhanced.event.entity.otherplayer.OtherPlayerTickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OtherClientPlayerEntity.class)
public abstract class MixinOtherClientPlayerEntity {

    @Inject(at = @At("RETURN"), method = "tick")
    public void tick(CallbackInfo ci) {
        OtherClientPlayerEntity thisPlayer = (OtherClientPlayerEntity) (Object) this;

        // Hacky method to handle the initialized event since init() isn't overridden in this class.
        if(thisPlayer.age < 2) {
            OtherPlayerInitializedEvent.EVENT.invoker().otherPlayerInitialized(MinecraftClient.getInstance(), thisPlayer);
        }

        OtherPlayerTickEvent.EVENT.invoker().otherPlayerTick(MinecraftClient.getInstance(), thisPlayer);
    }
}
