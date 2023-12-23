package dev.jb0s.blockgameenhanced.config.structure;

public enum DiscordRPCPrivacy {
    HIDE_COMPLETELY(0),
    PLAYING_ONLY(1),
    PLAYING_AND_INGAME(2),
    PLAYING_INGAME_AND_PARTY(3);

    private final int value;
    private DiscordRPCPrivacy(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
