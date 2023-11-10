package dev.jb0s.blockgameenhanced.manager.latency;

import dev.jb0s.blockgameenhanced.helper.NetworkHelper;
import dev.jb0s.blockgameenhanced.manager.Manager;
import dev.jb0s.blockgameenhanced.manager.latency.event.ItemUsageEvent;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LatencyManager extends Manager {
    /**
     * Allowable margin of error (both directions) to match an event from the timeline.
     */
    private static final int ERROR_MARGIN = 15;

    @Getter
    private int heartbeatLatency;

    @Getter
    @Setter
    private int preLoginLatency;

    @Getter
    private boolean isClientCaughtUp;

    private int tick;
    private String catchUpReason;
    private final ArrayList<ItemUsageEvent> capturedItemUsages = new ArrayList<>();

    @Override
    public void init() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            tick = 0;
            isClientCaughtUp = false;
            catchUpReason = "Not caught up yet";
            heartbeatLatency = 0;
        });
    }

    @Override
    public void tick(MinecraftClient client) {
        ++tick;

        PlayerEntity player = client.player;
        if(player == null) {
            return;
        }

        heartbeatLatency = NetworkHelper.getNetworkLatency(player);

        // Eventually we want to stop using the pre-login ping, so once the client is caught up we can stop using that.
        // Additionally, if 3500 ticks have passed, and we're still not caught up, the pre-login ping might have been a fluke. Discard it.
        if((tick > 3500 && (preLoginLatency - heartbeatLatency > ERROR_MARGIN)) && !isClientCaughtUp) {
            isClientCaughtUp = true;
            catchUpReason = "Pre-login appears to be a fluke";
        }
        else if(preLoginLatency - heartbeatLatency < ERROR_MARGIN) {
            isClientCaughtUp = true;
            catchUpReason = "Client is reasonable";
        }
    }

    public void captureItemUsage(ItemStack itemStack) {
        ItemUsageEvent event = new ItemUsageEvent(itemStack);
        capturedItemUsages.add(event);
    }

    public ItemUsageEvent getItemUsage() {
        int latency = getLatency();
        return getItemUsage(latency);
    }

    public ItemUsageEvent getItemUsage(int latency) {
        long targetLatency = System.currentTimeMillis() - latency;

        ItemUsageEvent winning = null;
        long winningLatency = Long.MAX_VALUE;

        for (ItemUsageEvent e : capturedItemUsages) {
            long dif = Math.abs(e.getTimeMs() - targetLatency);

            if(dif < winningLatency) {
                winningLatency = dif;
                winning = e;
            }
        }

        return winning;
    }

    /**
     * Calculates a reliable latency value.
     * @return Pre-login or Heartbeat latency depending on which is more accurate at the time.
     */
    public int getLatency() {
        int hb = heartbeatLatency;
        int pl = preLoginLatency;
        int dif = preLoginLatency - heartbeatLatency;
        return (dif > ERROR_MARGIN && !isClientCaughtUp) ? pl : hb;
    }
}
