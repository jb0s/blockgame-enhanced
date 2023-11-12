package dev.jb0s.blockgameenhanced.gui.hud.immersive;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.effect.ImmersiveDrownVignette;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.experience.ImmersiveExpPopupContainer;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.hotbar.ImmersiveStatusBar;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.hotbar.ImmersiveDiabloHotbar;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.pickups.ImmersivePickupStream;
import lombok.Getter;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class ImmersiveIngameHud extends InGameHud {
    protected ImmersiveStatusBar statusBar;
    protected ImmersiveDiabloHotbar newImmersiveHotbar;
    protected ImmersiveDrownVignette immersiveDrownVignette;

    @Getter
    protected ImmersiveExpPopupContainer immersiveExpPopupContainer;

    @Getter
    protected ImmersivePickupStream immersivePickupStream;

    public ImmersiveIngameHud(MinecraftClient client) {
        super(client);
        //statusBar = new ImmersiveStatusBar(this);
        newImmersiveHotbar = new ImmersiveDiabloHotbar(this);
        immersiveExpPopupContainer = new ImmersiveExpPopupContainer(this);
        immersiveDrownVignette = new ImmersiveDrownVignette(this);
        immersivePickupStream = new ImmersivePickupStream(this);
    }

    @Override
    protected void renderHotbar(float tickDelta, MatrixStack matrices) {
        int bottom = scaledHeight/* - 22*/;

        immersiveDrownVignette.render(matrices, 0, 0, tickDelta);
        newImmersiveHotbar.render(matrices, scaledWidth / 2, bottom, tickDelta);

        boolean shouldRaiseExpBar = overlayMessage != null && overlayRemaining > 0;
        if(shouldRaiseExpBar) {
            immersiveExpPopupContainer.render(matrices, scaledWidth / 2, bottom - 105, tickDelta);
        }
        else {
            immersiveExpPopupContainer.render(matrices, scaledWidth / 2, bottom - 80, tickDelta);
        }

        immersivePickupStream.render(matrices, scaledWidth - 15, (scaledHeight / 2) - (immersivePickupStream.getHeight() / 2), tickDelta);
    }


    @Override
    public void tick(boolean paused) {
        super.tick(paused);

        // Only call it on elements that actually do shit
        immersiveExpPopupContainer.tick();
        immersivePickupStream.tick();
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
