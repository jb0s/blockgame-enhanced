package dev.jb0s.blockgameenhanced.gamefeature.dayphase;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.dayphase.DayPhaseChangedEvent;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;

public class DayPhaseGameFeature extends GameFeature {
    private static final HashMap<Integer, DayPhase> PHASE_THRESHOLDS = new HashMap<>();

    @Getter
    private DayPhase currentDayPhase = DayPhase.NONE;

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);

        // Populate phase threshold list
        PHASE_THRESHOLDS.put(0, DayPhase.MORNING);
        PHASE_THRESHOLDS.put(2000, DayPhase.NOON);
        PHASE_THRESHOLDS.put(11500, DayPhase.EVENING);
        PHASE_THRESHOLDS.put(13000, DayPhase.NIGHT);
        PHASE_THRESHOLDS.put(23600, DayPhase.MORNING); // next day
    }

    @Override
    public void tick() {
        if(getMinecraftClient().world == null) {
            return;
        }

        DayPhase calculatedDayPhase = getRecalculatedDayPhase();
        if(getCurrentDayPhase() != calculatedDayPhase) {
            currentDayPhase = calculatedDayPhase;

            // Invoke events
            DayPhaseChangedEvent.EVENT.invoker().dayPhaseChanged(currentDayPhase);
        }
    }

    /**
     * Recalculates the current day phase and returns it.
     */
    private DayPhase getRecalculatedDayPhase() {
        if(getMinecraftClient().world == null)
            return DayPhase.NONE;

        // Return raining DayPhase if it's raining
        if(getMinecraftClient().world.isRaining())
            return DayPhase.RAINING;

        long timeOfDay = getMinecraftClient().world.getTimeOfDay() % 24000;
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
