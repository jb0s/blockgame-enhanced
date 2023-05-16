package dev.jb0s.blockgameenhanced.manager.dayphase;

import dev.jb0s.blockgameenhanced.event.dayphase.DayPhaseChangedEvent;
import dev.jb0s.blockgameenhanced.manager.Manager;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;

import java.util.*;

public class DayPhaseManager extends Manager {
    private static final HashMap<Integer, DayPhase> PHASE_THRESHOLDS = new HashMap<>();

    private MinecraftClient client;

    @Getter
    private DayPhase currentDayPhase = DayPhase.NONE;

    @Override
    public void init() {
        client = MinecraftClient.getInstance();

        // Populate phase threshold list
        PHASE_THRESHOLDS.put(0, DayPhase.MORNING);
        PHASE_THRESHOLDS.put(2000, DayPhase.NOON);
        PHASE_THRESHOLDS.put(11500, DayPhase.EVENING);
        PHASE_THRESHOLDS.put(13000, DayPhase.NIGHT);
        PHASE_THRESHOLDS.put(23600, DayPhase.MORNING); // next day
    }

    @Override
    public void tick(MinecraftClient client) {
        if(client.world == null) {
            return;
        }

        DayPhase calculatedDayPhase = getRecalculatedDayPhase();
        if(getCurrentDayPhase() != calculatedDayPhase) {
            currentDayPhase = calculatedDayPhase;

            // Invoke events
            DayPhaseChangedEvent.EVENT.invoker().dayPhaseChanged(currentDayPhase);
        }
    }

    @Override
    public List<String> getDebugStats() {
        ArrayList<String> lines = new ArrayList<>();
        if(client.world == null)
            return lines;

        lines.add("World Time: " + client.world.getTimeOfDay() % 24000);
        lines.add("Day Phase: " + currentDayPhase);
        return lines;
    }

    /**
     * Recalculates the current day phase and returns it.
     */
    private DayPhase getRecalculatedDayPhase() {
        if(client.world == null)
            return DayPhase.NONE;

        // Return raining DayPhase if it's raining
        if(client.world.isRaining())
            return DayPhase.RAINING;

        long timeOfDay = client.world.getTimeOfDay() % 24000;
        if(timeOfDay >= 23600)
            return DayPhase.MORNING;
        if(timeOfDay >= 13000)
            return DayPhase.NIGHT;
        if(timeOfDay >= 11500)
            return DayPhase.EVENING;
        if(timeOfDay >= 2000)
            return DayPhase.NOON;

        return DayPhase.MORNING;
    }
}
