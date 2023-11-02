package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;

public class ImmersiveWidget {
    @Getter
    private InGameHud inGameHud;

    public ImmersiveWidget(InGameHud inGameHud) {
        this.inGameHud = inGameHud;
    }

    public void render(DrawContext context, int x, int y, float tickDelta) {
        RenderSystem.enableBlend();
        context.fill(x, y, x + getWidth(), y + getHeight(), 135 << 24);

        Text text = Text.of("Hello world!");
        int centerX = x + (getWidth() / 2) - (MinecraftClient.getInstance().textRenderer.getWidth(text) / 2);
        int centerY = y + (getHeight() / 2) - (MinecraftClient.getInstance().textRenderer.fontHeight / 2);
        int textColor = 0xFFFFFF;

        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, centerX, centerY, textColor);
        RenderSystem.disableBlend();
    }

    public void tick() {
    }

    public int getWidth() {
        return 70;
    }

    public int getHeight() {
        return 12;
    }

    protected final void drawText(DrawContext context, Text text, int x, int y, int textColor) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        context.drawText(textRenderer, text, x, y, textColor, true);
    }
}
