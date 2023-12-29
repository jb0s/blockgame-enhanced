package dev.jb0s.blockgameenhanced.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.event.gamefeature.party.PartyPingEvent;
import dev.jb0s.blockgameenhanced.event.gamefeature.party.PartyUpdatedEvent;
import dev.jb0s.blockgameenhanced.gamefeature.party.PartyGameFeature;
import dev.jb0s.blockgameenhanced.helper.MathHelper;
import dev.jb0s.blockgameenhanced.helper.TimeHelper;
import dev.jb0s.blockgameenhanced.gamefeature.party.PartyMember;
import dev.jb0s.blockgameenhanced.gamefeature.party.PartyPing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;

@Environment(value = EnvType.CLIENT)
public class PartyHud {
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
    private static ArrayList<PartyMember> partyMembers;
    private static HashMap<PartyMember, PartyPing> partyPings;

    public static void render(DrawContext context, float tickDelta) {
        timer += tickDelta;
        if(client == null) {
            client = MinecraftClient.getInstance();

            PartyUpdatedEvent.EVENT.register(PartyHud::onPartyUpdate);
            PartyPingEvent.EVENT.register(PartyHud::onPartyPing);
            return;
        }

        // Don't draw if the party hud is disabled
        if(!BlockgameEnhanced.getConfig().getPartyHudConfig().showHud) {
            return;
        }

        // Don't draw if we're not in a party
        if(partyMembers == null) {
            return;
        }

        // Don't draw if the F3 menu is visible to prevent drawing on top of it
        /*if(client.options.debugEnabled) {
            return;
        }*/

        int indexOffset = 0;
        for (int i = 0; i < partyMembers.size(); i++) {
            boolean allowRenderSelf = BlockgameEnhanced.getConfig().getPartyHudConfig().showSelf;
            String sessionPlayerName = client.getSession().getUsername();

            // If we're trying to render our own stats and that's not allowed, don't do so
            if(partyMembers.get(i).getPlayerName().equals(sessionPlayerName) && !allowRenderSelf) {
                indexOffset--; // hack to fix list positioning
                continue;
            }

            renderPartyMember(context, partyMembers.get(i), i + indexOffset);
        }

        if(partyPings != null) {
            for (PartyPing ping : partyPings.values()) {
                renderPartyPing(context, ping);
            }
        }
    }

    private static void onPartyPing(PartyGameFeature partyGameFeature) {
        partyPings = partyGameFeature.getPartyPings();
    }

    private static void onPartyUpdate(PartyGameFeature partyGameFeature) {
        partyMembers = partyGameFeature.getPartyMembers();
    }

