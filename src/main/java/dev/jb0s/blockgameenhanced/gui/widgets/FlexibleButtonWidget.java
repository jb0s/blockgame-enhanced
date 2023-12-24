package dev.jb0s.blockgameenhanced.gui.widgets;

import dev.jb0s.blockgameenhanced.helper.GUIHelper;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class FlexibleButtonWidget extends ButtonWidget {
    private static final Rectangle REGULAR_REGION = new Rectangle(0, 66, 200, 20);
    private static final Rectangle HOVERED_REGION = new Rectangle(0, 86, 200, 20);
    private static final Rectangle DISABLED_REGION = new Rectangle(0, 46, 200, 20);
    private static final Rectangle INNER_REGION = new Rectangle(2, 2, 196, 15);
    private PressAction rightClickAction;

    public FlexibleButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress);
    }

    public FlexibleButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, message, onPress, tooltipSupplier);
    }

    public void setRightClickAction(PressAction rightClickAction) {
        this.rightClickAction = rightClickAction;
    }

    @Override
    protected void renderBackground(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
        Rectangle rect = new Rectangle(this.x, this.y, this.width, this.height);
        Rectangle region = !this.active ? DISABLED_REGION : this.hovered ? HOVERED_REGION : REGULAR_REGION;

        GUIHelper.draw9SliceBg(matrices, WIDGETS_TEXTURE, rect, region, INNER_REGION);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1 && rightClickAction != null && this.clicked(mouseX, mouseY)) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.rightClickAction.onPress(this);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
