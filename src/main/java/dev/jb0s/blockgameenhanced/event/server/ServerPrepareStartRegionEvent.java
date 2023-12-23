package dev.jb0s.blockgameenhanced.event.server;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.util.ActionResult;

public interface ServerPrepareStartRegionEvent {
    Event<ServerPrepareStartRegionEvent> EVENT = EventFactory.createArrayBacked(ServerPrepareStartRegionEvent.class, (listeners) -> (server, progressListener) -> {
        for (ServerPrepareStartRegionEvent listener : listeners) {
            ActionResult x = listener.prepareStartRegion(server, progressListener);
            if(x != ActionResult.PASS) return x;
        }
        return ActionResult.PASS;
    });

    ActionResult prepareStartRegion(MinecraftServer server, WorldGenerationProgressListener progressListener);
}
