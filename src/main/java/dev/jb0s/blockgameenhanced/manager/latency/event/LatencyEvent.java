package dev.jb0s.blockgameenhanced.manager.latency.event;

import lombok.Getter;

public class LatencyEvent {
    /**
     * Epoch in milliseconds at the time of capture.
     */
    @Getter
    private final long timeMs;

    public LatencyEvent() {
        timeMs = System.currentTimeMillis();
    }
}
