package dev.jb0s.blockgameenhanced.manager.music.types;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.manager.dayphase.DayPhase;
import dev.jb0s.blockgameenhanced.manager.dayphase.DayPhaseManager;
import dev.jb0s.blockgameenhanced.manager.music.json.JsonMusic;
import lombok.Getter;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class TimeOfDayMusic extends Music {
    @Getter
    private final ArrayList<Identifier> identifiers;

    public TimeOfDayMusic(String soundId, String type, ArrayList<Identifier> identifiers) {
        super(soundId, type, null);
        this.identifiers = identifiers;
    }

    @Override
    public Identifier getSoundId() {
        DayPhaseManager dayPhaseManager = BlockgameEnhancedClient.getDayPhaseManager();
        DayPhase dayPhase = dayPhaseManager.getCurrentDayPhase();

        // Avoid a crash here
        if(dayPhase == DayPhase.NONE)
            return null;

        // Return id at index of current day phase.
        // 0: Morning
        // 1: Noon
        // 2: Evening
        // 3: Night
        return identifiers.get(dayPhase.getId());
    }

    public static TimeOfDayMusic fromJSON(JsonMusic json) {
        TimeOfDayMusic randomMusic = new TimeOfDayMusic(json.getId(), json.getType(), new ArrayList<>());
        for (String id : json.getSoundIds()) {
            randomMusic.getIdentifiers().add(new Identifier(id));
        }
        return randomMusic;
    }
}
