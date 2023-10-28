package dev.jb0s.blockgameenhanced.gui.hud.immersive;

import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.ImmersiveHealthText;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.ImmersiveHotbar;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.ImmersiveStatusBar;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.NewImmersiveHotbar;
import net.minecraft.client.gui.hud.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class ImmersiveIngameHud extends InGameHud {
    protected ImmersiveStatusBar statusBar;
    protected ImmersiveHotbar hotbar;
    protected ImmersiveHealthText healthText;
    protected NewImmersiveHotbar newImmersiveHotbar;

    public ImmersiveIngameHud(MinecraftClient client) {
        super(client);
        statusBar = new ImmersiveStatusBar(this);
        hotbar = new ImmersiveHotbar(this);
        healthText = new ImmersiveHealthText(this);
        newImmersiveHotbar = new NewImmersiveHotbar(this);
    }

    @Override
    protected void renderHotbar(float tickDelta, MatrixStack matrices) {
        int bottom = scaledHeight - 22;

        statusBar.render(matrices, 0, bottom, tickDelta);
        //hotbar.render(matrices, scaledWidth / 2, bottom + 1, tickDelta);
        newImmersiveHotbar.render(matrices, scaledWidth / 2, bottom + 1, tickDelta);
    }

    @Override
    protected void renderStatusBars(MatrixStack matrices) {
    }

    @Override
    public void renderExperienceBar(MatrixStack matrices, int x) {
    }

    @Override
    public void renderMountJumpBar(MatrixStack matrices, int x) {
    }

    @Override
    public void renderMountHealth(MatrixStack matrices) {
    }
}
