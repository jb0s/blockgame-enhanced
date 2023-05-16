package dev.jb0s.blockgameenhanced.mixin.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public abstract class MixinPlayerListEntry {
    private static final Identifier DEVELOPER_CAPE = new Identifier("blockgame", "textures/cape/devcape.png");
    private static final Identifier PIRATESOFTWARE_CAPE = new Identifier("blockgame", "textures/cape/thorcape.png");

    @Shadow public abstract GameProfile getProfile();

    @Inject(method = "getCapeTexture", at = @At("RETURN"), cancellable = true)
    public void getCapeTexture(CallbackInfoReturnable<Identifier> cir) {
        String username = getProfile().getName();
        switch (username) {
            case "jakm" -> cir.setReturnValue(DEVELOPER_CAPE);
            case "PirateSoftware" -> cir.setReturnValue(PIRATESOFTWARE_CAPE);
        }
    }
}
