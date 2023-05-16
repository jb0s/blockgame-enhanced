package dev.jb0s.blockgameenhanced.module;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class MMOItems {
    /**
     * Callback that check if the player is trying to place an MMOItem that has interaction disabled, and blocks doing so.
     * @param playerEntity The Player Entity that is trying to place the block.
     * @param world The world in which the Player Entity is trying to place the block.
     * @param hand The hand that contains the block the Player Entity is trying to place.
     * @param blockHitResult The hit result where the block should be placed.
     * @return ActionResult.PASS if the placement is allowed, ActionResult.FAIL if not.
     */
    public static ActionResult preventIllegalMMOItemsInteraction(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
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
}
