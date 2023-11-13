package dev.jb0s.blockgameenhanced.mixin.entity;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    public void isGlowing(CallbackInfoReturnable<Boolean> cir) {
        // todo bruh
        /*Entity thisEntity = (Entity)(Object) this;
        PartyManager pm = BlockgameEnhancedClient.getPartyManager();

        // Ensure that we are only executing this on player entities
        if(!(thisEntity instanceof OtherClientPlayerEntity thisPlayer)) {
            return;
        }

        // Make this entity glow regardless of everything if they are a member of the player's party
        boolean outliningEnabled = BlockgameEnhanced.getConfig().getPartyHudConfig().outlineMembers;
        if(pm.isPlayerInParty(thisPlayer) && outliningEnabled) {
            cir.setReturnValue(true);
            cir.cancel();
        }*/
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    public void getTeamColorValue(CallbackInfoReturnable<Integer> cir) {
        // todo bruh
        /*Entity thisEntity = (Entity)(Object) this;
        PartyManager pm = BlockgameEnhancedClient.getPartyManager();

        // Ensure that we are only executing this on player entities
        if(!(thisEntity instanceof OtherClientPlayerEntity thisPlayer)) {
            return;
        }

        // Make this entity glow blue regardless of everything if they are a member of the player's party
        boolean outliningEnabled = BlockgameEnhanced.getConfig().getPartyHudConfig().outlineMembers;
        if(pm.isPlayerInParty(thisPlayer) && outliningEnabled) {
            cir.setReturnValue(6475516);
            cir.cancel();
        }*/
    }
}
