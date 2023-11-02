package dev.jb0s.blockgameenhanced.manager.mmoitems;

import com.google.common.collect.Maps;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.chat.ReceiveChatMessageEvent;
import dev.jb0s.blockgameenhanced.helper.MMOItemHelper;
import dev.jb0s.blockgameenhanced.manager.Manager;
import dev.jb0s.blockgameenhanced.manager.latency.LatencyManager;
import dev.jb0s.blockgameenhanced.manager.latency.event.ItemUsageEvent;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MMOItemsManager extends Manager {
    private final Map<String, MMOItemsCooldownEntry> cooldownEntryMap = Maps.newHashMap();
    private final ArrayList<ScheduledItemUsePacket> scheduledPackets = new ArrayList<>();
    private int tick;

    @Override
    public void init() {
        UseBlockCallback.EVENT.register(this::preventIllegalMMOItemsInteraction);
        UseItemCallback.EVENT.register(this::repeatItemUseForCooldownMessage);
        ReceiveChatMessageEvent.EVENT.register(this::visualizeCooldown);
    }

    @Override
    public void tick(MinecraftClient client) {
        ++tick;

        // Update cooldown list
        if(!cooldownEntryMap.isEmpty()) {
            Iterator<Map.Entry<String, MMOItemsCooldownEntry>> it = cooldownEntryMap.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, MMOItemsCooldownEntry> entry = it.next();
                if (entry.getValue().endTick > tick) {
                    continue;
                }

                it.remove();
            }
        }

        // Send any scheduled packets
        if(!scheduledPackets.isEmpty()) {
            if(client.getNetworkHandler() == null) {
                scheduledPackets.clear();
                return;
            }

            Iterator<ScheduledItemUsePacket> it = scheduledPackets.iterator();

            while (it.hasNext()) {
                ScheduledItemUsePacket pak = it.next();
                if (pak.endTick > tick) {
                    continue;
                }

                client.getNetworkHandler().sendPacket(pak.packet);
                it.remove();
            }
        }
    }

    @Override
    public List<String> getDebugStats() {
        ArrayList<String> lines = new ArrayList<>();

        boolean moduleEnabled = BlockgameEnhanced.getConfig().getIngameHudConfig().showCooldownsInHotbar;
        if(moduleEnabled) {
            lines.add("Tick: " + tick);
            lines.add("Cooldown items: " + cooldownEntryMap.size());
            if(!cooldownEntryMap.isEmpty()) {
                for (Map.Entry<String, MMOItemsCooldownEntry> entry : cooldownEntryMap.entrySet()) {
                    lines.add(" - " + entry + ": " + (entry.getValue().endTick - tick));
                }
            }
            return lines;
        }

        lines.add("Disabled by mod config");
        return lines;
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
        LatencyManager latencyManager = BlockgameEnhancedClient.getLatencyManager();
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
        scheduledPackets.add(new ScheduledItemUsePacket(new PlayerInteractItemC2SPacket(hand, 0), tick, tick + 2));
        latencyManager.captureItemUsage(stack);

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

        LatencyManager latencyManager = BlockgameEnhancedClient.getLatencyManager();
        ItemUsageEvent itemUsage = latencyManager.getItemUsage();
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

    record MMOItemsCooldownEntry(int startTick, int endTick) { }
    record ScheduledItemUsePacket(PlayerInteractItemC2SPacket packet, int startTick, int endTick) { }
}
