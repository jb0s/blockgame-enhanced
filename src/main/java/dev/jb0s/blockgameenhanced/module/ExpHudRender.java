package dev.jb0s.blockgameenhanced.module;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.helper.ExpHudDataHelper;
import dev.jb0s.blockgameenhanced.helper.HudTextHelper;
import dev.jb0s.blockgameenhanced.manager.config.modules.ExpHudConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;

import static net.minecraft.client.gui.DrawableHelper.fill;

public class ExpHudRender {
    public static void onHudRender(MatrixStack matrixStack, float tickDelta) {
        ExpHudConfig config = BlockgameEnhanced.getConfig().getExpHudConfig();

        if (!config.expHudEnabled || ExpHudDataHelper.hideOverlay) return;

        float startVertical = config.yPosExpHud;
        float startHorizontal = config.xPosExpHud;
        float offset = config.lineSpacingExpHud;
        float scale = config.expHudScale;
        float opacity = config.expHudOpacity;

        int textColor = config.textColorExpHud;
        int coinColor = config.coinColorExpHud;

        boolean coinEnabled = config.coinEnabledExpHud;

        //Apply the Hud Scale
        matrixStack.scale(scale, scale, 1);

        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;

        //renderer.drawWithShadow(matrixStack, HudTextHelper.getProfessionHudText(professionNames[1], 0f, 0f,1000f, 5.64f, textColorExpHud), startHorizontal + 50, startVertical + 100, textColorExpHud);
        // List of all hud text to calculate the background size and draw
        MutableText[] textList = new MutableText[ExpHudDataHelper.professionNames.length + 2];

        // Create Title Text
        textList[0] = ExpHudDataHelper.showGlobal ? HudTextHelper.getTitleHudText("Session EXP Stats:", textColor) : HudTextHelper.getTitleHudText("Last " + ExpHudDataHelper.DEFAULT_MAX_SAMPLE_VALUE + " EXP Stats:", textColor);
        // Create Coin Text
        if (ExpHudDataHelper.coins > 0 && coinEnabled) {
            textList[1] = HudTextHelper.getCoinHudText(ExpHudDataHelper.coins, coinColor, textColor);
        }
        // Create Profession Text
        for (int i = 0; i < ExpHudDataHelper.professionNames.length; i++) {
            // Only Add professions with Exp
            if (ExpHudDataHelper.professionTotalExpValues[i] > 0f) {
                // Create the Profession Texts
                textList[i + 2] = HudTextHelper.getProfessionHudText(
                        ExpHudDataHelper.professionNames[i],
                        ExpHudDataHelper.professionLevelValues[i],
                        ExpHudDataHelper.equipmentBonusExpValues[i],
                        ExpHudDataHelper.showGlobal ? ExpHudDataHelper.professionTotalExpValues[i] : ExpHudDataHelper.professionSampleTotalExpValues[i],
                        ExpHudDataHelper.showGlobal ? ExpHudDataHelper.professionTotalAverageValues[i] : ExpHudDataHelper.professionSampleAverages[i],
                        textColor
                );
            }
        }


        // Create the Background
        if (opacity > 0f) {
            int textBoxHeight = ExpHudDataHelper.DEFAULT_TEXT_HEIGHT;
            int textBoxWidth = 0;
            int borderWidth = ExpHudDataHelper.getHudBackgroundBorderSize();

            // Calculate Background Height
            for (MutableText mutableText : textList) {
                if (mutableText != null) {
                    // Change the Background box width if the text is wider then the current box
                    textBoxWidth = Math.max(textBoxWidth, renderer.getWidth(mutableText));
                    // Add Line Height
                    textBoxHeight += offset;
                }
            }
            // Draw Background
            int backgroundColor = MinecraftClient.getInstance().options.getTextBackgroundColor(opacity);
            fill(matrixStack,
                    (int) startHorizontal - borderWidth,
                    (int) startVertical - borderWidth ,
                    (int) startHorizontal + textBoxWidth + borderWidth,
                    (int) startVertical + textBoxHeight - borderWidth,
                    backgroundColor);
        }



        //Draw Title
        renderer.drawWithShadow(matrixStack, textList[0], startHorizontal, startVertical, textColor);
        int row = 1;

        // Draw Coin Text
        if (ExpHudDataHelper.coins > 0 && coinEnabled) {
            renderer.drawWithShadow(matrixStack, textList[1], startHorizontal, startVertical + ((row) * offset), coinColor);
            row++;
        }
        // Draw Profession Text
        for (int i = 0; i < ExpHudDataHelper.professionNames.length; i++) {
            if (ExpHudDataHelper.professionTotalExpValues[i] > 0f) {
                renderer.drawWithShadow(matrixStack, textList[i + 2], startHorizontal, startVertical + ((row) * offset), textColor);
                row++;
            }

        }
        // Restore the Default scale after drawing the Hud
        matrixStack.scale(1f / scale, 1f / scale, 1);

    }
}
