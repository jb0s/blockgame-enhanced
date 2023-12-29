package dev.jb0s.blockgameenhanced.gamefeature.jukebox;

import com.google.gson.Gson;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.adventurezone.EnteredWildernessEvent;
import dev.jb0s.blockgameenhanced.event.adventurezone.PlayerEnteredZoneEvent;
import dev.jb0s.blockgameenhanced.event.bossbattle.BossBattleCommencedEvent;
import dev.jb0s.blockgameenhanced.event.bossbattle.BossBattleEndedEvent;
import dev.jb0s.blockgameenhanced.event.dayphase.DayPhaseChangedEvent;
import dev.jb0s.blockgameenhanced.event.entity.player.PlayerRespawnedEvent;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.dayphase.DayPhase;
import dev.jb0s.blockgameenhanced.gamefeature.jukebox.json.JsonMusic;
import dev.jb0s.blockgameenhanced.gamefeature.jukebox.json.JsonMusicList;
import dev.jb0s.blockgameenhanced.gamefeature.jukebox.types.BattleMusic;
import dev.jb0s.blockgameenhanced.gamefeature.jukebox.types.Music;
import dev.jb0s.blockgameenhanced.gamefeature.jukebox.types.RandomMusic;
import dev.jb0s.blockgameenhanced.gamefeature.jukebox.types.TimeOfDayMusic;
import dev.jb0s.blockgameenhanced.gamefeature.zone.Zone;
import dev.jb0s.blockgameenhanced.gamefeature.zone.ZoneBoss;
import dev.jb0s.blockgameenhanced.gamefeature.zoneboss.ZoneBossBattleState;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JukeboxGameFeature extends GameFeature {
    private static final String DATA_RESOURCE_PATH = "assets/blockgame/data/config/music.json";

    private SoundManager soundManager;
    private MusicSoundInstance soundInstance;

    // State trackers
    private Zone currentZone;
    private ZoneBoss currentBoss;
    private ZoneBossBattleState currentBossBattleState;
    private DayPhase currentDayPhase = DayPhase.NONE;

    @Getter
    private JsonMusicList musicList;

    @Getter
    private boolean muted;

    @Getter
    private boolean playing;

    @Getter
    private boolean fading;

    @Getter
    private boolean refreshing;

    @Getter
    private Music currentMusic;

    @Getter
    private Music desiredMusic;

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);
        loadData();

        // Play zone music when player enters zone.
        PlayerEnteredZoneEvent.EVENT.register((client, playerEntity, zone) -> {
            currentZone = zone;
            if(currentZone == null || currentZone.getBattle() == null) {
                currentBoss = null;
                currentBossBattleState = ZoneBossBattleState.NO_BATTLE;
            }

            if(zone.getMusic() != null) {
                playMusic(zone.getMusic(), false, 0);
            }
            else {
                stopMusic(true);
            }
        });

        // Stop any zone music when player enters wilderness.
        EnteredWildernessEvent.EVENT.register((client, playerEntity) -> {
            currentZone = null;
            currentBoss = null;
            currentBossBattleState = ZoneBossBattleState.NO_BATTLE;
            stopMusic(true);
        });

        // Play boss music when a battle has begun.
        BossBattleCommencedEvent.EVENT.register(((boss) -> {
            currentBoss = boss;
            currentBossBattleState = ZoneBossBattleState.IN_PROGRESS;
            playMusic(boss.getMusic(), false, 0);
        }));

        // Refresh the music when the player defeats a boss so that the victory music plays.
        BossBattleEndedEvent.EVENT.register(((boss, state) -> {
            currentBoss = null;
            currentBossBattleState = state;

            if(currentMusic instanceof BattleMusic) {
                refresh();
            }
        }));

        // Refresh the music when the current day phase changes, and we're playing Time Of Day based music.
        DayPhaseChangedEvent.EVENT.register((dayPhase -> {
            currentDayPhase = dayPhase;

            if(currentMusic != null && currentMusic.getType().equals("TimeOfDay")) {
                refresh();
            }
        }));

        // Restart the music when dying and respawning
        PlayerRespawnedEvent.EVENT.register((client -> {
            Music music = getDesiredMusic();
            if(music != null) {
                playMusic(music.getId(), true, 0);
            }
        }));
    }

    @Override
    public void tick() {
        if(soundManager == null) {
            soundManager = getMinecraftClient().getSoundManager();
            return;
        }

        if(soundInstance == null) {
            playing = false;
            fading = false;
            return;
        }

        if(isPlaying() && getMinecraftClient().world == null) {
            stopMusic(false);
            return;
        }

        // Hack around a stupid feature that stops all playing sounds permanently if the volume is set to 0 at one point
        float masterVolume = getMinecraftClient().options.getSoundVolume(SoundCategory.MASTER);
        float musicVolume = getMinecraftClient().options.getSoundVolume(SoundCategory.MUSIC);
        boolean shouldBeMuted = masterVolume < MathHelper.EPSILON || musicVolume < MathHelper.EPSILON;
        boolean shouldBeUnmuted = masterVolume > MathHelper.EPSILON && musicVolume > MathHelper.EPSILON;

        if(shouldBeMuted && isPlaying()) {
            muted = true;
        }
        else if (shouldBeUnmuted && muted) {
            muted = false;

            if(isPlaying()) {
                soundInstance = new MusicSoundInstance(SoundEvent.of(currentMusic.getSoundId(getMusicSoundIndex())), SoundCategory.MUSIC, 1f, 1f, getMinecraftClient().player);
                soundManager.play(soundInstance);
            }
        }

        if(isFading() && soundInstance.getSound() != null) {
            if(desiredMusic != null && desiredMusic.getId().equals(currentMusic.getId()) && !isRefreshing())  {
                fading = false;
                soundInstance.setVolume(1f);
                return;
            }

            float vol = soundInstance.getVolume();
            float sub = vol - 0.025f;
            soundInstance.setVolume(MathHelper.clamp(sub, 0f, 1f));

            if(sub <= 0.01f) {
                stopMusic(false);

                if(isRefreshing()) {
                    refreshing = false;
                }

                if(desiredMusic != null) {
                    playMusic(desiredMusic.getId(), true, 0);
                }
            }
        }
    }

    private void loadData() {
        try {
            InputStream inputStream = JukeboxGameFeature.class.getClassLoader().getResourceAsStream(DATA_RESOURCE_PATH);
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            musicList = new Gson().fromJson(json, JsonMusicList.class);
        }
        catch (Exception e) {
            musicList = new JsonMusicList();
            MinecraftClient.getInstance().getToastManager().add(new SystemToast(
                    SystemToast.Type.PACK_LOAD_FAILURE,
                    Text.of("Mod Error"),
                    Text.of("Music list could not be loaded."))
            );
        }
    }

    /**
     * Play a music track.
     * @param music The ID of the music track to play.
     * @param bypassFade Whether to bypass the fade out.
     */
    public void playMusic(String music, boolean bypassFade, int delay) {
        Music mus = getMusicById(music);
        if(mus == null) {
            return;
        }

        if(bypassFade || !playing) {
            if(bypassFade && playing) {
                stopMusic(false);
            }

            ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
            if(playerEntity == null) {
                return;
            }

            currentMusic = mus;
            desiredMusic = mus;

            BlockgameEnhanced.LOGGER.info("Now playing: " + music + " (" + getMusicSoundIndex() + ")");
            soundInstance = new MusicSoundInstance(SoundEvent.of(mus.getSoundId(getMusicSoundIndex())), SoundCategory.MUSIC, 1f, 1f, playerEntity);
            soundManager.play(soundInstance, delay);
            playing = true;
            return;
        }

        stopMusic(!bypassFade);
        desiredMusic = mus;
    }

    /**
     * Stop the current music track.
     * @param fadeOut Whether to fade out the music.
     */
    public void stopMusic(boolean fadeOut) {
        if(soundInstance == null) {
            return;
        }

        if(!fadeOut) {
            if(desiredMusic == currentMusic) {
                desiredMusic = null;
            }

            soundManager.stop(soundInstance);
            soundInstance = null;
            currentMusic = null;
            playing = false;
            fading = false;
        }
        else {
            fading = true;
            desiredMusic = null;
        }
    }

    /**
     * Re-fetches what track should be playing and starts playing it anew.
     */
    public void refresh() {
        if(currentMusic == null) {
            return;
        }

        refreshing = true;
        playMusic(currentMusic.getId(), false, 0);
    }

    /**
     * Gets a Music instance by its music id.
     * @param id The id of the music.
     * @return A music instance object. Use .getSoundId() on it to get a Minecraft sound id.
     */
    private Music getMusicById(String id) {
        for (JsonMusic json : musicList.getMusic()) {
            if(!json.getId().equals(id)) {
                continue;
            }

            switch (json.getType()) {
                case "Default" -> {
                    return Music.fromJSON(json);
                }
                case "Random" -> {
                    return RandomMusic.fromJSON(json);
                }
                case "TimeOfDay" -> {
                    return TimeOfDayMusic.fromJSON(json);
                }
                case "Battle" -> {
                    return BattleMusic.fromJSON(json);
                }
            }
        }

        return null;
    }

    /**
     * Gets the sound index for a music track.
     */
    private int getMusicSoundIndex() {
        if(currentBossBattleState != ZoneBossBattleState.NO_BATTLE) {
            return (currentBoss == null | currentBossBattleState == ZoneBossBattleState.BATTLE_ENDED) ? 1 : 0;
        }

        return currentDayPhase.getId();
    }
}
