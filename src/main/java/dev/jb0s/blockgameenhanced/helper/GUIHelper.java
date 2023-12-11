package dev.jb0s.blockgameenhanced.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GUIHelper {
    /**
     * Draws a 9-slice texture
     *
     * @param matrices    current matrix stack
     * @param texture     texture to use
     * @param rect        where on screen to draw
     * @param outerRegion outer bounds of the sprite on the texture
     * @param innerRegion inner bounds that define the slices
     * @see <a href="https://en.wikipedia.org/wiki/9-slice_scaling">9-slice scaling</a>
     */
    public static void draw9Slice(MatrixStack matrices, Identifier texture, Rectangle rect, Rectangle outerRegion, Rectangle innerRegion) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, texture);

        matrices.push();
        matrices.translate(rect.x, rect.y, 0);

        int middleWidth = Math.min(innerRegion.getMaxX() - innerRegion.x, rect.width - (outerRegion.width - innerRegion.getMaxX()) - innerRegion.x);
        int middleHeight = Math.min(innerRegion.getMaxY() - innerRegion.y, rect.height - (outerRegion.height - innerRegion.getMaxY()) - innerRegion.y);

        DrawableHelper.drawTexture(matrices, 0, 0, innerRegion.x, innerRegion.y, outerRegion.x, outerRegion.y, innerRegion.x, innerRegion.y, 256, 256);
        DrawableHelper.drawTexture(matrices, innerRegion.x, 0, rect.width - (outerRegion.width - innerRegion.getMaxX()) - innerRegion.x, innerRegion.y, outerRegion.x + innerRegion.x, outerRegion.y, middleWidth, innerRegion.y, 256, 256);
        DrawableHelper.drawTexture(matrices, rect.width - (outerRegion.width - innerRegion.getMaxX()), 0, outerRegion.width - innerRegion.getMaxX(), innerRegion.y, outerRegion.x + innerRegion.getMaxX(), outerRegion.y, outerRegion.width - innerRegion.getMaxX(), innerRegion.y, 256, 256);

        DrawableHelper.drawTexture(matrices, 0, innerRegion.y, innerRegion.x, rect.height - (outerRegion.height - innerRegion.getMaxY()) - innerRegion.y, outerRegion.x, outerRegion.y + innerRegion.y, innerRegion.x, middleHeight, 256, 256);
        DrawableHelper.drawTexture(matrices, rect.width - (outerRegion.width - innerRegion.getMaxX()), innerRegion.y, outerRegion.width - innerRegion.getMaxX(), rect.height - (outerRegion.height - innerRegion.getMaxY()) - innerRegion.y, outerRegion.x + innerRegion.getMaxX(), outerRegion.y + innerRegion.y, outerRegion.width - innerRegion.getMaxX(), middleHeight, 256, 256);

        matrices.translate(0, rect.height - (outerRegion.height - innerRegion.getMaxY()), 0);

        DrawableHelper.drawTexture(matrices, 0, 0, innerRegion.x, outerRegion.height - innerRegion.getMaxY(), outerRegion.x, outerRegion.y + innerRegion.getMaxY(), innerRegion.x, outerRegion.height - innerRegion.getMaxY(), 256, 256);
        DrawableHelper.drawTexture(matrices, innerRegion.x, 0, rect.width - (outerRegion.width - innerRegion.getMaxX()) - innerRegion.x, outerRegion.height - innerRegion.getMaxY(), outerRegion.x + innerRegion.x, outerRegion.y + innerRegion.getMaxY(), middleWidth, outerRegion.height - innerRegion.getMaxY(), 256, 256);
        DrawableHelper.drawTexture(matrices, rect.width - (outerRegion.width - innerRegion.getMaxX()), 0, outerRegion.width - innerRegion.getMaxX(), outerRegion.height - innerRegion.getMaxY(), outerRegion.x + innerRegion.getMaxX(), outerRegion.y + innerRegion.getMaxY(), outerRegion.width - innerRegion.getMaxX(), outerRegion.height - innerRegion.getMaxY(), 256, 256);

        matrices.pop();
    }

    /**
     * Draws a 9-slice texture with background
     *
     * @param matrices    current matrix stack
     * @param texture     texture to use
     * @param rect        where on screen to draw
     * @param outerRegion outer bounds of the sprite on the texture
     * @param innerRegion inner bounds that define the slices
     * @see <a href="https://en.wikipedia.org/wiki/9-slice_scaling">9-slice scaling</a>
     */
    public static void draw9SliceBg(MatrixStack matrices, Identifier texture, Rectangle rect, Rectangle outerRegion, Rectangle innerRegion) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, texture);

        int middleWidth = Math.min(innerRegion.getMaxX() - innerRegion.x, rect.width - (outerRegion.width - innerRegion.getMaxX()) - innerRegion.x);
        int middleHeight = Math.min(innerRegion.getMaxY() - innerRegion.y, rect.height - (outerRegion.height - innerRegion.getMaxY()) - innerRegion.y);
        // TODO: won't work if the background outerRegion is smaller than what we are filling
        DrawableHelper.drawTexture(matrices, rect.x + innerRegion.x, rect.y + innerRegion.y, rect.width - (outerRegion.width - innerRegion.getMaxX()) - innerRegion.x, rect.height - (outerRegion.height - innerRegion.getMaxY()) - innerRegion.y, outerRegion.x + innerRegion.x, outerRegion.y + innerRegion.y, middleWidth, middleHeight, 256, 256);
        draw9Slice(matrices, texture, rect, outerRegion, innerRegion);
    }
}
