package dev.jb0s.blockgameenhanced.mixin.entity;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class MixinItemEntity {
    @Shadow private int itemAge;

    @Inject(method = "tick", at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        ItemEntity thisItemEntity = (ItemEntity) (Object) this;

        boolean enableItemLabels = BlockgameEnhanced.getConfig().getAccessibilityConfig().enableItemLabels;
        if(itemAge == 0 && thisItemEntity.getCustomName() == null && enableItemLabels) {
            ItemStack stack = thisItemEntity.getStack();

            Text entityName = Text.of("§7" + stack.getCount() + "x ").shallowCopy().append(stack.getName());
            thisItemEntity.setCustomNameVisible(true);
            thisItemEntity.setCustomName(entityName);
        }
    }
}
