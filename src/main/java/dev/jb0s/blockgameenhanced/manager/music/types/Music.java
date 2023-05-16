package dev.jb0s.blockgameenhanced.manager.music.types;

import dev.jb0s.blockgameenhanced.manager.music.json.JsonMusic;
import lombok.Getter;
import net.minecraft.util.Identifier;

public class Music {
    @Getter
    protected String id;

    @Getter
    protected String type;

    private Identifier soundId;

    public Music(String id, String type, Identifier soundId) {
        this.id = id;
        this.type = type;
        this.soundId = soundId;
    }

    public Identifier getSoundId() {
        return soundId;
    }

    public static Music fromJSON(JsonMusic json) {
        return new Music(json.getId(), json.getType(), new Identifier(json.getSoundIds()[0]));
    }
}
