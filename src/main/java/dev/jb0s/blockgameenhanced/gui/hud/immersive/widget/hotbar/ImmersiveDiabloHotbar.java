package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.hotbar;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.ImmersiveWidget;
import dev.jb0s.blockgameenhanced.manager.latency.LatencyManager;
import dev.jb0s.blockgameenhanced.manager.mmocore.MMOCoreManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ImmersiveDiabloHotbar extends ImmersiveWidget {
    private static final Identifier WIDGETS_TEXTURE = new Identifier("blockgame", "textures/gui/hud/widgets.png");

    public ImmersiveDiabloHotbar(InGameHud inGameHud) {
        super(inGameHud);
    }

    @Override
    public int getWidth() {
        return 297;
    }

    @Override
    public int getHeight() {
        return 40;
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, float tickDelta) {
        PlayerEntity playerEntity = getInGameHud().getCameraPlayer();
        if (playerEntity == null) {
            return;
        }

        // Gather info
        boolean hasVehicle = playerEntity.hasVehicle();

        // Prepare for drawing
        getInGameHud().client.getProfiler().push("immersiveHotbar");
        resetShaders();

        // Draw frame
        int frameX = x - getWidth() / 2;
        int frameY = y - getHeight();
        drawFrame(matrices, frameX, frameY, hasVehicle);

        // Draw gauges (This isn't even the right word for it LMAO)
        drawHealthGauge(matrices, frameX + 4, frameY + 4, playerEntity);
        drawAirGauge(matrices, frameX + 25, frameY + 4, playerEntity);
        if(!hasVehicle || !(playerEntity.getVehicle() instanceof LivingEntity vehicle)) {
            drawHydrationGauge(matrices, frameX + 278, frameY + 4);
            drawHungerGauge(matrices, frameX + 261, frameY + 4);
        }
        else {
            drawVehicleHealthGauge(matrices, frameX + 261, frameY + 4, vehicle);
        }

        // Draw statues
        drawStatue(matrices, (x - (getWidth() / 2)) - 11, y - 49, false);
        drawStatue(matrices, (x + (getWidth() / 2)) - 11, y - 49, true);

        // Draw misc. widgets
        drawLatencyMeter(matrices, frameX + 66, frameY + 19);
        if (!hasVehicle) drawExperienceBar(matrices, frameX + 48, frameY + 11, playerEntity);
        else drawMountJumpBar(matrices, frameX + 48, frameY + 11, (ClientPlayerEntity) playerEntity);
        drawLevelMeter(matrices, frameX + 34, frameY - 7, playerEntity);

        // Render hotbar items
        drawHotbarItems((frameX + 75), y - 19, tickDelta, playerEntity);

        // Render selected slot arrow
        getInGameHud().client.getProfiler().push("slotArrow");
        getInGameHud().setZOffset(getInGameHud().getZOffset() + 50);
        drawTexture(matrices, (frameX + 73) + playerEntity.getInventory().selectedSlot * 20, frameY + 18, 20, 240, 22, 22);
        getInGameHud().setZOffset(getInGameHud().getZOffset() - 50);
        getInGameHud().client.getProfiler().pop();

        RenderSystem.disableBlend();
        getInGameHud().client.getProfiler().pop();
    }

    private void drawFrame(MatrixStack matrices, int x, int y, boolean hasVehicle) {
        getInGameHud().client.getProfiler().push("frame");

        if(hasVehicle) {
            // If we have a vehicle we need to split the frame in half to render a full container on the right
            drawTexture(matrices, x, y, 0, 49, 257, 40);
            drawTexture(matrices, x + 257, y, 257, 89, 40, 40);
        }
        else {
            // If we don't have a vehicle we can simplify this down to a single draw call
            drawTexture(matrices, x, y, 0, 49, 297, 40);
        }

        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Draws health gauge fluid thing yeah.
     * @param matrices the matrix stack used for rendering
     * @param x the X coordinate of the gauge
     * @param y the Y coordinate of the gauge
     */
    private void drawHealthGauge(MatrixStack matrices, int x, int y, PlayerEntity playerEntity) {
        getInGameHud().client.getProfiler().push("healthMeter");

        // calculate shite
        MMOCoreManager mmoCoreManager = BlockgameEnhancedClient.getMmoCoreManager();
        float healthPercent = (float) mmoCoreManager.getHealth() / mmoCoreManager.getMaxHealth();
        int gaugeHeight = (int) (32.f * healthPercent);
        int yOffset = 32 - gaugeHeight;

        if(playerEntity.hasStatusEffect(StatusEffects.WITHER)) {
            // Withered
            drawTexture(matrices, x, y + yOffset, 116, 99 + yOffset, 32, gaugeHeight);
        }
        else if(playerEntity.hasStatusEffect(StatusEffects.POISON)) {
            // Poisoned
            drawTexture(matrices, x, y + yOffset, 32, 99 + yOffset, 32, gaugeHeight);
        }
        else {
            // Normal
            drawTexture(matrices, x, y + yOffset, 0, 99 + yOffset, 32, gaugeHeight);
        }

        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Draws vehicle health gauge fluid thing yeah.
     * @param matrices the matrix stack used for rendering
     * @param x the X coordinate of the gauge
     * @param y the Y coordinate of the gauge
     */
    private void drawVehicleHealthGauge(MatrixStack matrices, int x, int y, LivingEntity vehicle) {
        getInGameHud().client.getProfiler().push("vehicleHealthMeter");

        // calculate shite
        float healthPercent = vehicle.getHealth() / vehicle.getMaxHealth();
        int gaugeHeight = (int) (32.f * healthPercent);
        int yOffset = 32 - gaugeHeight;

        drawTexture(matrices, x, y + yOffset, 84, 99 + yOffset, 32, gaugeHeight);
        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Draws hunger gauge fluid thing yeah.
     * @param matrices the matrix stack used for rendering
     * @param x the X coordinate of the gauge
     * @param y the Y coordinate of the gauge
     */
    private void drawHungerGauge(MatrixStack matrices, int x, int y) {
        PlayerEntity player = getInGameHud().getCameraPlayer();
        if(player == null) return;

        getInGameHud().client.getProfiler().push("hungerMeter");

        // calculate shite
        MMOCoreManager mmoCoreManager = BlockgameEnhancedClient.getMmoCoreManager();
        float hungerPercent = (float) mmoCoreManager.getHunger() / 20;
        int gaugeHeight = (int) (32.f * hungerPercent);
        int yOffset = 32 - gaugeHeight;

        if(player.hasStatusEffect(StatusEffects.HUNGER)) {
            // Hunger
            drawTexture(matrices, x, y + yOffset, 15, 163 + yOffset, 15, gaugeHeight);
        }
        else {
            // Normal
            drawTexture(matrices, x, y + yOffset, 0, 163 + yOffset, 15, gaugeHeight);
        }

        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Draws hydration gauge fluid thing yeah.
     * @param matrices the matrix stack used for rendering
     * @param x the X coordinate of the gauge
     * @param y the Y coordinate of the gauge
     */
    private void drawHydrationGauge(MatrixStack matrices, int x, int y) {
        PlayerEntity player = getInGameHud().getCameraPlayer();
        if(player == null) return;

        getInGameHud().client.getProfiler().push("hydrationMeter");

        // calculate shite
        MMOCoreManager mmoCoreManager = BlockgameEnhancedClient.getMmoCoreManager();
        float hydratePercent = mmoCoreManager.getHydration() / 20.f;
        int gaugeHeight = (int) (32.f * hydratePercent);
        int yOffset = 32 - gaugeHeight;

        drawTexture(matrices, x, y + yOffset, 0, 131 + yOffset, 15, gaugeHeight);
        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Draws air gauge air thing yeah.
     * @param matrices the matrix stack used for rendering
     * @param x the X coordinate of the gauge
     * @param y the Y coordinate of the gauge
     * @param playerEntity the player entity to render air meter of
     */
    private void drawAirGauge(MatrixStack matrices, int x, int y, PlayerEntity playerEntity) {
        if(playerEntity.getAir() != playerEntity.getMaxAir()) {
            getInGameHud().client.getProfiler().push("airMeter");

            float airPercent = (float) playerEntity.getAir() / playerEntity.getMaxAir();
            int yOffset = 32 - (int)(airPercent * 32.f);

            // Draw frame, then air filler
            drawTexture(matrices, x, y, 64, 98, 11, 32);
            drawTexture(matrices, x + 2, y + yOffset, 75, 98 + yOffset, 9, 32 - yOffset);

            getInGameHud().client.getProfiler().pop();
        }
    }

    /**
     * Draws the latency meter.
     * @param matrices the matrix stack used for rendering
     * @param x the Y coordinate of the meter
     * @param y the Y coordinate of the meter
     */
    private void drawLatencyMeter(MatrixStack matrices, int x, int y) {
        getInGameHud().client.getProfiler().push("latencyMeter");

        LatencyManager latencyManager = BlockgameEnhancedClient.getLatencyManager();
        int latency = latencyManager.getLatency();
        int severity = 0;

        if(latency > 70) severity = 1;
        if(latency > 160) severity = 2;
        if(latency > 200) severity = 3;

        int yOffset = (int) (((float) latency / 230.f) * 20); // Add a little more so the bar is always visible
        drawTexture(matrices, x, y + yOffset, severity * 5, 195 + yOffset, 5, 20 - yOffset);

        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Draws the meter that shows what level you are.
     * @param matrices the matrix stack used for rendering
     * @param x the Y coordinate of the meter
     * @param y the Y coordinate of the meter
     * @param playerEntity the player entity to render level of
     */
    private void drawLevelMeter(MatrixStack matrices, int x, int y, PlayerEntity playerEntity) {
        getInGameHud().client.getProfiler().push("levelMeter");

        // Draw frame
        drawTexture(matrices, x, y, 0, 215, 25, 25);

        // Draw text
        TextRenderer textRenderer = getInGameHud().client.textRenderer;
        String string = String.valueOf(playerEntity.experienceLevel);
        int tx = x + 13 - (textRenderer.getWidth(string) / 2);
        int ty = y + 13 - (textRenderer.fontHeight / 2);

        getInGameHud().getTextRenderer().draw(matrices, string, (float)tx, (float)ty, 0xD1B945);
        getInGameHud().client.getProfiler().pop();

        // I find it fucking absurd that I need to do this after rendering text. What the hell Mojang
        resetShaders();
    }

    /**
     * Draws the experience bar.
     * @param matrices the matrix stack used for rendering
     * @param x the Y coordinate of the bar
     * @param y the Y coordinate of the bar
     * @param playerEntity the player entity to experience progress of
     */
    private void drawExperienceBar(MatrixStack matrices, int x, int y, PlayerEntity playerEntity) {
        getInGameHud().client.getProfiler().push("expBar");

        int nle = playerEntity.getNextLevelExperience();
        if(nle > 0) {
            int fill = (int) (playerEntity.experienceProgress * 210.f);
            drawTexture(matrices, x, y, 0, 89, fill, 5);
        }

        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Draws the mount jump bar.
     * @param matrices the matrix stack used for rendering
     * @param x the Y coordinate of the bar
     * @param y the Y coordinate of the bar
     * @param playerEntity the player entity to render jump bar for
     */
    private void drawMountJumpBar(MatrixStack matrices, int x, int y, ClientPlayerEntity playerEntity) {
        getInGameHud().client.getProfiler().push("mountJumpBar");

        float mjs = playerEntity.getMountJumpStrength();
        if(mjs > 0.f) {
            int fill = (int) (mjs * 210.f);
            drawTexture(matrices, x, y, 0, 94, fill, 5);
        }

        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Helper function to draw the statues at either side of the hotbar.
     * @param matrices the matrix stack used for rendering
     * @param x the X coordinate of the statue
     * @param y the Y coordinate of the statue
     * @param flipped if the statue should face left or right
     */
    private void drawStatue(MatrixStack matrices, int x, int y, boolean flipped) {
        getInGameHud().client.getProfiler().push("statue");
        drawTexture(matrices, x, y, flipped ? 23 : 0, 0, 23, 49);
        getInGameHud().client.getProfiler().pop();
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
        DrawableHelper.drawTexture(matrices, x, y, getInGameHud().getZOffset(), u, v, width, height, 297, 263);
    }

    /**
     * Draws the inventory items & item selector on the hotbar.
     * @param x the X coordinate of item list
     * @param y the Y coordinate of item list
     * @param tickDelta subtick delta value
     * @param playerEntity player entity to draw hotbar items from
     */
    private void drawHotbarItems(int x, int y, float tickDelta, PlayerEntity playerEntity) {
        getInGameHud().client.getProfiler().push("items");

        int slotSeed = 1;
        int slotIndex;
        int slotX;
        for (slotIndex = 0; slotIndex < 9; ++slotIndex) {
            slotX = x + (slotIndex * 20);
            getInGameHud().renderHotbarItem(slotX, y, tickDelta, playerEntity, playerEntity.getInventory().main.get(slotIndex), slotSeed++);
        }

        // Draw offhand item
        ItemStack offHandStack = playerEntity.getOffHandStack();
        if (!offHandStack.isEmpty()) {
            getInGameHud().renderHotbarItem(x - 29, y, tickDelta, playerEntity, offHandStack, slotSeed++);
        }

        resetShaders(); // Again, what the hell Mojang
        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Resets the shaders to prepare for drawing immersive widget sprites.
     */
    private void resetShaders() {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.enableBlend();
    }
}
