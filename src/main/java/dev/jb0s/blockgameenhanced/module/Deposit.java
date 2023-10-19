package dev.jb0s.blockgameenhanced.module;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.slot.SlotActionType;

public class Deposit {
    private static int waitingForSyncId;

    public static void handleScreenOpen(OpenScreenS2CPacket packet) {
        if(packet.getName().asString().equals("Deposit")) {
            waitingForSyncId = packet.getSyncId();
            BlockgameEnhanced.LOGGER.info("Waiting for Deposit screen content. Sync ID " + waitingForSyncId);
            return;
        }

        waitingForSyncId = -1;
    }

    public static void handleScreenInventoryData(InventoryS2CPacket packet) {
        if(packet.getSyncId() != waitingForSyncId) {
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity p = mc.player;
        Inventory inv = p.getInventory();

        for(int i = 0; i < 36; i++) {
            ItemStack item = inv.getStack(i);

            NbtElement tier = item.getOrCreateNbt().get("MMOITEMS_TIER");
            if(tier == null) continue;

            if(tier.asString().equals("CURRENCY")) {
                int slot = i > 8 ? 26 + (i - 8) : 54 + i;
                mc.interactionManager.clickSlot(waitingForSyncId, slot, 0, SlotActionType.QUICK_MOVE, p);
            }
        }

        BlockgameEnhanced.LOGGER.info("We ready bitch. Sync ID " + waitingForSyncId);
        waitingForSyncId = -1;
    }
}