    private static void renderPartyMember(DrawContext context, PartyMember member, int index) {
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
        context.drawTexture(isBedrockPlayer ? BACKGROUND_TEXTURE_BEDROCK : BACKGROUND_TEXTURE, x, yIncrement, 0, 0, 0, MEMBER_CARD_WIDTH, MEMBER_CARD_HEIGHT, MEMBER_CARD_BACKGROUND_SIZE, MEMBER_CARD_BACKGROUND_SIZE);

        // Draw Vignette
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderTexture(0, VIGNETTE_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, .4f);
        context.drawTexture(VIGNETTE_TEXTURE, x, yIncrement, 0, 0, 0, MEMBER_CARD_WIDTH, MEMBER_CARD_HEIGHT, MEMBER_CARD_WIDTH, MEMBER_CARD_HEIGHT);
        RenderSystem.disableBlend();

        // Player Head
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, member.getPlayer().getSkinTextures().texture());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);
        context.drawTexture(member.getPlayer().getSkinTextures().texture(), contentX, contentY, headSize, headSize, 8.0f, 8.0f, 8, 8, 64, 64);
        context.drawTexture(member.getPlayer().getSkinTextures().texture(), contentX, contentY, headSize, headSize, 40.0f, 8.0f, 8, 8, 64, 64);
        RenderSystem.disableBlend();

        // Draw Name
        context.drawTextWithShadow(client.textRenderer, Text.of(member.getPlayerName()), contentX + headSize + 5, contentY, 0xFFFFFF);

        // Health bar Atlas Dimensions
        int textureWidth = 192;
        int textureHeight = 46;

        // Draw Health
        float calculatedPercentage = (float)member.getHealth() / (float)member.getMaxHealth();
        RenderSystem.setShaderTexture(0, HEALTHBARS_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);
        context.drawTexture(HEALTHBARS_TEXTURE, contentX + headSize + 5, contentY + client.textRenderer.fontHeight, 9, 9, 0, 0, 18, 18, textureWidth, textureHeight);
        context.drawTexture(HEALTHBARS_TEXTURE, contentX + headSize + 16, contentY + client.textRenderer.fontHeight + 1, textureWidth / 2, 7, 0, 32, textureWidth, 14, textureWidth, textureHeight);
        context.drawTexture(HEALTHBARS_TEXTURE, contentX + headSize + 16, contentY + client.textRenderer.fontHeight + 1, (int) ((textureWidth / 2) * calculatedPercentage), 7, 0, 18, (int) (textureWidth * calculatedPercentage), 14, textureWidth, textureHeight);

        // Dead / Out Of Range Blackout
        boolean isOutOfRange = TimeHelper.getSystemTimeUnix() - member.getLastUpdateSecond() > OUT_OF_RANGE_THRESHOLD;
        boolean blackOut = isOutOfRange || !member.isAlive();
        if(blackOut) {
            RenderSystem.enableBlend();
            context.fill(x, yIncrement, x + MEMBER_CARD_WIDTH, yIncrement + MEMBER_CARD_HEIGHT, 135 << 24);

            if(isOutOfRange && member.isAlive()) {
                Text oorText = Text.of("OUT OF RANGE");
                int centerX = x + (MEMBER_CARD_WIDTH / 2) - (client.textRenderer.getWidth(oorText) / 2);
                int centerY = yIncrement + (MEMBER_CARD_HEIGHT / 2) - (client.textRenderer.fontHeight / 2);
                int textColor = 0xFFFFFF;
                if(timer % 120 >= 60) {
                    textColor = 0xC4C4C4;
                }

                context.drawTextWithShadow(client.textRenderer, Text.of("OUT OF RANGE"), centerX, centerY, textColor);
            }

            RenderSystem.disableBlend();
        }
    }

    private static void renderPartyPing(DrawContext context, PartyPing ping) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        ClientPlayerEntity cpe = minecraft.player;

        if (cpe == null || ping.getScreenSpacePos() == null) {
            return;
        }

        // Don't render pings inter dimensionally
        if(!cpe.getWorld().getRegistryKey().getValue().getPath().equals(ping.getWorld())) {
            return;
        }

        // Calculate where and how big the ping has to be displayed
        Vector3f pos = ping.getScreenSpacePos();
        Vec3d cameraPosVec = MinecraftClient.getInstance().player.getCameraPosVec(0.0f);
        float distanceToPing = (float) cameraPosVec.distanceTo(ping.getLocation());
        float uiScale = (float) MinecraftClient.getInstance().getWindow().getScaleFactor();
        float uiScaleAdjustment = uiScale * 2f / 5f;
        float size = getDistanceScale(distanceToPing) * 2.5f / uiScale * uiScaleAdjustment;

        context.getMatrices().push();
        context.getMatrices().translate((pos.x() / uiScale), (pos.y() / uiScale), 0);
        context.getMatrices().scale(size, size, 1f);

        // Calculate ping label text and size
        String labelText = String.format("%s - %dm", ping.getPartyMember().getPlayerName(), (int) distanceToPing);
        var labelSize = new Vec2f(MinecraftClient.getInstance().textRenderer.getWidth(labelText), MinecraftClient.getInstance().textRenderer.fontHeight);
        var labelOffset = labelSize.multiply(-0.5f).add(new Vec2f(0f, labelSize.y * -1.5f));

        // Draw player head
        context.getMatrices().push();
        context.getMatrices().translate(labelOffset.x + ((labelOffset.y / 2) + 3), labelOffset.y, 0);
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, ping.getPartyMember().getPlayer().getSkinTextures().texture());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);
        context.drawTexture(ping.getPartyMember().getPlayer().getSkinTextures().texture(), -2, -2, (int)labelSize.y + 2, (int)labelSize.y + 2, 8.0f, 8.0f, 8, 8, 64, 64);
        context.drawTexture(ping.getPartyMember().getPlayer().getSkinTextures().texture(), -2, -2, (int)labelSize.y + 2, (int)labelSize.y + 2, 40.0f, 8.0f, 8, 8, 64, 64);
        RenderSystem.disableBlend();
        context.getMatrices().pop();

        // Draw text
        context.getMatrices().push();
        context.getMatrices().translate(labelOffset.x - ((labelOffset.y / 2) + 3), labelOffset.y, 0);
        context.fill(-2, -2, (int)labelSize.x + 1, (int)labelSize.y, 0x77000000);
        context.drawText(MinecraftClient.getInstance().textRenderer, labelText, 0, 0, 0xFFFFFFFF, false);
        context.getMatrices().pop();

        // Draw angled square at origin
        MathHelper.rotateZ(context.getMatrices(), (float)(Math.PI / 4f));
        context.getMatrices().translate(-2.5, -2.5, 0);
        context.fill(0, 0, 5, 5, ping.isHovered() ? 0xFFFF0000 : 0xFFFFFFFF);

        context.getMatrices().pop();
    }

    private static float getDistanceScale(float distance) {
        var scaleMin = 1f;
        var scale = 2f / Math.pow(distance, 0.3f);

        return (float)Math.max(scaleMin, scale);
    }
}
