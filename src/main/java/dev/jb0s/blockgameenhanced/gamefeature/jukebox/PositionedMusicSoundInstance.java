package dev.jb0s.blockgameenhanced.gamefeature.jukebox;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class PositionedMusicSoundInstance extends PositionedSoundInstance {
    public PositionedMusicSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, BlockPos blockPos) {
        super(sound, category, volume, pitch, blockPos);
        this.repeat = true;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public int getRepeatDelay() {
        return 0;
    }
}
