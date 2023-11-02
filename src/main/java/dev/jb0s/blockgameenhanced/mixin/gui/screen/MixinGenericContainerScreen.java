package dev.jb0s.blockgameenhanced.mixin.gui.screen;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GenericContainerScreen.class)
public class MixinGenericContainerScreen extends HandledScreen<GenericContainerScreenHandler> implements ScreenHandlerProvider<GenericContainerScreenHandler> {
    @Shadow @Final private int rows;

    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private static final Text LOOT_ALL_BUTTON = Text.translatable("menu.blockgame.container.plunder");

    public MixinGenericContainerScreen(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init(); // right. forgot about that

        // If this is not a regular chest we're interacting with, don't add the loot all button
        String titleStr = title.getString();
        boolean isValidContainer = titleStr.endsWith("Chest") || titleStr.endsWith("Shulker Box") || titleStr.endsWith("Barrel");
        if(!titleStr.isEmpty() && !isValidContainer) {
            return;
        }

        // If the user has disabled the Loot All button, cancel here
        if(!BlockgameEnhanced.getConfig().getAccessibilityConfig().enableLootAllButton) {
            return;
        }

        int originX = width / 2;
        int originY = height / 2;
        int btnWidth = 47;
        int btnHeight = 12;

        int x = originX + 34;
        int y = originY - (backgroundHeight / 2) + 3;
        addDrawableChild(ButtonWidget.builder(LOOT_ALL_BUTTON, (button) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            ClientPlayerEntity p = mc.player;

            for(int i = 0; i < 9 * rows; i++) {
                mc.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.QUICK_MOVE, p);
            }
        }).position(x, y).size(btnWidth, btnHeight).build());
    }

    /**
     * Reimplementation from GenericContainerScreen
     * @param context n/a
     * @param delta n/a
     * @param mouseX n/a
     * @param mouseY n/a
     */
    @Override
    public void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.rows * 18 + 17);
        context.drawTexture(TEXTURE, i, j + this.rows * 18 + 17, 0, 126, this.backgroundWidth, 96);
    }
}
