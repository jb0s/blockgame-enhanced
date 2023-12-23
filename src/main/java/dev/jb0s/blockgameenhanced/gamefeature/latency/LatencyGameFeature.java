package dev.jb0s.blockgameenhanced.gamefeature.latency;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.helper.NetworkHelper;
import lombok.Getter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;

public class LatencyGameFeature extends GameFeature {

    /**
     * Allowable margin of error (both directions) for ping-event calculation.
     */
    private static final int LATENCY_MARGIN_OF_ERROR = 15;

    @Getter
    private int heartbeatLatency;

    @Getter
    private boolean isClientCaughtUp;

    private int tick;

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);

        ClientPlayConnectionEvents.JOIN.register((x, y, z) -> reset());
        ClientPlayConnectionEvents.DISCONNECT.register((x, y) -> reset());
    }

    @Override
    public void tick() {
        ++tick;
        heartbeatLatency = NetworkHelper.getNetworkLatency(getMinecraftClient().player);

        // Eventually we want to stop using the pre-login ping, so once the client is caught up we can stop using that.
        // Additionally, if 3500 ticks have passed, and we're still not caught up, the pre-login ping might have been a fluke. Discard it.
        if((tick > 3500 && (BlockgameEnhancedClient.getPreLoginLatency() - heartbeatLatency > LATENCY_MARGIN_OF_ERROR)) && !isClientCaughtUp) {
            isClientCaughtUp = true;
            //catchUpReason = "Pre-login appears to be a fluke";
        }
        else if(BlockgameEnhancedClient.getPreLoginLatency() - heartbeatLatency < LATENCY_MARGIN_OF_ERROR) {
            isClientCaughtUp = true;
            //catchUpReason = "Client is reasonable";
        }

        // Update latency value
        BlockgameEnhancedClient.setLatency(getLatency());
    }

    /**
     * Calculates a reliable latency value.
     * @return Pre-login or Heartbeat latency depending on which is more accurate at the time.
     */
    public int getLatency() {
        int hb = heartbeatLatency;
        int pl = BlockgameEnhancedClient.getPreLoginLatency();
        int dif = BlockgameEnhancedClient.getPreLoginLatency() - heartbeatLatency;
        return (dif > LATENCY_MARGIN_OF_ERROR && !isClientCaughtUp) ? pl : hb;
    }

    private void reset() {
        tick = 0;
        isClientCaughtUp = false;
        heartbeatLatency = 0;
    }
}
