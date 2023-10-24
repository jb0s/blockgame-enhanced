package dev.jb0s.blockgameenhanced.manager.latency.event;

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
