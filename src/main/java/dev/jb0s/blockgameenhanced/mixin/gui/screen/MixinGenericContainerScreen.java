package dev.jb0s.blockgameenhanced.mixin.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import lombok.SneakyThrows;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(GenericContainerScreen.class)
public class MixinGenericContainerScreen extends HandledScreen<GenericContainerScreenHandler> implements ScreenHandlerProvider<GenericContainerScreenHandler> {
    @Shadow @Final private int rows;

    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private static final TranslatableText LOOT_ALL_BUTTON = new TranslatableText("menu.blockgame.container.plunder");

    public MixinGenericContainerScreen(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init(); // right. forgot about that

        // If this is not a regular chest we're interacting with, don't add the loot all button
        String titleStr = title.getString();
        if(!titleStr.isEmpty() && !titleStr.endsWith("Chest")) {
            return;
        }

        int originX = width / 2;
        int originY = height / 2;
        int btnWidth = 47;
        int btnHeight = 12;

        int x = originX + 34;
        int y = originY - (backgroundHeight / 2) + 3;
        addDrawableChild(new ButtonWidget(x, y, btnWidth, btnHeight, LOOT_ALL_BUTTON, (button) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            ClientPlayerEntity p = mc.player;

            for(int i = 0; i < 9 * rows; i++) {
                mc.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.QUICK_MOVE, p);
            }
        }));
    }

    /**
     * Reimplementation from GenericContainerScreen
     * @param matrices n/a
     * @param delta n/a
     * @param mouseX n/a
     * @param mouseY n/a
     */
    @Override
    public void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.rows * 18 + 17);
        this.drawTexture(matrices, i, j + this.rows * 18 + 17, 0, 126, this.backgroundWidth, 96);
    }
}
