package dev.jb0s.blockgameenhanced.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.helper.TimeHelper;
import dev.jb0s.blockgameenhanced.manager.config.ConfigManager;
import dev.jb0s.blockgameenhanced.manager.party.PartyManager;
import dev.jb0s.blockgameenhanced.manager.party.PartyMember;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class PartyHud extends DrawableHelper {
    private static float timer;
    private static final Identifier VIGNETTE_TEXTURE = new Identifier("blockgame", "textures/gui/hud/vignette.png");
    private static final Identifier HEALTHBARS_TEXTURE = new Identifier("blockgame", "textures/gui/hud/healthbars.png");
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/options_background.png");
    private static final Identifier BACKGROUND_TEXTURE_BEDROCK = new Identifier("textures/block/bedrock.png");

    private static final int MEMBER_CARD_WIDTH = 140;
    private static final int MEMBER_CARD_HEIGHT = 28;
    private static final int MEMBER_CARD_MARGIN = 10;
    private static final int MEMBER_CARD_PADDING = 5;
    private static final int MEMBER_CARD_SPACING = 5;
    private static final int MEMBER_CARD_BACKGROUND_SIZE = 32;
    private static final int OUT_OF_RANGE_THRESHOLD = 2;

    private static MinecraftClient client;
    private static PartyManager partyManager;

    public static void render(MatrixStack matrices, float tickDelta) {
        timer += tickDelta;
        if(client == null) {
            client = MinecraftClient.getInstance();
            return;
        }
        if(partyManager == null) {
            partyManager = BlockgameEnhancedClient.getPartyManager();
            return;
        }

        // Don't draw if the party hud is disabled
        if(!BlockgameEnhanced.getConfig().getPartyHudConfig().showHud) {
            return;
        }

        // Don't draw if we're not in a party
        if(partyManager.getPartyMembers() == null) {
            return;
        }

        // Don't draw if the F3 menu is visible to prevent drawing on top of it
        if(client.options.debugEnabled) {
            return;
        }

        ArrayList<PartyMember> partyMembers = partyManager.getPartyMembers();
        int indexOffset = 0;
        for (int i = 0; i < partyMembers.size(); i++) {
            boolean allowRenderSelf = BlockgameEnhanced.getConfig().getPartyHudConfig().showSelf;
            String sessionPlayerName = client.getSession().getUsername();

            // If we're trying to render our own stats and that's not allowed, don't do so
            if(partyMembers.get(i).getPlayerName().equals(sessionPlayerName) && !allowRenderSelf) {
                indexOffset--; // hack to fix list positioning
                continue;
            }

            renderPartyMember(matrices, partyMembers.get(i), i + indexOffset);
        }
    }

    private static void renderPartyMember(MatrixStack matrices, PartyMember member, int index) {
        if(client.world == null)
            return;

        int x = MEMBER_CARD_MARGIN;
        int yIncrement = MEMBER_CARD_MARGIN + ((MEMBER_CARD_HEIGHT + MEMBER_CARD_SPACING) * index);

        int contentX = MEMBER_CARD_MARGIN + MEMBER_CARD_PADDING;
        int contentY = yIncrement + MEMBER_CARD_PADDING;
        int headSize = MEMBER_CARD_HEIGHT - (MEMBER_CARD_PADDING * 2);

        // Draw Background
        boolean isBedrockPlayer = member.getPlayerName().startsWith(".");
        RenderSystem.setShaderTexture(0, isBedrockPlayer ? BACKGROUND_TEXTURE_BEDROCK : BACKGROUND_TEXTURE);
        RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, 1f);
        DrawableHelper.drawTexture(matrices, x, yIncrement, 0, 0, 0, MEMBER_CARD_WIDTH, MEMBER_CARD_HEIGHT, MEMBER_CARD_BACKGROUND_SIZE, MEMBER_CARD_BACKGROUND_SIZE);

        // Draw Vignette
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderTexture(0, VIGNETTE_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, .4f);
        DrawableHelper.drawTexture(matrices, x, yIncrement, 0, 0, 0, MEMBER_CARD_WIDTH, MEMBER_CARD_HEIGHT, MEMBER_CARD_WIDTH, MEMBER_CARD_HEIGHT);
        RenderSystem.disableBlend();

        // Player Head
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, member.getPlayer().getSkinTexture());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);
        DrawableHelper.drawTexture(matrices, contentX, contentY, headSize, headSize, 8.0f, 8.0f, 8, 8, 64, 64);
        DrawableHelper.drawTexture(matrices, contentX, contentY, headSize, headSize, 40.0f, 8.0f, 8, 8, 64, 64);
        RenderSystem.disableBlend();

        // Draw Name
        DrawableHelper.drawTextWithShadow(matrices, client.textRenderer, Text.of(member.getPlayerName()), contentX + headSize + 5, contentY, 0xFFFFFF);

        // Health bar Atlas Dimensions
        int textureWidth = 192;
        int textureHeight = 46;

        // Draw Health
        float calculatedPercentage = (float)member.getHealth() / (float)member.getMaxHealth();
        RenderSystem.setShaderTexture(0, HEALTHBARS_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);
        DrawableHelper.drawTexture(matrices, contentX + headSize + 5, contentY + client.textRenderer.fontHeight, 9, 9, 0, 0, 18, 18, textureWidth, textureHeight);
        DrawableHelper.drawTexture(matrices, contentX + headSize + 16, contentY + client.textRenderer.fontHeight + 1, textureWidth / 2, 7, 0, 32, textureWidth, 14, textureWidth, textureHeight);
        DrawableHelper.drawTexture(matrices, contentX + headSize + 16, contentY + client.textRenderer.fontHeight + 1, (int) ((textureWidth / 2) * calculatedPercentage), 7, 0, 18, (int) (textureWidth * calculatedPercentage), 14, textureWidth, textureHeight);

        // Dead / Out Of Range Blackout
        boolean isOutOfRange = TimeHelper.getSystemTimeUnix() - member.getLastUpdateSecond() > OUT_OF_RANGE_THRESHOLD;
        boolean blackOut = isOutOfRange || !member.isAlive();
        if(blackOut) {
            RenderSystem.enableBlend();
            DrawableHelper.fill(matrices, x, yIncrement, x + MEMBER_CARD_WIDTH, yIncrement + MEMBER_CARD_HEIGHT, 135 << 24);

            if(isOutOfRange && member.isAlive()) {
                Text oorText = Text.of("OUT OF RANGE");
                int centerX = x + (MEMBER_CARD_WIDTH / 2) - (client.textRenderer.getWidth(oorText) / 2);
                int centerY = yIncrement + (MEMBER_CARD_HEIGHT / 2) - (client.textRenderer.fontHeight / 2);
                int textColor = 0xFFFFFF;
                if(timer % 120 >= 60) {
                    textColor = 0xC4C4C4;
                }

                DrawableHelper.drawTextWithShadow(matrices, client.textRenderer, Text.of("OUT OF RANGE"), centerX, centerY, textColor);
            }

            RenderSystem.disableBlend();
        }
    }
}
