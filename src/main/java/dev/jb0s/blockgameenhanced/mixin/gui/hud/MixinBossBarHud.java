package dev.jb0s.blockgameenhanced.mixin.gui.hud;

import dev.jb0s.blockgameenhanced.helper.BossBarHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(BossBarHud.class)
public class MixinBossBarHud {
    @Shadow @Final Map<UUID, ClientBossBar> bossBars;

    @Inject(method = "render", at = @At("RETURN"))
    public void render(DrawContext context, CallbackInfo ci) {
        BossBarHelper.setBossBars(bossBars);
    }
}
