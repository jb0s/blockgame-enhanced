package dev.jb0s.blockgameenhanced.gui.hud.immersive;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.experience.ImmersiveExpPopupContainer;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.hotbar.ImmersiveStatusBar;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.hotbar.ImmersiveDiabloHotbar;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.JumpingMount;

public class ImmersiveIngameHud extends InGameHud {
    protected ImmersiveStatusBar statusBar;
    protected ImmersiveDiabloHotbar newImmersiveHotbar;

    @Getter
    protected ImmersiveExpPopupContainer immersiveExpPopupContainer;

    public ImmersiveIngameHud(MinecraftClient client) {
        super(client, client.getItemRenderer());
        //statusBar = new ImmersiveStatusBar(this);
        newImmersiveHotbar = new ImmersiveDiabloHotbar(this);
        immersiveExpPopupContainer = new ImmersiveExpPopupContainer(this);
    }

    @Override
    protected void renderHotbar(float tickDelta, DrawContext context) {
        int bottom = scaledHeight/* - 22*/;

        //statusBar.render(matrices, 0, bottom, tickDelta);
        newImmersiveHotbar.render(context, scaledWidth / 2, bottom, tickDelta);

        boolean shouldRaiseExpBar = overlayMessage != null && overlayRemaining > 0;
        if(shouldRaiseExpBar) {
            immersiveExpPopupContainer.render(context, scaledWidth / 2, bottom - 105, tickDelta);
        }
        else {
            immersiveExpPopupContainer.render(context, scaledWidth / 2, bottom - 80, tickDelta);
        }
    }

    @Override
    public void tick(boolean paused) {
        super.tick(paused);

        // Only call it on elements that actually do shit
        immersiveExpPopupContainer.tick();
    }

    @Override
    protected void renderStatusBars(DrawContext context) {
    }

    @Override
    public void renderExperienceBar(DrawContext context, int x) {
    }

    @Override
    public void renderMountJumpBar(JumpingMount mount, DrawContext context, int x) {
    }

    @Override
    public void renderMountHealth(DrawContext context) {
    }
}
