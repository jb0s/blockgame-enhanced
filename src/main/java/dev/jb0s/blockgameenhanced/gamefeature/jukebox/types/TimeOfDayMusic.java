package dev.jb0s.blockgameenhanced.gamefeature.jukebox.types;

import dev.jb0s.blockgameenhanced.gamefeature.jukebox.json.JsonMusic;
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
        // todo bruh
        /*DayPhaseManager dayPhaseManager = BlockgameEnhancedClient.getDayPhaseManager();
        DayPhase dayPhase = dayPhaseManager.getCurrentDayPhase();

        // Avoid a crash here
        if(dayPhase == DayPhase.NONE)
            return null;

        // Return id at index of current day phase.
        // 0: Morning
        // 1: Noon
        // 2: Evening
        // 3: Night
        return identifiers.get(dayPhase.getId());*/
        return identifiers.get(0);
    }

    public static TimeOfDayMusic fromJSON(JsonMusic json) {
        TimeOfDayMusic randomMusic = new TimeOfDayMusic(json.getId(), json.getType(), new ArrayList<>());
        for (String id : json.getSoundIds()) {
            randomMusic.getIdentifiers().add(new Identifier(id));
        }
        return randomMusic;
    }
}
