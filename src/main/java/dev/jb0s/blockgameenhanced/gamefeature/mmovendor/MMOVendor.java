package dev.jb0s.blockgameenhanced.gamefeature.mmovendor;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public enum MMOVendor {
    WARRIOR("Willy", "blacksmith"),
    BLACKSMITH("Markus", "blacksmith"),
    GUARDIAN("Armand", "blacksmith"),
    SPELLCRAFTER("Sally", "spellhouse"),
    ALCHEMIST("Nina", "spellhouse"),
    SILK_WEAVER("Wendy", "spellhouse"),
    LUMBERJACK("Larry", "woodshack"),
    MINER("Steve", "mining"),
    HUNTER("Hanzo", "einherjar"),
    LEATHERWORKER("Seymour", "leathershack"),
    BOWYER("Brent", "leathershack"),
    REPAIR("RepairBot 3000", "repairbot"),
    FISHERMAN("Franky", "fishing"),
    ARTIFICER("Eitri", "runecrafter"),
    RUNE_CARVER("Brokkr", "runecrafter"),
    THAUMATURGE("Gregory", "healhouse"),
    POTION_SELLER("Justin", "healhouse"),
    WOOL_WEAVER("Porfirio", "healhouse"),
    CHEF("Ken", "restaurant"),
    TEA_MASTER("Piggly Wiggly", "restaurant"),
    BOTANIST("Hesha", "botanist"),
    ARCHAEOLOGIST("Indy", "archaeology"),
    BARON_WARBUCKS("Baron Warbucks", "einherjar"),
    MAYOR_MCCHEESE("Mayor McCheese", "mayor");

    @Getter
    @Setter
    private String characterName;

    @Getter
    @Setter
    private String ui;

    MMOVendor(String name, String ui) {
        setCharacterName(name);
        setUi(ui);
    }

    /**
     * Determine a MMOVendor type by their character name.
     * @param name Name of the vendor's character.
     * @return Vendor matching provided name, null if not found.
     */
    public static MMOVendor getByName(String name) {
        for (MMOVendor x : MMOVendor.values()) {
            if(x.getCharacterName().equals(name)) {
                return x;
            }
        }

         return null;
    }

    /**
     * Find the PlayerEntity associated with this vendor type.
     * @return PlayerEntity that is a vendor of this type
     */
    public PlayerEntity getVendorEntity() {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        World world = minecraft.world; if(world == null) return null;

        String convertedName = toString().toLowerCase().replace("_", " ");

        for (PlayerEntity pe : world.getPlayers()) {
            if(pe.getName().getString().toLowerCase().equals(convertedName)) {
                return pe;
            }
        }

        return null;
    }
}
