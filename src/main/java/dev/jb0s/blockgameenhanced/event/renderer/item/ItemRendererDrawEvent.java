package dev.jb0s.blockgameenhanced.event.renderer.item;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface ItemRendererDrawEvent {
    Event<ItemRendererDrawEvent> EVENT = EventFactory.createArrayBacked(ItemRendererDrawEvent.class, (listeners) -> (context, textRenderer, itemStack, x, y, countLabel) -> {
        for (ItemRendererDrawEvent listener : listeners) {
            ActionResult res = listener.drawItem(context, textRenderer, itemStack, x, y, countLabel);
            if(res != ActionResult.PASS) return res;
        }

        return ActionResult.PASS;
    });

    ActionResult drawItem(DrawContext context, TextRenderer textRenderer, ItemStack itemStack, int x, int y, String countLabel);
}
