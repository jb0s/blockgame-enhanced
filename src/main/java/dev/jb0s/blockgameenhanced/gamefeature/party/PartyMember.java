package dev.jb0s.blockgameenhanced.gamefeature.party;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.network.PlayerListEntry;

public class PartyMember {
    @Getter
    private PlayerListEntry player;

    @Getter
    private String playerName;

    @Getter
    @Setter
    private int health;

    @Getter
    @Setter
    private int maxHealth;

    @Getter
    @Setter
    private long lastUpdateSecond;

    @Getter
    @Setter
    private boolean isAlive;

    public PartyMember(PlayerListEntry ple) {
        player = ple;
        playerName = player.getProfile().getName();
        isAlive = true;
        health = 20;
        maxHealth = 20;
    }
}
