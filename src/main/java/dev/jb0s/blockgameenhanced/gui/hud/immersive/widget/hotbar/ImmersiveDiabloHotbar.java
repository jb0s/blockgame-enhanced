package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.hotbar;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.gamefeature.mmostats.MMOStatsUpdatedEvent;
import dev.jb0s.blockgameenhanced.gamefeature.mmostats.MMOStatsGameFeature;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.ImmersiveWidget;
import dev.jb0s.blockgameenhanced.config.modules.IngameHudConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ImmersiveDiabloHotbar extends ImmersiveWidget {
    private static final Identifier WIDGETS_TEXTURE = new Identifier("blockgame", "textures/gui/hud/widgets.png");

    private int health;
    private int maxHealth;
    private int hunger;
    private float hydration;

    public ImmersiveDiabloHotbar(InGameHud inGameHud) {
        super(inGameHud);

        MMOStatsUpdatedEvent.EVENT.register(this::mmoStatsUpdated);
    }

    private void mmoStatsUpdated(MMOStatsGameFeature gameFeature) {
        health = gameFeature.getHealth();
        maxHealth = gameFeature.getMaxHealth();
        hunger = gameFeature.getHunger();
        hydration = gameFeature.getHydration();
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
    public void render(DrawContext context, int x, int y, float tickDelta) {
        PlayerEntity playerEntity = getInGameHud().getCameraPlayer();
        if (playerEntity == null) {
            return;
        }

        // Gather info
        boolean hasVehicle = playerEntity.hasVehicle() && playerEntity.getVehicle() instanceof LivingEntity;

        // Prepare for drawing
        getInGameHud().client.getProfiler().push("immersiveHotbar");
        resetShaders();

        // Draw frame
        int frameX = x - getWidth() / 2;
        int frameY = y - getHeight();
        drawFrame(context, frameX, frameY, hasVehicle);

        // Draw gauges (This isn't even the right word for it LMAO)
        drawHealthGauge(context, frameX + 4, frameY + 4, playerEntity);
        drawAirGauge(context, frameX + getWidth() + 13, frameY + 3, playerEntity);
        if(!hasVehicle || !(playerEntity.getVehicle() instanceof LivingEntity vehicle)) {
            drawHydrationGauge(context, frameX + 278, frameY + 4);
            drawHungerGauge(context, frameX + 261, frameY + 4);
        }
        else {
            drawVehicleHealthGauge(context, frameX + 261, frameY + 4, vehicle);
        }

        // Draw statues
        drawStatue(context, (x - (getWidth() / 2)) - 11, y - 49, false);
        drawStatue(context, (x + (getWidth() / 2)) - 11, y - 49, true);

        // Draw misc. widgets
        drawLatencyMeter(context, frameX + 66, frameY + 19);
        if (!hasVehicle) drawExperienceBar(context, frameX + 48, frameY + 11, playerEntity);
        else drawMountJumpBar(context, frameX + 48, frameY + 11, (ClientPlayerEntity) playerEntity);
        drawLevelMeter(context, frameX + 34, frameY - 7, playerEntity);

        // Render hotbar items
        drawHotbarItems(context, (frameX + 75), y - 19, tickDelta, playerEntity);

        // Render selected slot arrow
        getInGameHud().client.getProfiler().push("slotArrow");
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 999);
        drawTexture(context, (frameX + 73) + playerEntity.getInventory().selectedSlot * 20, frameY + 18, 20, 240, 22, 22);
        context.getMatrices().pop();
        getInGameHud().client.getProfiler().pop();

        RenderSystem.disableBlend();
        getInGameHud().client.getProfiler().pop();
    }

    private void drawFrame(DrawContext context, int x, int y, boolean hasVehicle) {
        getInGameHud().client.getProfiler().push("frame");

        if(hasVehicle) {
            // If we have a vehicle we need to split the frame in half to render a full container on the right
            drawTexture(context, x, y, 0, 49, 257, 40);
            drawTexture(context, x + 257, y, 257, 89, 40, 40);
        }
        else {
            // If we don't have a vehicle we can simplify this down to a single draw call
            drawTexture(context, x, y, 0, 49, 297, 40);
        }

        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Draws health gauge fluid thing yeah.
     * @param context the context of the drawing of the pixels
     * @param x the X coordinate of the gauge
     * @param y the Y coordinate of the gauge
     */
    private void drawHealthGauge(DrawContext context, int x, int y, PlayerEntity playerEntity) {
        getInGameHud().client.getProfiler().push("healthMeter");

        // calculate shite
        float healthPercent = (float) health / maxHealth;

        // If there is an actionbar message, we are not receiving MMO data. Use vanilla data instead to avoid hitches in HUD
        if(getInGameHud().overlayMessage != null && getInGameHud().overlayRemaining > 0) {
            healthPercent = playerEntity.getHealth() / playerEntity.getMaxHealth();
        }

        int gaugeHeight = (int) (32.f * healthPercent);
        int yOffset = 32 - gaugeHeight;

        if(playerEntity.hasStatusEffect(StatusEffects.WITHER)) {
            // Withered
            drawTexture(context, x, y + yOffset, 116, 99 + yOffset, 32, gaugeHeight);
        }
        else if(playerEntity.hasStatusEffect(StatusEffects.POISON)) {
            // Poisoned
            drawTexture(context, x, y + yOffset, 32, 99 + yOffset, 32, gaugeHeight);
        }
        else {
            // Normal
            drawTexture(context, x, y + yOffset, 0, 99 + yOffset, 32, gaugeHeight);
        }

        IngameHudConfig ighConfig = BlockgameEnhanced.getConfig().getIngameHudConfig();
        if(!ighConfig.showAdvancedStats) {
            TextRenderer textRenderer = getInGameHud().getTextRenderer();
            String healthVal = String.valueOf(health);

            if(getInGameHud().overlayMessage != null && getInGameHud().overlayRemaining > 0) {
                float calc = maxHealth * healthPercent;
                healthVal = String.valueOf((int) calc);
            }

            RenderSystem.enableBlend();
            int centerX = x + 16;
            int centerY = y + 16;
            int txtWidth = textRenderer.getWidth(healthVal);
            int txtHeight = textRenderer.fontHeight;

            context.drawText(textRenderer, healthVal, (int) (centerX - (txtWidth / 2.f) + 2), (int) (centerY - (txtHeight / 2.f) + 1), 0x55000000, false);
            context.drawText(textRenderer, healthVal, (int) (centerX - (txtWidth / 2.f) + 1), (int) (centerY - (txtHeight / 2.f)), 0xFFFFFF, false);
            RenderSystem.disableBlend();
            resetShaders();
        }

        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Draws vehicle health gauge fluid thing yeah.
     * @param context the context of the drawing of the pixels
     * @param x the X coordinate of the gauge
     * @param y the Y coordinate of the gauge
     */
    private void drawVehicleHealthGauge(DrawContext context, int x, int y, LivingEntity vehicle) {
        getInGameHud().client.getProfiler().push("vehicleHealthMeter");

        // calculate shite
        float healthPercent = vehicle.getHealth() / vehicle.getMaxHealth();
        int gaugeHeight = (int) (32.f * healthPercent);
        int yOffset = 32 - gaugeHeight;

        drawTexture(context, x, y + yOffset, 84, 99 + yOffset, 32, gaugeHeight);

        IngameHudConfig ighConfig = BlockgameEnhanced.getConfig().getIngameHudConfig();
        if(!ighConfig.showAdvancedStats) {
            TextRenderer textRenderer = getInGameHud().getTextRenderer();
            String healthVal = String.valueOf((int) vehicle.getHealth());

            RenderSystem.enableBlend();
            int centerX = x + 16;
            int centerY = y + 16;
            int txtWidth = textRenderer.getWidth(healthVal);
            int txtHeight = textRenderer.fontHeight;

            // this will be good eventually
            context.drawText(textRenderer, healthVal, (int) (centerX - (txtWidth / 2.f)), (int) (centerY - (txtHeight / 2.f) + 1), 0x55000000, false);
            context.drawText(textRenderer, healthVal, (int) (centerX - (txtWidth / 2.f) - 1), (int) (centerY - (txtHeight / 2.f)), 0xFFFFFF, false);
            RenderSystem.disableBlend();
            resetShaders();
        }

        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Draws hunger gauge fluid thing yeah.
     * @param context the context of the drawing of the pixels
     * @param x the X coordinate of the gauge
     * @param y the Y coordinate of the gauge
     */
    private void drawHungerGauge(DrawContext context, int x, int y) {
        PlayerEntity player = getInGameHud().getCameraPlayer();
        if(player == null) return;

        getInGameHud().client.getProfiler().push("hungerMeter");

        // calculate shite
        float hungerPercent = (float) hunger / 20;

        // If there is an actionbar message, we are not receiving MMO data. Use vanilla data instead to avoid hitches in HUD
        if(getInGameHud().overlayMessage != null && getInGameHud().overlayRemaining > 0) {
            hungerPercent = player.getHungerManager().getFoodLevel() / 20.f;
        }

        int gaugeHeight = (int) (32.f * hungerPercent);
        int yOffset = 32 - gaugeHeight;

        if(player.hasStatusEffect(StatusEffects.HUNGER)) {
            // Hunger
            drawTexture(context, x, y + yOffset, 15, 163 + yOffset, 15, gaugeHeight);
        }
        else {
            // Normal
            drawTexture(context, x, y + yOffset, 0, 163 + yOffset, 15, gaugeHeight);
        }

        IngameHudConfig ighConfig = BlockgameEnhanced.getConfig().getIngameHudConfig();
        if(!ighConfig.showAdvancedStats && hungerPercent > 0.0f) {
            TextRenderer textRenderer = getInGameHud().getTextRenderer();
            String hungerVal = String.valueOf(player.getHungerManager().getFoodLevel());

            RenderSystem.enableBlend();
            float centerX = x + 7.5f;
            int centerY = y + 16;
            int txtWidth = textRenderer.getWidth(hungerVal);
            int txtHeight = textRenderer.fontHeight;

            context.drawText(textRenderer, hungerVal, (int) (centerX - (txtWidth / 2.f) + 1), (int) (centerY - (txtHeight / 2.f) + 1), 0x55000000, false);
            context.drawText(textRenderer, hungerVal, (int) (centerX - (txtWidth / 2.f)), (int) (centerY - (txtHeight / 2.f)), 0xFFFFFF, false);
            RenderSystem.disableBlend();
            resetShaders();
        }

        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Draws hydration gauge fluid thing yeah.
     * @param context the context of the drawing of the pixels
     * @param x the X coordinate of the gauge
     * @param y the Y coordinate of the gauge
     */
    private void drawHydrationGauge(DrawContext context, int x, int y) {
        PlayerEntity player = getInGameHud().getCameraPlayer();
        if(player == null) return;

        getInGameHud().client.getProfiler().push("hydrationMeter");

        // calculate shite
        float hydratePercent = hydration / 20.f;
        int gaugeHeight = (int) (32.f * hydratePercent);
        int yOffset = 32 - gaugeHeight;

        drawTexture(context, x, y + yOffset, 0, 131 + yOffset, 15, gaugeHeight);

        IngameHudConfig ighConfig = BlockgameEnhanced.getConfig().getIngameHudConfig();
        if(!ighConfig.showAdvancedStats && hydratePercent > 0.0f) {
            TextRenderer textRenderer = getInGameHud().getTextRenderer();
            String hydrateVal = String.valueOf((int) hydration);

            RenderSystem.enableBlend();
            int centerX = x + 7;
            int centerY = y + 16;
            int txtWidth = textRenderer.getWidth(hydrateVal);
            int txtHeight = textRenderer.fontHeight;

            context.drawText(textRenderer, hydrateVal, (int) (centerX - (txtWidth / 2.f) + 1), (int) (centerY - (txtHeight / 2.f) + 1), 0x55000000, false);
            context.drawText(textRenderer, hydrateVal, (int) (centerX - (txtWidth / 2.f)), (int) (centerY - (txtHeight / 2.f)), 0xFFFFFF, false);
            RenderSystem.disableBlend();
            resetShaders();
        }

        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Draws air gauge air thing yeah.
     * @param context the context of the drawing of the pixels
     * @param x the X coordinate of the gauge
     * @param y the Y coordinate of the gauge
     * @param playerEntity the player entity to render air meter of
     */
    private void drawAirGauge(DrawContext context, int x, int y, PlayerEntity playerEntity) {
        if(playerEntity.getAir() != playerEntity.getMaxAir()) {
            getInGameHud().client.getProfiler().push("airMeter");

            float airPercent = (float) playerEntity.getAir() / playerEntity.getMaxAir();
            int yOffset = (int)(airPercent * 27.f);

            // Draw bottle and air filler
            RenderSystem.enableBlend();
            drawTexture(context, x, y, 48, 12, 18, 37);
            drawTexture(context, x + 2, y + 8 + yOffset, 66, 20 + yOffset, 14, 27 - yOffset);
            RenderSystem.disableBlend();

            getInGameHud().client.getProfiler().pop();
        }
    }

    /**
     * Draws the latency meter.
     * @param context the context of the drawing of the pixels
     * @param x the Y coordinate of the meter
     * @param y the Y coordinate of the meter
     */
    private void drawLatencyMeter(DrawContext context, int x, int y) {
        getInGameHud().client.getProfiler().push("latencyMeter");

        int latency = BlockgameEnhancedClient.getLatency();
        int severity = 0;

        if(latency > 100) severity = 1;
        if(latency > 175) severity = 2;
        if(latency > 215) {
            severity = 3;
            latency = 215; // clamp it here so bar doesn't get empty
        }

        int yOffset = (int) (((float) latency / 250.f) * 20); // Add a little more so the bar is always visible
        drawTexture(context, x, y + yOffset, severity * 5, 195 + yOffset, 5, 20 - yOffset);

        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Draws the meter that shows what level you are.
     * @param context the context of the drawing of the pixels
     * @param x the Y coordinate of the meter
     * @param y the Y coordinate of the meter
     * @param playerEntity the player entity to render level of
     */
    private void drawLevelMeter(DrawContext context, int x, int y, PlayerEntity playerEntity) {
        getInGameHud().client.getProfiler().push("levelMeter");

        // Draw frame
        drawTexture(context, x, y, 0, 215, 25, 25);

        // Draw text
        TextRenderer textRenderer = getInGameHud().client.textRenderer;
        String string = String.valueOf(playerEntity.experienceLevel);
        int tx = x + 13 - (textRenderer.getWidth(string) / 2);
        int ty = y + 13 - (textRenderer.fontHeight / 2);

        context.drawText(getInGameHud().getTextRenderer(), string, tx, ty, 0xD1B945, false);
        getInGameHud().client.getProfiler().pop();

        // I find it fucking absurd that I need to do this after rendering text. What the hell Mojang
        resetShaders();
    }

    /**
     * Draws the experience bar.
     * @param context the context of the drawing of the pixels
     * @param x the Y coordinate of the bar
     * @param y the Y coordinate of the bar
     * @param playerEntity the player entity to experience progress of
     */
    private void drawExperienceBar(DrawContext context, int x, int y, PlayerEntity playerEntity) {
        getInGameHud().client.getProfiler().push("expBar");

        int nle = playerEntity.getNextLevelExperience();
        if(nle > 0) {
            int fill = (int) (playerEntity.experienceProgress * 210.f);
            drawTexture(context, x, y, 0, 89, fill, 5);
        }

        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Draws the mount jump bar.
     * @param context the context of the drawing of the pixels
     * @param x the Y coordinate of the bar
     * @param y the Y coordinate of the bar
     * @param playerEntity the player entity to render jump bar for
     */
    private void drawMountJumpBar(DrawContext context, int x, int y, ClientPlayerEntity playerEntity) {
        getInGameHud().client.getProfiler().push("mountJumpBar");

        float mjs = playerEntity.getMountJumpStrength();
        if(mjs > 0.f) {
            int fill = (int) (mjs * 210.f);
            drawTexture(context, x, y, 0, 94, fill, 5);
        }

        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Helper function to draw the statues at either side of the hotbar.
     * @param context the context of the drawing of the pixels
     * @param x the X coordinate of the statue
     * @param y the Y coordinate of the statue
     * @param flipped if the statue should face left or right
     */
    private void drawStatue(DrawContext context, int x, int y, boolean flipped) {
        getInGameHud().client.getProfiler().push("statue");
        drawTexture(context, x, y, flipped ? 23 : 0, 0, 23, 49);
        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Helper function to call InGameHud.drawTexture with our custom texture dimensions.
     * @param context the context of the drawing of the pixels
     * @param x the X coordinate of the rectangle
     * @param y the Y coordinate of the rectangle
     * @param u the left-most coordinate of the texture region
     * @param v the top-most coordinate of the texture region
     * @param width the width
     * @param height the height
     */
    private void drawTexture(DrawContext context, int x, int y, int u, int v, int width, int height) {
        context.drawTexture(WIDGETS_TEXTURE, x, y, u, v, width, height, 297, 263);
    }

    /**
     * Draws the inventory items & item selector on the hotbar.
     * @param context the context of the drawing of the pixels
     * @param x the X coordinate of item list
     * @param y the Y coordinate of item list
     * @param tickDelta subtick delta value
     * @param playerEntity player entity to draw hotbar items from
     */
    private void drawHotbarItems(DrawContext context, int x, int y, float tickDelta, PlayerEntity playerEntity) {
        getInGameHud().client.getProfiler().push("items");

        int slotSeed = 1;
        int slotIndex;
        int slotX;
        for (slotIndex = 0; slotIndex < 9; ++slotIndex) {
            slotX = x + (slotIndex * 20);
            getInGameHud().renderHotbarItem(context, slotX, y, tickDelta, playerEntity, playerEntity.getInventory().main.get(slotIndex), slotSeed++);
        }

        // Draw offhand item
        ItemStack offHandStack = playerEntity.getOffHandStack();
        if (!offHandStack.isEmpty()) {
            getInGameHud().renderHotbarItem(context, x - 29, y, tickDelta, playerEntity, offHandStack, slotSeed++);
        }

        resetShaders(); // Again, what the hell Mojang
        getInGameHud().client.getProfiler().pop();
    }

    /**
     * Resets the shaders to prepare for drawing immersive widget sprites.
     */
    private void resetShaders() {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.enableBlend();
    }
}
