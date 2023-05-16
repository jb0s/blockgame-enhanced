package dev.jb0s.blockgameenhanced.manager.music;

import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class MusicSoundInstance extends EntityTrackingSoundInstance {
    public MusicSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, Entity entity) {
        super(sound, category, volume, pitch, entity);
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
