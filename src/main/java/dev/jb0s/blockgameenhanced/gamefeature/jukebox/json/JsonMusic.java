package dev.jb0s.blockgameenhanced.gamefeature.jukebox.json;

import lombok.Getter;

public class JsonMusic {
    @Getter
    private String id;

    @Getter
    private String type;

    @Getter
    private String[] soundIds;
}
