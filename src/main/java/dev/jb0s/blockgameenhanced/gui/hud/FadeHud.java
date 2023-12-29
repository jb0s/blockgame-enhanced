package dev.jb0s.blockgameenhanced.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class FadeHud extends DrawableHelper {
    private static final int BLACK = ColorHelper.Argb.getArgb(255, 0, 0, 0);

    public static void render(MatrixStack matrices, MinecraftClient client) {
        if(client.player == null)
            return;

        float alpha = 1f - MathHelper.clamp(((float) client.player.age / 20), 0f, 1f);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        fill(matrices, 0, 0, client.getWindow().getWidth(), client.getWindow().getHeight(), BLACK);
        RenderSystem.disableBlend();
    }
}
