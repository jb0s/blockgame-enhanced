package dev.jb0s.blockgameenhanced.manager.adventure;

import lombok.Getter;

public class AdventureZone {
    @Getter
    private String id;

    @Getter
    private String world;

    @Getter
    private String musicType;

    @Getter
    private boolean adventure;

    @Getter
    private AdventureZoneBoss battle;

    @Getter
    private String music;

    @Getter
    private int[][] chunks;

    @Getter
    public int minY;

    @Getter
    public int maxY;
}
