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

        // 75% of drown overlay is air, the last 25% will fade in as you die
        // The final result is (a + b) divided by the air percentage so that we don't do a drown vignette with PvE/PvP damage
        float a = ((float) player.getAir() / player.getMaxAir()) * 0.75f;
        float b = (player.getHealth() / player.getMaxHealth()) * 0.25f;
        float c = (a + b) / ((float) player.getAir() / player.getMaxAir());

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f - c);

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
