package dev.jb0s.blockgameenhanced.gamefeature.mmoitems;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.chat.ReceiveChatMessageEvent;
import dev.jb0s.blockgameenhanced.event.gamefeature.mmoitems.ItemUsageEvent;
import dev.jb0s.blockgameenhanced.event.renderer.item.ItemRendererDrawEvent;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.helper.MMOItemHelper;
import dev.jb0s.blockgameenhanced.helper.NetworkHelper;
import lombok.Getter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Map;

public class MMOItemsGameFeature extends GameFeature {
    /**
     * Allowable margin of error (both directions) to match an event from the timeline.
     */
    private static final int LATENCY_MARGIN_OF_ERROR = 15;

    @Getter
    private int heartbeatLatency;

    @Getter
    private boolean isClientCaughtUp;

    private final Gson gson = new Gson();
    private final Map<String, MMOItemsCooldownEntry> cooldownEntryMap = Maps.newHashMap();
    private final ArrayList<ScheduledItemUsePacket> scheduledPackets = new ArrayList<>();
    private final ArrayList<ItemUsageEvent> capturedItemUsages = new ArrayList<>();
    private int tick;

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);

        UseBlockCallback.EVENT.register(this::preventIllegalMMOItemsInteraction);
        UseItemCallback.EVENT.register(this::repeatItemUseForCooldownMessage);
        ReceiveChatMessageEvent.EVENT.register(this::visualizeCooldown);
        ItemRendererDrawEvent.EVENT.register(this::drawItemCooldownOverlay);
        ItemRendererDrawEvent.EVENT.register(this::drawItemChargeCounter);
        ClientPlayConnectionEvents.JOIN.register((x, y, z) -> reset());
        ClientPlayConnectionEvents.DISCONNECT.register((x, y) -> reset());
    }

    /**
     * Draws charge count for items with charges
     */
    private ActionResult drawItemChargeCounter(TextRenderer textRenderer, ItemStack itemStack, int x, int y, String countLabel) {
        NbtCompound nbt = itemStack.getOrCreateNbt();

        if(BlockgameEnhanced.isNotkerMmoPresent()) {
            // Compatibility with Notker's McMMO Item Durability viewer.
            return ActionResult.PASS;
        }

        MatrixStack matrixStack = new MatrixStack();
        if(nbt.getInt("MMOITEMS_MAX_CONSUME") != 0 && itemStack.getCount() == 1) {
            String chargeCountString = countLabel == null ? String.valueOf(nbt.getInt("MMOITEMS_MAX_CONSUME")) : countLabel;
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

            matrixStack.translate(0.0, 0.0, (MinecraftClient.getInstance().inGameHud.getZOffset() + 300.0f));

            // Shitty outline (Notch did it first!)
            textRenderer.draw(chargeCountString, (float)(x + 19 - 2 - textRenderer.getWidth(chargeCountString)) + 1, (float)(y + 6 + 3), 0x000000, false, matrixStack.peek().getPositionMatrix(), immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            textRenderer.draw(chargeCountString, (float)(x + 19 - 2 - textRenderer.getWidth(chargeCountString)) - 1, (float)(y + 6 + 3), 0x000000, false, matrixStack.peek().getPositionMatrix(), immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            textRenderer.draw(chargeCountString, (float)(x + 19 - 2 - textRenderer.getWidth(chargeCountString)), (float)(y + 6 + 3) + 1, 0x000000, false, matrixStack.peek().getPositionMatrix(), immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            textRenderer.draw(chargeCountString, (float)(x + 19 - 2 - textRenderer.getWidth(chargeCountString)), (float)(y + 6 + 3) - 1, 0x000000, false, matrixStack.peek().getPositionMatrix(), immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);

            matrixStack.translate(0.0, 0.0, 0.001f);
            textRenderer.draw(chargeCountString, (float)(x + 19 - 2 - textRenderer.getWidth(chargeCountString)), (float)(y + 6 + 3), 0x7EFC20, false, matrixStack.peek().getPositionMatrix(), immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);

            immediate.draw();
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    /**
     * Draw cooldown for items
     */
    private ActionResult drawItemCooldownOverlay(TextRenderer textRenderer, ItemStack itemStack, int x, int y, String countLabel) {
        NbtCompound nbt = itemStack.getOrCreateNbt();
        String tag = nbt.getString("MMOITEMS_ABILITY");
        if(tag != null) {
            MMOItemsAbility[] itemAbilities = gson.fromJson(tag, MMOItemsAbility[].class);
            if(itemAbilities != null && itemAbilities.length > 0) {
                float cooldownProgressForThisStack = getCooldownProgress(itemAbilities[0].Id, MinecraftClient.getInstance().getTickDelta());
                if (cooldownProgressForThisStack > 0.0f) {
                    RenderSystem.disableDepthTest();
                    RenderSystem.disableTexture();
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    Tessellator tessellator2 = Tessellator.getInstance();
                    BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
                    renderGuiQuad(bufferBuilder2, x, y + MathHelper.floor(16.0f * (1.0f - cooldownProgressForThisStack)), 16, MathHelper.ceil(16.0f * cooldownProgressForThisStack), 255, 255, 255, 127);
                    RenderSystem.enableTexture();
                    RenderSystem.enableDepthTest();
                }
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void tick() {
        ++tick;

        if(getMinecraftClient().player == null) {
            return;
        }

        // Remove expired cooldowns
        cooldownEntryMap.entrySet().removeIf(x -> tick > x.getValue().endTick);

        // Send any scheduled packets
        if(!scheduledPackets.isEmpty()) {
            if(getMinecraftClient().getNetworkHandler() == null) {
                scheduledPackets.clear();
                return;
            }

            ScheduledItemUsePacket[] list = getCooldownPacketsSafe();
            for(ScheduledItemUsePacket packet : list) {
                // If it is not time to send this packet yet, skip it
                if (packet.endTick > tick) {
                    continue;
                }

                // Send the packet to trigger a cooldown message
                getMinecraftClient().getNetworkHandler().sendPacket(packet.packet);
            }

            // Remove expired packets (the ones we've just sent)
            scheduledPackets.removeIf(x -> tick > x.endTick);
        }

        heartbeatLatency = NetworkHelper.getNetworkLatency(getMinecraftClient().player);

        // Eventually we want to stop using the pre-login ping, so once the client is caught up we can stop using that.
        // Additionally, if 3500 ticks have passed, and we're still not caught up, the pre-login ping might have been a fluke. Discard it.
        if((tick > 3500 && (BlockgameEnhancedClient.getPreloginLatency() - heartbeatLatency > LATENCY_MARGIN_OF_ERROR)) && !isClientCaughtUp) {
            isClientCaughtUp = true;
            //catchUpReason = "Pre-login appears to be a fluke";
        }
        else if(BlockgameEnhancedClient.getPreloginLatency() - heartbeatLatency < LATENCY_MARGIN_OF_ERROR) {
            isClientCaughtUp = true;
            //catchUpReason = "Client is reasonable";
        }

        // Update latency value
        BlockgameEnhancedClient.setLatency(getLatency());

        // removeme
        if(BlockgameEnhanced.DEBUG) {
            getMinecraftClient().player.sendMessage(Text.of("§bNum cooldowns: §e" + cooldownEntryMap.size() + "§7 | §bNum sched paks: §e" + scheduledPackets.size()), true);
        }
    }

    /**
     * Callback that checks if the player is trying to place an MMOItem that has interaction disabled, and blocks doing so.
     * @param playerEntity The Player Entity that is trying to place the block.
     * @param world The world in which the Player Entity is trying to place the block.
     * @param hand The hand that contains the block the Player Entity is trying to place.
     * @param blockHitResult The hit result where the block should be placed.
     * @return ActionResult.PASS if the placement is allowed, ActionResult.FAIL if not.
     */
    public ActionResult preventIllegalMMOItemsInteraction(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
        ItemStack handItem = playerEntity.getStackInHand(hand);
        NbtCompound nbt = handItem.getOrCreateNbt();

        // If we have a tag named MMOITEMS_DISABLE_INTERACTION set to true, we need to block placement.
        // If there's a block entity where we clicked, and the player is not sneaking, then the player
        // is trying to interact with a block entity. In that case, we need to let it pass through.
        if(nbt.getBoolean("MMOITEMS_DISABLE_INTERACTION")) {
            BlockEntity b = world.getBlockEntity(blockHitResult.getBlockPos());
            boolean isTryingToInteractWithBlockEntity = b != null && !playerEntity.isSneaking();

            return isTryingToInteractWithBlockEntity ? ActionResult.PASS : ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }

    /**
     * Callback that repeats a right click action so that the server sends the client a cooldown value to use in the hotbar.
     * @param playerEntity The Player Entity that is trying to use an item.
     * @param world The world in which the Player Entity is trying to use an item.
     * @param hand The hand that contains the item the Player Entity is trying to use.
     * @return Always returns PASS, whether the routine was successful or not.
     */
    public TypedActionResult<ItemStack> repeatItemUseForCooldownMessage(PlayerEntity playerEntity, World world, Hand hand) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        ItemStack stack = playerEntity.getStackInHand(hand);

        boolean moduleEnabled = BlockgameEnhanced.getConfig().getIngameHudConfig().showCooldownsInHotbar;
        if(!moduleEnabled) return TypedActionResult.pass(stack);

        if(interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
            return TypedActionResult.pass(stack);
        }

        // If item in hand doesn't have an MMOItems ability, skip
        if(!MMOItemHelper.hasMMOAbility(stack)) {
            return TypedActionResult.pass(stack);
        }

        // This item has an ability, resend the right click packet to trigger a cooldown message from the server, which we then use for the hotbar
        scheduledPackets.add(new ScheduledItemUsePacket(new PlayerInteractItemC2SPacket(hand), tick, tick + 2));
        captureItemUsage(stack);

        return TypedActionResult.pass(stack);
    }

    /**
     * Callback that scans chat messages for cooldown values to display in the hotbar.
     * @param client The MinecraftClient instance.
     * @param message The received message in String format.
     * @return Always returns PASS, whether the routine was successful or not.
     */
    public ActionResult visualizeCooldown(MinecraftClient client, String message) {
        boolean moduleEnabled = BlockgameEnhanced.getConfig().getIngameHudConfig().showCooldownsInHotbar;
        if(!moduleEnabled) return ActionResult.PASS;

        if(!message.startsWith("[CD]")) {
            return ActionResult.PASS;
        }

        ItemUsageEvent itemUsage = getItemUsage();
        if(itemUsage == null) {
            return ActionResult.PASS;
        }

        ItemStack stack = itemUsage.getItemStack();

        // If item in hand doesn't have an MMOItems ability, skip
        if(!MMOItemHelper.hasMMOAbility(stack)) {
            return ActionResult.PASS;
        }

        // Extract cooldown length from chat message
        String[] spl = message.split(" ");
        String sec = spl[1].replace("s", "").trim();
        float fSec = Float.parseFloat(sec);
        int ticks = (int)(fSec * 20);

        // Get MMOAbility and set a cooldown for it
        String abil = MMOItemHelper.getMMOAbility(stack);
        if(abil != null) {
            setCooldown(abil, ticks);
        }

        return ActionResult.SUCCESS;
    }

    /**
     * Is "ability" currently cooling down?
     * @param ability Ability ID, e.g. "FROSTBOLT"
     * @return Whether this ability is currently on cooldown.
     */
    public boolean isCoolingDown(String ability) {
        return getCooldownProgress(ability, 0.0f) > 0;
    }

    /**
     * Range 0-1 determining the percentage of completion of a cooldown.
     * @param ability Ability ID, e.g. "FROSTBOLT"
     * @param partialTicks Value to make up for a small imprecision mid-tick, in most cases this will be deltaSeconds.
     * @return Float ranging 0-1.
     */
    public float getCooldownProgress(String ability, float partialTicks) {
        MMOItemsCooldownEntry entry = cooldownEntryMap.get(ability);
        if(entry != null) {
            float f = entry.endTick - entry.startTick;
            float g = (float) entry.endTick - ((float)tick + partialTicks);
            return MathHelper.clamp(g / f, 0.0f, 1.0f);
        }

        return 0.0f;
    }

    /**
     * Set an ability's cooldown length in ticks.
     * @param ability Ability ID, e.g. "FROSTBOLT"
     * @param durationTicks
     */
    public void setCooldown(String ability, int durationTicks) {
        if(!cooldownEntryMap.containsKey(ability)) {
            cooldownEntryMap.put(ability, new MMOItemsCooldownEntry(tick, tick + durationTicks));
        }
    }

    /**
     * Clear an ability's cooldown.
     * @param ability Ability ID, e.g. "FROSTBOLT"
     */
    public void removeCooldown(String ability) {
        cooldownEntryMap.remove(ability);
    }

    public void captureItemUsage(ItemStack itemStack) {
        ItemUsageEvent event = new ItemUsageEvent(itemStack);
        capturedItemUsages.add(event);
    }

    /**
     * Resets all the values.
     */
    private void reset() {
        tick = 0;
        isClientCaughtUp = false;
        heartbeatLatency = 0;
        cooldownEntryMap.clear();
        scheduledPackets.clear();
        capturedItemUsages.clear();
    }

    public ItemUsageEvent getItemUsage() {
        int latency = getLatency();
        return getItemUsage(latency);
    }

    public ItemUsageEvent getItemUsage(int latency) {
        long targetLatency = System.currentTimeMillis() - latency;

        ItemUsageEvent winning = null;
        long winningLatency = Long.MAX_VALUE;

        for (ItemUsageEvent e : capturedItemUsages) {
            long dif = Math.abs(e.getTimeMs() - targetLatency);

            if(dif < winningLatency) {
                winningLatency = dif;
                winning = e;
            }
        }

        return winning;
    }

    /**
     * Calculates a reliable latency value.
     * @return Pre-login or Heartbeat latency depending on which is more accurate at the time.
     */
    public int getLatency() {
        int hb = heartbeatLatency;
        int pl = BlockgameEnhancedClient.getPreloginLatency();
        int dif = BlockgameEnhancedClient.getPreloginLatency() - heartbeatLatency;
        return (dif > LATENCY_MARGIN_OF_ERROR && !isClientCaughtUp) ? pl : hb;
    }

    /**
     * Gets a clone of the cooldown hashmap to avoid multithread madness.
     * todo: Replace with a better solution that doesn't impact memory. (not like this game isn't garbage memory wise anyways)
     */
    private MMOItemsCooldownEntry[] getCooldownsSafe() {
        return cooldownEntryMap.values().toArray(new MMOItemsCooldownEntry[0]);
    }

    /**
     * Gets a clone of the scheduled packet list to avoid multithread madness.
     * todo: Replace with a better solution that doesn't impact memory. (not like this game isn't garbage memory wise anyways)
     */
    private ScheduledItemUsePacket[] getCooldownPacketsSafe() {
        return scheduledPackets.toArray(new ScheduledItemUsePacket[0]);
    }

    /**
     * Renders a quad on the GUI. Adapted from ItemRenderer.class
     */
    private void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x + 0, y + 0, 0.0).color(red, green, blue, alpha).next();
        buffer.vertex(x + 0, y + height, 0.0).color(red, green, blue, alpha).next();
        buffer.vertex(x + width, y + height, 0.0).color(red, green, blue, alpha).next();
        buffer.vertex(x + width, y + 0, 0.0).color(red, green, blue, alpha).next();
        buffer.end();
        BufferRenderer.draw(buffer);
    }

    record MMOItemsCooldownEntry(int startTick, int endTick) { }
    record ScheduledItemUsePacket(PlayerInteractItemC2SPacket packet, int startTick, int endTick) { }
}
