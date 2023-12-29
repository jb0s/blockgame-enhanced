package dev.jb0s.blockgameenhanced.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;

public class GUIHelper {
    /**
     * Draws a 9-slice texture
     *
     * @param context     current draw context
     * @param texture     texture to use
     * @param rect        where on screen to draw
     * @param outerRegion outer bounds of the sprite on the texture
     * @param innerRegion inner bounds that define the slices
     * @see <a href="https://en.wikipedia.org/wiki/9-slice_scaling">9-slice scaling</a>
     */
    public static void draw9Slice(DrawContext context, Identifier texture, Rectangle rect, Rectangle outerRegion, Rectangle innerRegion) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, texture);

        int middleWidth = Math.min(innerRegion.getMaxX() - innerRegion.x, rect.width - (outerRegion.width - innerRegion.getMaxX()) - innerRegion.x);
        int middleHeight = Math.min(innerRegion.getMaxY() - innerRegion.y, rect.height - (outerRegion.height - innerRegion.getMaxY()) - innerRegion.y);

        context.drawTexture(texture, 0, 0, innerRegion.x, innerRegion.y, outerRegion.x, outerRegion.y, innerRegion.x, innerRegion.y, 256, 256);
        context.drawTexture(texture, innerRegion.x, 0, rect.width - (outerRegion.width - innerRegion.getMaxX()) - innerRegion.x, innerRegion.y, outerRegion.x + innerRegion.x, outerRegion.y, middleWidth, innerRegion.y, 256, 256);
        context.drawTexture(texture, rect.width - (outerRegion.width - innerRegion.getMaxX()), 0, outerRegion.width - innerRegion.getMaxX(), innerRegion.y, outerRegion.x + innerRegion.getMaxX(), outerRegion.y, outerRegion.width - innerRegion.getMaxX(), innerRegion.y, 256, 256);

        context.drawTexture(texture, 0, innerRegion.y, innerRegion.x, rect.height - (outerRegion.height - innerRegion.getMaxY()) - innerRegion.y, outerRegion.x, outerRegion.y + innerRegion.y, innerRegion.x, middleHeight, 256, 256);
        context.drawTexture(texture, rect.width - (outerRegion.width - innerRegion.getMaxX()), innerRegion.y, outerRegion.width - innerRegion.getMaxX(), rect.height - (outerRegion.height - innerRegion.getMaxY()) - innerRegion.y, outerRegion.x + innerRegion.getMaxX(), outerRegion.y + innerRegion.y, outerRegion.width - innerRegion.getMaxX(), middleHeight, 256, 256);

        context.drawTexture(texture, 0, 0, innerRegion.x, outerRegion.height - innerRegion.getMaxY(), outerRegion.x, outerRegion.y + innerRegion.getMaxY(), innerRegion.x, outerRegion.height - innerRegion.getMaxY(), 256, 256);
        context.drawTexture(texture, innerRegion.x, 0, rect.width - (outerRegion.width - innerRegion.getMaxX()) - innerRegion.x, outerRegion.height - innerRegion.getMaxY(), outerRegion.x + innerRegion.x, outerRegion.y + innerRegion.getMaxY(), middleWidth, outerRegion.height - innerRegion.getMaxY(), 256, 256);
        context.drawTexture(texture, rect.width - (outerRegion.width - innerRegion.getMaxX()), 0, outerRegion.width - innerRegion.getMaxX(), outerRegion.height - innerRegion.getMaxY(), outerRegion.x + innerRegion.getMaxX(), outerRegion.y + innerRegion.getMaxY(), outerRegion.width - innerRegion.getMaxX(), outerRegion.height - innerRegion.getMaxY(), 256, 256);
    }

    /**
     * Draws a 9-slice texture with background
     *
     * @param drawContext    current draw context
     * @param texture     texture to use
     * @param rect        where on screen to draw
     * @param outerRegion outer bounds of the sprite on the texture
     * @param innerRegion inner bounds that define the slices
     * @see <a href="https://en.wikipedia.org/wiki/9-slice_scaling">9-slice scaling</a>
     */
    public static void draw9SliceBg(DrawContext drawContext, Identifier texture, Rectangle rect, Rectangle outerRegion, Rectangle innerRegion) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, texture);

        int middleWidth = Math.min(innerRegion.getMaxX() - innerRegion.x, rect.width - (outerRegion.width - innerRegion.getMaxX()) - innerRegion.x);
        int middleHeight = Math.min(innerRegion.getMaxY() - innerRegion.y, rect.height - (outerRegion.height - innerRegion.getMaxY()) - innerRegion.y);
        // TODO: won't work if the background outerRegion is smaller than what we are filling
        drawContext.drawTexture(texture, rect.x + innerRegion.x, rect.y + innerRegion.y, rect.width - (outerRegion.width - innerRegion.getMaxX()) - innerRegion.x, rect.height - (outerRegion.height - innerRegion.getMaxY()) - innerRegion.y, outerRegion.x + innerRegion.x, outerRegion.y + innerRegion.y, middleWidth, middleHeight, 256, 256);
        draw9Slice(drawContext, texture, rect, outerRegion, innerRegion);
    }
}
