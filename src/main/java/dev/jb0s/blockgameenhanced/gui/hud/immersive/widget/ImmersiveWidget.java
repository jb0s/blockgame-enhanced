package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ImmersiveWidget {
    @Getter
    private InGameHud inGameHud;

    public ImmersiveWidget(InGameHud inGameHud) {
        this.inGameHud = inGameHud;
    }

    public void render(MatrixStack matrices, int x, int y, float tickDelta) {
        RenderSystem.enableBlend();
        DrawableHelper.fill(matrices, x, y, x + getWidth(), y + getHeight(), 135 << 24);

        Text text = Text.of("Hello world!");
        int centerX = x + (getWidth() / 2) - (MinecraftClient.getInstance().textRenderer.getWidth(text) / 2);
        int centerY = y + (getHeight() / 2) - (MinecraftClient.getInstance().textRenderer.fontHeight / 2);
        int textColor = 0xFFFFFF;

        DrawableHelper.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, text, centerX, centerY, textColor);
        RenderSystem.disableBlend();
    }

    public int getWidth() {
        return 70;
    }

    public int getHeight() {
        return 12;
    }

    protected final void drawText(MatrixStack matrices, Text text, int x, int y, int textColor) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        textRenderer.draw(matrices, text, x, y, textColor);
    }
}
