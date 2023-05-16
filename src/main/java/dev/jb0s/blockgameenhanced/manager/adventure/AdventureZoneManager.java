package dev.jb0s.blockgameenhanced.manager.adventure;

import com.google.gson.Gson;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.adventurezone.EnteredWildernessEvent;
import dev.jb0s.blockgameenhanced.event.adventurezone.EnteredZoneEvent;
import dev.jb0s.blockgameenhanced.event.adventurezone.ExitedZoneEvent;
import dev.jb0s.blockgameenhanced.manager.Manager;
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

public class AdventureZoneManager extends Manager {
    private static final String DATA_RESOURCE_PATH = "assets/blockgame/data/config/adventure_zones.json";

    private MinecraftClient client;
    private int lastCheckedChunkX;
    private int lastCheckedChunkZ;
    private AdventureZone lastCheckedChunkResult;

    @Getter
    private AdventureZone currentZone;

    @Getter
    private AdventureZoneList adventureZones;

    @Override
    public void init() {
        client = MinecraftClient.getInstance();

        try {
            InputStream inputStream = AdventureZoneManager.class.getClassLoader().getResourceAsStream(DATA_RESOURCE_PATH);
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            adventureZones = new Gson().fromJson(json, AdventureZoneList.class);
        }
        catch (Exception e) {
            adventureZones = new AdventureZoneList();
            MinecraftClient.getInstance().getToastManager().add(new SystemToast(
                    SystemToast.Type.PACK_LOAD_FAILURE,
                    Text.of("Mod Error"),
                    Text.of("AdventureZones could not be loaded."))
            );
        }
    }

    @Override
    public void tick(MinecraftClient client) {
        if(client.player == null) {
            return;
        }

        ClientPlayerEntity player = client.player;
        ChunkPos playerChunkPos = player.getChunkPos();
        AdventureZone foundAdventureZone = findAdventureZoneForChunk(player.getWorld(), playerChunkPos);

        if(currentZone != foundAdventureZone) {
            if(currentZone != null) {
                handlePlayerExitAdventureZone(player, currentZone);
            }

            currentZone = foundAdventureZone;
            if(currentZone != null) {
                handlePlayerEnterAdventureZone(player, currentZone);
            }
            else {
                EnteredWildernessEvent.EVENT.invoker().enteredWilderness(client, player);
            }
        }
    }

    @Override
    public List<String> getDebugStats() {
        ArrayList<String> lines = new ArrayList<>();
        World world = MinecraftClient.getInstance().world;
        lines.add("World: " + world.getRegistryKey().getValue().getPath());
        lines.add("Current Zone: " + (currentZone != null ? currentZone.getId() : "null"));
        lines.add("Zone Music Type: " + (currentZone != null ? currentZone.getMusicType() : "null"));
        lines.add("Zone Music: " + (currentZone != null ? currentZone.getMusic() : "null"));
        lines.add("Data loaded: " + (adventureZones.getAdventureZones().length > 0));
        return lines;
    }

    /**
     * Compiles a list of all Adventure Zones for the provided dimension.
     * @param world The dimension to fetch all zones for.
     * @return An ArrayList of all Adventure Zones, empty if none were found.
     */
    public List<AdventureZone> getAdventureZonesByWorld(World world) {
        List<AdventureZone> out = new ArrayList<>();
        for (AdventureZone zone : getAdventureZones().getAdventureZones()) {
            if(zone.getWorld().equals(world.getRegistryKey().getValue().getPath())) {
                out.add(zone);
            }
        }

        return out;
    }

    /**
     * Checks if the chunk is in an Adventure Zone.
     * The last return value of this operation is cached, spam calling is not too inefficient.
     * @param world The dimension that the chunk is in.
     * @param pos The ChunkPos of the chunk.
     * @return The AdventureZone that was found for this chunk, null if none were found.
     */
    public AdventureZone findAdventureZoneForChunk(World world, ChunkPos pos) {
        if(pos.x == lastCheckedChunkX && pos.z == lastCheckedChunkZ)
            return lastCheckedChunkResult;

        lastCheckedChunkX = pos.x;
        lastCheckedChunkZ = pos.z;

        List<AdventureZone> zonesForThisWorld = getAdventureZonesByWorld(world);
        for (AdventureZone zone : zonesForThisWorld) {
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
     * Handles a player entering a new Adventure Zone.
     * @param playerEntity The player entity that has entered the zone.
     * @param zone The zone that the player has entered.
     */
    private void handlePlayerEnterAdventureZone(ClientPlayerEntity playerEntity, AdventureZone zone) {
        applyGameModeForZone(zone);

        // Invoke events
        EnteredZoneEvent.EVENT.invoker().enteredZone(client, playerEntity, zone);

        // Apply battle
        if(zone.getBattle() != null) {
            BlockgameEnhancedClient.getBossBattleManager().setCurrentBattle(zone.getBattle());
        }
    }

    /**
     * Handles a player exiting an adventure zone.
     * @param playerEntity The player entity that has left the zone.
     */
    private void handlePlayerExitAdventureZone(ClientPlayerEntity playerEntity, AdventureZone zone) {
        applyGameModeForZone(null);

        // Invoke events
        ExitedZoneEvent.EVENT.invoker().exitedZone(client, playerEntity, zone);
    }

    /**
     * Applies the zone's desired GameMode to the player.
     * @param zone The zone to read the GameMode from.
     */
    private void applyGameModeForZone(AdventureZone zone) {
        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
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
