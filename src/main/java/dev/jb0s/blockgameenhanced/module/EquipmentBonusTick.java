package dev.jb0s.blockgameenhanced.module;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.helper.ExpHudDataHelper;
import dev.jb0s.blockgameenhanced.manager.config.modules.ExpHudConfig;
import dev.jb0s.blockgameenhanced.manager.config.modules.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class EquipmentBonusTick {
    private static int ticksToWait = 15;
    public static void tick(MinecraftClient client) {
        ExpHudConfig config = BlockgameEnhanced.getConfig().getExpHudConfig();

        if (ticksToWait <= 0 && config.expHudEnabled) {
            // Reset Time
            ticksToWait = 15;
            //Update Bonus Exp information
            playerEquipmentBonusExp();
        } else {
            ticksToWait--;
        }


    }

    private static void playerEquipmentBonusExp() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        // {"Archaeology", "Einherjar", "Fishing", "Herbalism", "Logging", "Mining", "Runecarving"};
        if (player != null) {
            //NbtCompound mainHand = player.getMainHandStack().getOrCreateNbt();
            NbtCompound offHand = player.getOffHandStack().getOrCreateNbt();
            ItemStack mainHand = player.getMainHandStack();
            float[] newEquipmentBonusExp = new float[ExpHudDataHelper.professionNames.length];

            // offhand MMOITEMS_HANDWORN:b1

            // Check if mainhand item is in Mainhand slot
            if (!mainHand.getOrCreateNbt().contains("MMOITEMS_HANDWORN") && !(mainHand.getItem() instanceof ArmorItem)){
                for (int i = 0; i < ExpHudDataHelper.nbtKeyNames.length; i++) {
                    newEquipmentBonusExp[i] = mainHand.getOrCreateNbt().getFloat(ExpHudDataHelper.nbtKeyNames[i]);
                }
            }

            // Check if offhand item is in Offhand slot
            if (offHand.contains("MMOITEMS_HANDWORN")){
                for (int i = 0; i < ExpHudDataHelper.nbtKeyNames.length; i++) {
                    newEquipmentBonusExp[i] += offHand.getFloat(ExpHudDataHelper.nbtKeyNames[i]);
                }
            }

            //Check Armor slots
            Iterable<ItemStack> equippedItems = player.getArmorItems();
            String[] setArmor = new String[4];
            int[] correspondingProfessionIndex = new int[4];
            int index = 0;
            for (ItemStack items : equippedItems) {
                //LOGGER.fatal(items.getOrCreateNbt());
                String setType = items.getOrCreateNbt().getString("MMOITEMS_ITEM_SET");
                for (int i = 0; i < ExpHudDataHelper.professionSetNames.length; i++) {
                    // Make sure it's a Profession Type set
                    if (setType.contains(ExpHudDataHelper.professionSetNames[i])){
                        //put Set Name in to array
                        setArmor[index] = setType;
                        correspondingProfessionIndex[index] = i;
                        // bump to next slot
                        index++;
                    }
                }
                // Add +EXP% values
                for (int i = 0; i < ExpHudDataHelper.nbtKeyNames.length; i++) {
                    newEquipmentBonusExp[i] += items.getOrCreateNbt().getFloat(ExpHudDataHelper.nbtKeyNames[i]);
                }
            }


            // Check for set bonus
            // Set Bonus ExP%
            String boots = setArmor[0] != null ? setArmor[0] : "0";
            String leggings = setArmor[1] != null ? setArmor[1] : "1";
            String chest = setArmor[2] != null ? setArmor[2] : "2";
            String helmet = setArmor [3] != null ? setArmor[3] : "3";

            //LOGGER.info(setArmor[0]);
            // T1 |  T2 |  T3 |  T4 |  T5
            // 10% | 20% | 30% | 40% | 50%
            if (helmet.equals(chest)
                    && helmet.equals(leggings)
                    && helmet.equals(boots)) {
                // 4 equal piece Matching Set Bonus
                //LOGGER.info("-> 4 Set");
                //LOGGER.info(professionNames[correspondingProfessionIndex[3]]);
                //LOGGER.fatal(Integer.parseInt(String.valueOf(setArmor[3].charAt(setArmor[3].length() - 1))) * 5 * 2 + "% Bonus");
                newEquipmentBonusExp[correspondingProfessionIndex[3]] += getSetBonusExpValue(setArmor[3], 1f);

            }

            /* else if (helmet.equals(chest) && helmet.equals(leggings)
            || helmet.equals(chest) && helmet.equals(boots)
            || helmet.equals(leggings) && helmet.equals(boots)) {
                // 3 piece Matching Sets
                newEquipmentBonusExp[correspondingProfessionIndex[3]] += getSetBonusExpValue(setArmor[3], 2f);

            } else if (chest.equals(leggings) && chest.equals(boots)) {
                // 3 piece Matching Sets
                newEquipmentBonusExp[correspondingProfessionIndex[2]] += getSetBonusExpValue(setArmor[2], 2f);

            } else {
                // only 2 pieces matching sets left
                if (helmet.equals(chest) && leggings.equals(boots) || helmet.equals(leggings) && chest.equals(boots)){
                    // 2 piece + 2 piece Matching Sets
                    newEquipmentBonusExp[correspondingProfessionIndex[3]] += getSetBonusExpValue(setArmor[3], 1f);
                    newEquipmentBonusExp[correspondingProfessionIndex[0]] += getSetBonusExpValue(setArmor[0], 1f);

                } else if (helmet.equals(boots) && chest.equals(leggings)) {
                    // 2 piece + 2 piece Matching Sets
                    newEquipmentBonusExp[correspondingProfessionIndex[3]] += getSetBonusExpValue(setArmor[3], 1f);
                    newEquipmentBonusExp[correspondingProfessionIndex[2]] += getSetBonusExpValue(setArmor[2], 1f);

                } else if (helmet.equals(chest) || helmet.equals(boots) || helmet.equals(leggings)) {
                    // 2 piece Matching Sets
                    newEquipmentBonusExp[correspondingProfessionIndex[3]] += getSetBonusExpValue(setArmor[3], 1f);

                } else if (chest.equals(leggings)) {
                    // 2 piece Matching Sets
                    newEquipmentBonusExp[correspondingProfessionIndex[2]] += getSetBonusExpValue(setArmor[2], 1f);

                } else if (boots.equals(leggings) || boots.equals(chest)) {
                    // 2 piece Matching Sets
                    newEquipmentBonusExp[correspondingProfessionIndex[0]] += getSetBonusExpValue(setArmor[0], 1f);
                }
            }*/

            // Set the new Bonus
            ExpHudDataHelper.equipmentBonusExpValues = newEquipmentBonusExp;
        }
    }

    private static float getSetBonusExpValue(String setType, float multiply) {
        if (setType == null) return 0f;
        // get the Set Tier lvl to multiply with
        int setLvl = Integer.parseInt(String.valueOf(setType.charAt(setType.length() - 1)));
        //LOGGER.info(setLvl * DEFAULT_BASE_BONUS_EXP * multiply);
        return setLvl * ExpHudDataHelper.DEFAULT_BASE_BONUS_EXP * multiply;
    }
}
