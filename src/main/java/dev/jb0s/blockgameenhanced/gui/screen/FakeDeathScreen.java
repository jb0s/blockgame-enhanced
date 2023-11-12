package dev.jb0s.blockgameenhanced.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.manager.music.MusicManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class FakeDeathScreen extends Screen {
    private static final int RED = ColorHelper.Argb.getArgb(125, 255, 0, 0);
    private static final int BLACK = ColorHelper.Argb.getArgb(255, 0, 0, 0);
    private static final int FADEOUT_THRESHOLD = 20;
    private static final int RESPAWN_THRESHOLD = 50;
    private static final SoundEvent DEATH_SOUND = new SoundEvent(new Identifier("blockgame", "mus.gui.combat.death"));

    // Managers
    private static MusicManager musicManager;

    // Stats
    private boolean isWaitingForPlayer;
    private int ticks;

    public FakeDeathScreen() {
        super(Text.of("F dude"));
    }

    @Override
    public void init() {
        super.init();

        musicManager = BlockgameEnhancedClient.getMusicManager();

        PlayerEntity playerEntity = client.player;
        SoundManager soundManager = client.getSoundManager();
        BlockPos playerPos = playerEntity.getBlockPos();

        // Play death sound
        soundManager.stopAll();
        soundManager.play(new PositionedSoundInstance(DEATH_SOUND, SoundCategory.MASTER, 1f, 1f, playerPos));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        float alpha = MathHelper.clamp(((float) ticks / FADEOUT_THRESHOLD), 0f, 1f);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f - alpha);
        fill(matrices, 0, 0, width, height, RED);
        RenderSystem.setShaderColor(.0f, .0f, .0f, alpha);
        fill(matrices, 0, 0, width, height, BLACK);
        RenderSystem.disableBlend();

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        super.tick();

        // Automatically respawn after a little bit
        ticks++;
        if(ticks >= RESPAWN_THRESHOLD) {
            close();
            if(client.player != null) {
                client.player.sendMessage(Text.of("§r(ﾉ>ω<)ﾉ §e:｡･::･ﾟ’★,｡･::･ﾟ’☆ Get trolled, nerd"), false);
            }
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
