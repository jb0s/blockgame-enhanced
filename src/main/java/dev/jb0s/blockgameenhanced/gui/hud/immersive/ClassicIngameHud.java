package dev.jb0s.blockgameenhanced.gui.hud.immersive;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class ClassicIngameHud extends ImmersiveIngameHud {
    public ClassicIngameHud(MinecraftClient client) {
        super(client);
    }

    @Override
    protected void renderHotbar(float tickDelta, MatrixStack matrices) {
        statusBar.render(matrices, 0, scaledHeight - statusBar.getHeight(), tickDelta);
        super.renderHotbar(tickDelta, matrices);
    }
}
