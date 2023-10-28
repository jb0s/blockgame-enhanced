package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ImmersiveHealthText extends ImmersiveWidget {
    @Getter
    @Setter
    private float value;

    @Getter
    @Setter
    private float maxValue;

    private int width;
    private int height;

    public ImmersiveHealthText(InGameHud inGameHud) {
        super(inGameHud);
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, float tickDelta) {
        drawText(matrices, Text.of("test"), x, y, 0xFFFFFF);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
