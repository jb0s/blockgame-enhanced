package dev.jb0s.blockgameenhanced.gamefeature.mmostats;

import lombok.Getter;
import lombok.Setter;

public enum MMOProfession {
    MINING("§7Mining", 1),
    LOGGING(" §6Logging", 2),
    ARCHAEOLOGY("§eArchaeology", 3),
    FISHING("§9Fishing", 4),
    HERBALISM("§2Herbalism", 5),
    RUNECARVING("§5Runecarving", 6),
    EINHERJAR("§cEinherjar", 7),
    COOKING("§cCooking", 7),
    HUNTING("§cHunting", 7),
    ALCHEMY("§5Alchemy", 6);

    @Getter
    @Setter
    private String displayName;

    @Getter
    @Setter
    private int index;

    MMOProfession(String displayName, int index) {
        setDisplayName(displayName);
        setIndex(index);
    }
}
