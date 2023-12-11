package dev.jb0s.blockgameenhanced.event.renderer.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface ItemRendererDrawEvent {
    Event<ItemRendererDrawEvent> EVENT = EventFactory.createArrayBacked(ItemRendererDrawEvent.class, (listeners) -> (renderer, stack, x, y, countLabel) -> {
        for (ItemRendererDrawEvent listener : listeners) {
            ActionResult res = listener.drawItem(renderer, stack, x, y, countLabel);
            if(res != ActionResult.PASS) return res;
        }

        return ActionResult.PASS;
    });

    ActionResult drawItem(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel);
}
