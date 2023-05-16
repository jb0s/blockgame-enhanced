package dev.jb0s.blockgameenhanced.manager.dayphase;

import lombok.Getter;
import lombok.Setter;

public enum DayPhase {
    NONE(-1),
    MORNING(0),
    NOON(1),
    EVENING(2),
    NIGHT(3),
    RAINING(4);

    @Getter
    @Setter
    private int id;

    DayPhase(int id) {
        setId(id);
    }
}
