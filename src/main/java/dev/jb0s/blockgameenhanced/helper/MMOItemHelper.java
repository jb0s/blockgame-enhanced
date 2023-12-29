package dev.jb0s.blockgameenhanced.helper;

import com.google.gson.Gson;
import dev.jb0s.blockgameenhanced.gamefeature.mmoitems.MMOItemsAbility;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class MMOItemHelper {
    private static final String NBT_DURABILITY = "MMOITEMS_DURABILITY";
    private static final String NBT_MAX_DURABILITY = "MMOITEMS_MAX_DURABILITY";
    private static final String NBT_ABILITY = "MMOITEMS_ABILITY";
    public static final String NBT_ITEM_TYPE = "MMOITEMS_ITEM_TYPE";

    private static final Gson GSON = new Gson();

    public static boolean hasMMODurability(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getOrCreateNbt();
        return nbt != null && nbt.getInt(NBT_MAX_DURABILITY) > 0;
    }

    public static boolean hasMMOAbility(ItemStack itemStack) {
        String nbt = itemStack.getOrCreateNbt().getString(NBT_ABILITY);
        return nbt != null && !nbt.isEmpty();
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

    public static String getMMOAbility(ItemStack itemStack) {
        String nbt = itemStack.getOrCreateNbt().getString("MMOITEMS_ABILITY");
        if(nbt != null && !nbt.isEmpty()) {
            MMOItemsAbility[] itemAbilities = GSON.fromJson(nbt, MMOItemsAbility[].class);
            if(itemAbilities != null && itemAbilities.length > 0) {
                return itemAbilities[0].Id;
            }
        }

        return null;
    }
}
