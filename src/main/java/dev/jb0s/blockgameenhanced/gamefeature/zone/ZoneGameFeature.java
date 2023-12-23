package dev.jb0s.blockgameenhanced.gamefeature.zone;

import com.google.gson.Gson;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.adventurezone.EnteredWildernessEvent;
import dev.jb0s.blockgameenhanced.event.adventurezone.PlayerEnteredZoneEvent;
import dev.jb0s.blockgameenhanced.event.adventurezone.PlayerExitedZoneEvent;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ZoneGameFeature extends GameFeature {
    private static final String DATA_RESOURCE_PATH = "assets/blockgame/data/config/adventure_zones.json";

    private int lastCheckedChunkX;
    private int lastCheckedChunkZ;
    private Zone lastCheckedChunkResult;

    @Getter
    private Zone currentZone;

    @Getter
    private ZoneList zones;

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);
        loadZoneData();
    }

    @Override
    public void tick() {
        MinecraftClient client = getMinecraftClient();

        if(client.player == null) {
            return;
        }

        ClientPlayerEntity player = client.player;
        ChunkPos playerChunkPos = player.getChunkPos();
        Zone foundZone = findZoneInChunk(player.getWorld(), playerChunkPos, player.getY());

        if(currentZone != foundZone) {
            if(currentZone != null) {
                handlePlayerExitedZone(player, currentZone);
            }

            currentZone = foundZone;
            if(currentZone != null) {
                handlePlayerEnteredZone(player, currentZone);
            }
            else {
                EnteredWildernessEvent.EVENT.invoker().enteredWilderness(client, player);
            }
        }
    }

    /**
     * Loads zone data from adventure_zones.json into memory.
     */
    private void loadZoneData() {
        BlockgameEnhanced.LOGGER.info("Loading zone data");

        try {
            InputStream inputStream = ZoneGameFeature.class.getClassLoader().getResourceAsStream(DATA_RESOURCE_PATH);
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            zones = new Gson().fromJson(json, ZoneList.class);
        }
        catch (Exception e) {
            zones = new ZoneList();
            MinecraftClient.getInstance().getToastManager().add(new SystemToast(
                    SystemToast.Type.PACK_LOAD_FAILURE,
                    Text.of("Mod Error"),
                    Text.of("Zone data could not be loaded."))
            );

            BlockgameEnhanced.LOGGER.error("Failed to load zone data: {}", e.getMessage());
        }
    }

    /**
     * Compiles a list of all zones for the provided dimension.
     * @param world The dimension to fetch all zones for.
     * @return An ArrayList of all zones, empty if none were found.
     */
    public List<Zone> getZonesByWorld(World world) {
        List<Zone> out = new ArrayList<>();
        for (Zone zone : getZones().getZones()) {
            if(zone.getWorld().equals(world.getRegistryKey().getValue().getPath())) {
                out.add(zone);
            }
        }

        return out;
    }

    /**
     * Checks if the chunk is in a zone.
     * The last return value of this operation is cached, spam calling is not too inefficient.
     * @param world The dimension that the chunk is in.
     * @param pos The ChunkPos of the chunk.
     * @param y Y-Position of the player. Used for overlapping zones.
     * @return The zone that was found for this chunk, null if none were found.
     */
    public Zone findZoneInChunk(World world, ChunkPos pos, double y) {
        if(pos.x == lastCheckedChunkX && pos.z == lastCheckedChunkZ)
        {
            if(lastCheckedChunkResult != null && y >= lastCheckedChunkResult.getMinY() && y < lastCheckedChunkResult.getMaxY()) {
                return lastCheckedChunkResult;
            }
        }

        lastCheckedChunkX = pos.x;
        lastCheckedChunkZ = pos.z;

        List<Zone> zonesForThisWorld = getZonesByWorld(world);
        for (Zone zone : zonesForThisWorld) {
            boolean isInHeightRange = y >= zone.getMinY() && y < zone.getMaxY();
            if(!isInHeightRange) {
                continue;
            }

            for (int[] chunk : zone.getChunks()) {
                if(chunk == null)
                    continue;

                if(pos.x == chunk[0] && pos.z == chunk[1]) {
                    lastCheckedChunkResult = zone;
                    return zone;
                }
            }
        }

        lastCheckedChunkResult = null;
        return null;
    }

    /**
     * Handles a player entering a new zone.
     * @param playerEntity The player entity that has entered the zone.
     * @param zone The zone that the player has entered.
     */
    private void handlePlayerEnteredZone(ClientPlayerEntity playerEntity, Zone zone) {
        applyGameModeForZone(zone);

        // Invoke events
        PlayerEnteredZoneEvent.EVENT.invoker().enteredZone(getMinecraftClient(), playerEntity, zone);
    }

    /**
     * Handles a player exiting a zone.
     * @param playerEntity The player entity that has left the zone.
     */
    private void handlePlayerExitedZone(ClientPlayerEntity playerEntity, Zone zone) {
        applyGameModeForZone(null);

        // Invoke events
        PlayerExitedZoneEvent.EVENT.invoker().exitedZone(getMinecraftClient(), playerEntity, zone);
    }

    /**
     * Applies the zone's desired GameMode to the player.
     * @param zone The zone to read the GameMode from.
     */
    private void applyGameModeForZone(Zone zone) {
        ClientPlayerInteractionManager interactionManager = getMinecraftClient().interactionManager;
        GameMode currentGameMode = interactionManager.getCurrentGameMode();

        // Deliberately don't mess with the game mode if we're in creative or spectator.
        // You're welcome Thor :)
        if(currentGameMode != GameMode.CREATIVE && currentGameMode != GameMode.SPECTATOR) {
            if(zone != null) {
                GameMode desiredGameMode = zone.isAdventure() ? GameMode.ADVENTURE : GameMode.SURVIVAL;
                if(currentGameMode != desiredGameMode) {
                    interactionManager.setGameMode(desiredGameMode);
                }

                return;
            }

            // If we've left all zones then just switch to survival.
            if(currentGameMode != GameMode.SURVIVAL) {
                interactionManager.setGameMode(GameMode.SURVIVAL);
            }
        }
    }
}
