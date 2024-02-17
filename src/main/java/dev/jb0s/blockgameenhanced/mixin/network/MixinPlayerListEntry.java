package dev.jb0s.blockgameenhanced.mixin.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(PlayerListEntry.class)
public abstract class MixinPlayerListEntry {
    private static final Identifier DEVELOPER_CAPE = new Identifier("blockgame", "textures/cape/devcape.png");
    private static final Identifier PIRATESOFTWARE_CAPE = new Identifier("blockgame", "textures/cape/thorcape.png");
    private static final Identifier NOTKER_CAPE = new Identifier("blockgame", "textures/cape/notkercape.png");
    private static final Identifier SOPHIE_CAPE = new Identifier("blockgame", "textures/cape/phicape.png");
    private static final Identifier KLH_IO_CAPE = new Identifier("blockgame", "textures/cape/klh_iocape.png");
    private static final Identifier HAM_CAPE = new Identifier("blockgame", "textures/cape/hamcape.png");

    @Shadow public abstract GameProfile getProfile();

    @Shadow public abstract SkinTextures getSkinTextures();

    @Shadow @Final private Supplier<SkinTextures> texturesSupplier;

    @Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
    public void getSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        Identifier capeTexture = null;

        // Set cape texture
        String username = getProfile().getName();
        switch (username) {
            case "jakm", "notIrma" -> capeTexture = DEVELOPER_CAPE;
            case "PirateSoftware" -> capeTexture = PIRATESOFTWARE_CAPE;
            case "Notker" -> capeTexture = NOTKER_CAPE;
            case "PhiPhantastx" -> capeTexture = SOPHIE_CAPE;
            case "klh_io" -> capeTexture = KLH_IO_CAPE;
            case "Little__Ham" -> capeTexture = HAM_CAPE;
        }

        // Modify outcome if we found a custom cape
        if(capeTexture != null) {
            SkinTextures textures = texturesSupplier.get();
            cir.setReturnValue(new SkinTextures(textures.texture(), textures.textureUrl(), capeTexture, capeTexture, textures.model(), textures.secure()));
        }
    }
}
