package dev.jb0s.blockgameenhanced.manager.config.structure;

public enum MMOItemsItemTypes {
    ALL(""),
    RUNES("RUNECARVING");

    private final String tag;

    MMOItemsItemTypes(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return this.tag;
    }
}
