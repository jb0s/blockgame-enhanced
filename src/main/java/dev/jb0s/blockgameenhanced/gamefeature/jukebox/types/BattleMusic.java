package dev.jb0s.blockgameenhanced.gamefeature.jukebox.types;

import dev.jb0s.blockgameenhanced.gamefeature.jukebox.json.JsonMusic;
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
        // todo reimplement
        /*BossBattleManager bossBattleManager = BlockgameEnhancedClient.getBossBattleManager();
        ZoneBoss bossBattle = bossBattleManager.getCurrentBattle();
        ZoneBossBattleState battleState = bossBattleManager.getCurrentBattleState();

        // Avoid a crash here
        //if(bossBattle == null)
        //    return null;

        // Return victory music if current battle was won
        if(bossBattle == null || battleState == ZoneBossBattleState.BATTLE_ENDED)
            return identifiers.get(1);*/

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
