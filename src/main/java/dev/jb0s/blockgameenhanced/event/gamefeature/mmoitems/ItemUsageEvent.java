package dev.jb0s.blockgameenhanced.event.gamefeature.mmoitems;

import lombok.Getter;
import net.minecraft.item.ItemStack;

public class ItemUsageEvent extends LatencyEvent {
    @Getter
    private final ItemStack itemStack;

    public ItemUsageEvent(ItemStack itemStack) {
        super();
        this.itemStack = itemStack;
    }
}
