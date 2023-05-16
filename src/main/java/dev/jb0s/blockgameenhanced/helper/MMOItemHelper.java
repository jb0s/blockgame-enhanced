package dev.jb0s.blockgameenhanced.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class MMOItemHelper {
    private static final String NBT_DURABILITY = "MMOITEMS_DURABILITY";
    private static final String NBT_MAX_DURABILITY = "MMOITEMS_MAX_DURABILITY";

    public static boolean hasMMODurability(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getOrCreateNbt();
        return nbt != null && nbt.getInt(NBT_MAX_DURABILITY) > 0;
    }

    public static int getMMOMaxDurability(ItemStack itemStack) {
        if(!hasMMODurability(itemStack)) {
            return 0;
        }

        NbtCompound nbt = itemStack.getOrCreateNbt();
        return nbt.getInt(NBT_MAX_DURABILITY);
    }

    public static int getMMODurability(ItemStack itemStack) {
        if(!hasMMODurability(itemStack)) {
            return 0;
        }

        NbtCompound nbt = itemStack.getOrCreateNbt();
        int mmoDurability = nbt.getInt(NBT_DURABILITY);

        // Edge case for max durability items
        if(mmoDurability == 0 && !nbt.contains(NBT_DURABILITY))
            return getMMOMaxDurability(itemStack);

        return mmoDurability;
    }

    public static int getMMODamage(ItemStack itemStack) {
        if(!hasMMODurability(itemStack)) {
            return 0;
        }

        int mmoMaxDurability = getMMOMaxDurability(itemStack);
        int mmoDurability = getMMODurability(itemStack);
        return mmoMaxDurability - mmoDurability;
    }
}
