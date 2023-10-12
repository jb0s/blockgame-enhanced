package dev.jb0s.blockgameenhanced.manager.music.types;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.manager.adventure.AdventureZoneBoss;
import dev.jb0s.blockgameenhanced.manager.bossbattle.BattleState;
import dev.jb0s.blockgameenhanced.manager.bossbattle.BossBattleManager;
import dev.jb0s.blockgameenhanced.manager.music.json.JsonMusic;
import lombok.Getter;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class BattleMusic extends Music {
    @Getter
    private final ArrayList<Identifier> identifiers;

    public BattleMusic(String soundId, String type, ArrayList<Identifier> identifiers) {
        super(soundId, type, null);
        this.identifiers = identifiers;
    }

    @Override
    public Identifier getSoundId() {
        BossBattleManager bossBattleManager = BlockgameEnhancedClient.getBossBattleManager();
        AdventureZoneBoss bossBattle = bossBattleManager.getCurrentBattle();
        BattleState battleState = bossBattleManager.getCurrentBattleState();

        // Avoid a crash here
        //if(bossBattle == null)
        //    return null;

        // Return victory music if current battle was won
        if(bossBattle == null || battleState == BattleState.BATTLE_ENDED)
            return identifiers.get(1);

        return identifiers.get(0);
    }

    public static BattleMusic fromJSON(JsonMusic json) {
        BattleMusic randomMusic = new BattleMusic(json.getId(), json.getType(), new ArrayList<>());
        for (String id : json.getSoundIds()) {
            randomMusic.getIdentifiers().add(new Identifier(id));
        }
        return randomMusic;
    }
}
