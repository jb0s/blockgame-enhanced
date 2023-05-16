package dev.jb0s.blockgameenhanced.eggs.thor;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class ThorScreen extends Screen {
    private static final Identifier BG = new Identifier("textures/gui/options_background.png");
    private static final Identifier THOR = new Identifier("blockgame", "textures/gui/title/thor.png");

    private final Screen parent;
    private final MinecraftClient client;
    private float previousMusicVolume;
    private SoundInstance caveNoiseInstance;

    private float timer = 0.0f;

    public ThorScreen(Screen parent) {
        super(Text.of("He sees all"));
        this.parent = parent;
        client = MinecraftClient.getInstance();
    }

    @Override
    public void init() {
        // Stop any music or shit like that
        previousMusicVolume = client.options.getSoundVolume(SoundCategory.MUSIC);
        client.options.setSoundVolume(SoundCategory.MUSIC, .0f);

        // Play cave noise
        caveNoiseInstance = new PositionedSoundInstance(SoundEvents.AMBIENT_CAVE, SoundCategory.MASTER, 1, 1, new BlockPos(0, 0, 0));
        client.getSoundManager().play(caveNoiseInstance);
    }

    @Override
    public void tick() {
        timer += 1;

        // Automatically exit the screen after a moment
        if(timer > 50) {
            client.getSoundManager().stop(caveNoiseInstance);
            client.options.setSoundVolume(SoundCategory.MUSIC, previousMusicVolume);
            client.setScreen(this.parent);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // Draw dark backdrop
        RenderSystem.setShaderColor(.0f, .0f, .0f, .0f);
        RenderSystem.setShaderTexture(0, BG);
        this.drawTexture(matrices, 0, 0, 0, 0, width, height);

        // Draw Thor
        int thorWidth = 579 / 5;
        int thorHeight = 635 / 5;
        float alpha = MathHelper.clamp(timer, 0, 20 * 3.5f) / (20 * 3.5f);
        RenderSystem.setShaderColor(alpha, alpha, alpha, alpha);
        RenderSystem.setShaderTexture(0, THOR);
        DrawableHelper.drawTexture(matrices, (width / 2) - (thorWidth / 2), (height / 2) - (thorHeight / 2), 0, 0, thorWidth, thorHeight, thorWidth, thorHeight);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
