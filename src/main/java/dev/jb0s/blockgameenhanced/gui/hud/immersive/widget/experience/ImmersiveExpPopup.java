package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.experience;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.ImmersiveWidget;
import dev.jb0s.blockgameenhanced.manager.mmocore.profession.MMOProfession;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ImmersiveExpPopup extends ImmersiveWidget {
    private static final Identifier EXPBARS_TEXTURE = new Identifier("blockgame", "textures/gui/hud/expbars.png");

    @Getter
    private MMOProfession mmoProfession;

    @Getter
    @Setter
    private float percentage;

    @Getter
    @Setter
    private float gained;

    @Getter
    @Setter
    private int inactivityTicks;

    public ImmersiveExpPopup(InGameHud inGameHud, MMOProfession profession, float percentage, float gained) {
        super(inGameHud);
        mmoProfession = profession;
        setPercentage(percentage);
        setGained(gained);
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, float tickDelta) {
        float alpha = (inactivityTicks + tickDelta) < 100 ? 1.0f : 1.0f - (((inactivityTicks + tickDelta) - 100.0f) / 21.f);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, EXPBARS_TEXTURE);

        // Draw bar
        int w = (int)((percentage / 100.f) * getWidth());
        drawTexture(matrices, x, y + (getHeight() - 5), 0, 0, getWidth(), 5);
        drawTexture(matrices, x, y + (getHeight() - 5), 0, 5 * getMmoProfession().getIndex(), w, 5);

        // Draw text
        TextRenderer textRenderer = getInGameHud().client.textRenderer;
        String string = String.format("§a+%d %s §7- §a%.2f%%", (int) getGained(), getMmoProfession().getDisplayName(), percentage);
        int tx = x + ((getWidth() / 2) - (textRenderer.getWidth(string) / 2));

        getInGameHud().getTextRenderer().drawWithShadow(matrices, string, (float)tx, y, 0xFFFFFF + ((int)(alpha * 255) << 24));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    @Override
    public void tick() {
        inactivityTicks++;
    }

    @Override
    public int getWidth() {
        return 151;
    }

    @Override
    public int getHeight() {
        return 17;
    }

    /**
     * Increase amount of EXP gained on this popup.
     * @param gained Amount of EXP gained to add
     */
    public void addGained(float gained) {
        setGained(getGained() + gained);
    }

    /**
     * Helper function to call InGameHud.drawTexture with our custom texture dimensions.
     * @param matrices the matrix stack used for rendering
     * @param x the X coordinate of the rectangle
     * @param y the Y coordinate of the rectangle
     * @param u the left-most coordinate of the texture region
     * @param v the top-most coordinate of the texture region
     * @param width the width
     * @param height the height
     */
    private void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        DrawableHelper.drawTexture(matrices, x, y, getInGameHud().getZOffset(), u, v, width, height, 151, 151);
    }
}
