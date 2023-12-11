package dev.jb0s.blockgameenhanced.mixin.entity;

import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class MixinItemEntity {
    @Shadow private int itemAge;

    /**
     * We use 877 in color codes to determine whether an item label is given by the Blockgame mod or not.
     * Thor would NEVER use this signature, so it's pretty foolproof. Thor if you're reading this, don't be a goblin. That's my task.
     */
    private static final String MOD_ASSIGNED_LABEL_SIGNATURE = "§8§7§7";

    @Inject(method = "tick", at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        ItemEntity thisItemEntity = (ItemEntity) (Object) this;
        boolean enableItemLabels = BlockgameEnhanced.getConfig().getAccessibilityConfig().enableItemLabels;

        if(enableItemLabels && (thisItemEntity.getCustomName() == null || thisItemEntity.getCustomName().getString().startsWith(MOD_ASSIGNED_LABEL_SIGNATURE))) {
            giveItemEntityLabel(thisItemEntity);
        }
    }

    private static void giveItemEntityLabel(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getStack();
        boolean enableItemLabels = BlockgameEnhanced.getConfig().getAccessibilityConfig().enableItemLabels;

        if(enableItemLabels) {
            Text entityName = Text.of(MOD_ASSIGNED_LABEL_SIGNATURE + stack.getCount() + "x ").shallowCopy().append(stack.getName());
            itemEntity.setCustomNameVisible(true);
            itemEntity.setCustomName(entityName);
        }
    }
}
