package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

public class ImmersiveHotbar extends ImmersiveWidget {
    private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");

    public ImmersiveHotbar(InGameHud inGameHud) {
        super(inGameHud);
    }

    @Override
    public int getWidth() {
        return 182;
    }

    @Override
    public int getHeight() {
        return 22;
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, float tickDelta) {
        PlayerEntity playerEntity = getInGameHud().getCameraPlayer();
        if (playerEntity == null) {
            return;
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        ItemStack itemStack = playerEntity.getOffHandStack();
        Arm arm = playerEntity.getMainArm().getOpposite();
        int j = getInGameHud().getZOffset();
        getInGameHud().setZOffset(-90);
        getInGameHud().drawTexture(matrices, x - 91, y - getHeight(), 0, 0, getWidth(), getHeight());
        getInGameHud().drawTexture(matrices, x - 91 - 1 + playerEntity.getInventory().selectedSlot * 20, y - getHeight() - 1, 0, getHeight(), 24, getHeight());
        if (!itemStack.isEmpty()) {
            if (arm == Arm.LEFT) {
                getInGameHud().drawTexture(matrices, x - 91 - 29, y - 23, 24, getHeight(), 29, 24);
            } else {
                getInGameHud().drawTexture(matrices, x + 91, y - 23, 53, getHeight(), 29, 24);
            }
        }
        getInGameHud().setZOffset(j);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Render hotbar items
        int slotSeed = 1;
        int slotIndex;
        int slotX;
        int slotY;
        for (slotIndex = 0; slotIndex < 9; ++slotIndex) {
            slotX = x - 90 + slotIndex * 20 + 2;
            slotY = y - 19;
            getInGameHud().renderHotbarItem(slotX, slotY, tickDelta, playerEntity, playerEntity.getInventory().main.get(slotIndex), slotSeed);
        }
        if (!itemStack.isEmpty()) {
            slotIndex = y - 16 - 3;
            if (arm == Arm.LEFT) {
                getInGameHud().renderHotbarItem(x - 91 - 26, slotIndex, tickDelta, playerEntity, itemStack, slotSeed);
            } else {
                getInGameHud().renderHotbarItem(x + 91 + 10, slotIndex, tickDelta, playerEntity, itemStack, slotSeed);
            }
        }

        float attackCooldownProgress;
        if (getInGameHud().client.options.attackIndicator == AttackIndicator.HOTBAR && (attackCooldownProgress = getInGameHud().client.player.getAttackCooldownProgress(0.0f)) < 1.0f) {
            slotX = y - 20;
            slotY = x + 91 + 6;
            if (arm == Arm.RIGHT) {
                slotY = x - 91 - getHeight();
            }
            RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
            int q = (int)(attackCooldownProgress * 19.0f);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            getInGameHud().drawTexture(matrices, slotY, slotX, 0, 94, 18, 18);
            getInGameHud().drawTexture(matrices, slotY, slotX + 18 - q, 18, 112 - q, 18, q);
        }
        RenderSystem.disableBlend();
    }
}
