package dev.jb0s.blockgameenhanced.gamefeature.zone;

import lombok.Getter;

public class Zone {
    @Getter
    private String id;

    @Getter
    private String world;

    @Getter
    private String musicType;

    @Getter
    private boolean adventure;

    @Getter
    private ZoneBoss battle;

    @Getter
    private String music;

    @Getter
    private int[][] chunks;

    @Getter
    public int minY;

    @Getter
    public int maxY;
}
