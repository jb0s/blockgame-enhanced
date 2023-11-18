package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.ImmersiveWidget;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class ImmersiveDrownVignette extends ImmersiveWidget {
    private static final Identifier TEXTURE = new Identifier("blockgame", "textures/gui/hud/drown_vignette.png");

    public ImmersiveDrownVignette(InGameHud inGameHud) {
        super(inGameHud);
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, float tickDelta) {
        LivingEntity player = getInGameHud().getCameraPlayer();
        if(player == null) return;

        float airPercent = ((float) player.getAir() / player.getMaxAir());
        float sumHealth = (1f - (player.getHealth() / player.getMaxHealth())) * 0.4f;
        float sumAir = (1f - airPercent) * 0.6f;
        float alpha = (sumHealth + sumAir) * (1f - airPercent);

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(0.0, getHeight(), -90.0).texture(0.0F, 1.0F).next();
        bufferBuilder.vertex(getWidth(), getHeight(), -90.0).texture(1.0F, 1.0F).next();
        bufferBuilder.vertex(getWidth(), 0.0, -90.0).texture(1.0F, 0.0F).next();
        bufferBuilder.vertex(0.0, 0.0, -90.0).texture(0.0F, 0.0F).next();
        tessellator.draw();

        RenderSystem.disableBlend();
    }

    @Override
    public int getWidth() {
        return getInGameHud().scaledWidth;
    }

    @Override
    public int getHeight() {
        return getInGameHud().scaledHeight;
    }
}
