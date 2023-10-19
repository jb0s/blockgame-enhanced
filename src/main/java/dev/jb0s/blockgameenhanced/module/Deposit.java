package dev.jb0s.blockgameenhanced.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.slot.SlotActionType;

public class Deposit {
    private static int waitingForSyncId;

    /**
     * Callback that checks if the menu that the server has opened is the Currency Deposit menu, and if so stores its sync id.
     * @param packet Packet data for the menu that has opened.
     */
    public static void handleScreenOpen(OpenScreenS2CPacket packet) {

        // If the menu is called "Deposit", store the sync id, so we can start putting currency in this menu
        // todo: This is easily forged by just creating a chest called "Deposit". We should do integrity checks
        if(packet.getName().asString().equals("Deposit")) {
            waitingForSyncId = packet.getSyncId();
            return;
        }

        waitingForSyncId = -1;
    }

    /**
     * Callback that checks if the inventory contents for a menu matches the sync id we've stored, and puts currency in the inventory if so.
     * @param packet Packet data for the inventory inside the menu.
     */
    public static void handleScreenInventoryData(InventoryS2CPacket packet) {
        if(packet.getSyncId() != waitingForSyncId) {
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity p = mc.player;
        Inventory inv = p.getInventory();

        for(int i = 0; i < 36; i++) {
            ItemStack item = inv.getStack(i);

            // Ensure that this is item is an MMOItems with a Tier
            NbtElement tier = item.getOrCreateNbt().get("MMOITEMS_TIER");
            if(tier == null) continue;

            // If the tier is CURRENCY, then send click packets to the server to move it to deposit menu
            if(tier.asString().equals("CURRENCY")) {
                int slot = i > 8 ? 26 + (i - 8) : 54 + i; // Weird hack to translate from Inventory Slot to ChestScreen Slot
                mc.interactionManager.clickSlot(waitingForSyncId, slot, 0, SlotActionType.QUICK_MOVE, p);
            }
        }

        // If we don't reset it right after, the client will absolutely SLAM the server with packets
        waitingForSyncId = -1;
    }
}
