package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.pickups;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.ImmersiveWidget;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ImmersivePickup extends ImmersiveWidget {

    @Getter
    private final Text itemName;

    @Getter
    private final ItemStack itemStack;

    @Getter
    @Setter
    private int amount;

    @Getter
    @Setter
    private int inactivityTicks;

    private final int ITEM_SIZE = 16;
    private final int PADDING = 5;
    private final int SPACING = 5;

    public ImmersivePickup(InGameHud inGameHud, Text itemName, ItemStack itemStack, int amount) {
        super(inGameHud);
        this.itemName = itemName;
        this.itemStack = itemStack;
        setAmount(amount);
        setInactivityTicks(0);
    }

    @Override
    public void render(DrawContext context, int x, int y, float tickDelta) {
        TextRenderer textRenderer = getInGameHud().getTextRenderer();
        ItemRenderer itemRenderer = getInGameHud().client.getItemRenderer();

        RenderSystem.enableBlend();
        context.fill(x, y, x + getWidth(), y + getHeight(), 135 << 24);

        int tx = x + PADDING;
        int ty = y + ((getHeight() / 2) - (textRenderer.fontHeight / 2));
        context.drawText(textRenderer, getText(), tx, ty, 0xFFFFFF, false);

        int ix = x + PADDING + getInGameHud().getTextRenderer().getWidth(getText()) + SPACING;
        int iy = y + ((getHeight() / 2) - (ITEM_SIZE / 2));
        // todo itemRenderer.renderGuiItemIcon(itemStack, ix, iy);

        RenderSystem.disableBlend();
    }

    @Override
    public void tick() {
        inactivityTicks++;
    }

    @Override
    public int getWidth() {
        return PADDING + getInGameHud().getTextRenderer().getWidth(getText()) + SPACING + ITEM_SIZE + PADDING;
    }

    @Override
    public int getHeight() {
        return 20;
    }

    private Text getText() {
        return Text.of("§7" + getAmount() + "x ").copy().append(getItemName());
    }

    /**
     * Increase amount of items picked up on this popup.
     * @param amount Amount of items picked up to add
     */
    public void addAmount(int amount) {
        setAmount(getAmount() + amount);
    }
}
