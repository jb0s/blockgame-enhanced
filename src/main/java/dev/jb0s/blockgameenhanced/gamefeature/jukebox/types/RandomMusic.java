package dev.jb0s.blockgameenhanced.gamefeature.jukebox.types;

import dev.jb0s.blockgameenhanced.gamefeature.jukebox.json.JsonMusic;
import lombok.Getter;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Random;

public class RandomMusic extends Music {
    private final Random random;

    @Getter
    private final ArrayList<Identifier> soundIds;

    public RandomMusic(String soundId, String type, ArrayList<Identifier> soundIds) {
        super(soundId, type, null);
        this.random = new Random();
        this.soundIds = soundIds;
    }

    @Override
    public Identifier getSoundId(int id) {
        return soundIds.get(random.nextInt(soundIds.size()));
    }

    public static RandomMusic fromJSON(JsonMusic json) {
        RandomMusic randomMusic = new RandomMusic(json.getId(), json.getType(), new ArrayList<>());
        for (String id : json.getSoundIds()) {
            randomMusic.getSoundIds().add(new Identifier(id));
        }
        return randomMusic;
    }
}
