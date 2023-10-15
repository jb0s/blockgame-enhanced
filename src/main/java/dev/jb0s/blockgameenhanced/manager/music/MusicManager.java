package dev.jb0s.blockgameenhanced.manager.music;

import com.google.gson.Gson;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.event.adventurezone.EnteredWildernessEvent;
import dev.jb0s.blockgameenhanced.event.adventurezone.EnteredZoneEvent;
import dev.jb0s.blockgameenhanced.event.bossbattle.BossBattleCommencedEvent;
import dev.jb0s.blockgameenhanced.event.bossbattle.BossBattleEndedEvent;
import dev.jb0s.blockgameenhanced.event.dayphase.DayPhaseChangedEvent;
import dev.jb0s.blockgameenhanced.manager.Manager;
import dev.jb0s.blockgameenhanced.manager.music.json.JsonMusic;
import dev.jb0s.blockgameenhanced.manager.music.json.JsonMusicList;
import dev.jb0s.blockgameenhanced.manager.music.types.BattleMusic;
import dev.jb0s.blockgameenhanced.manager.music.types.Music;
import dev.jb0s.blockgameenhanced.manager.music.types.RandomMusic;
import dev.jb0s.blockgameenhanced.manager.music.types.TimeOfDayMusic;
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
import java.util.ArrayList;
import java.util.List;

public class MusicManager extends Manager {
    private static final String DATA_RESOURCE_PATH = "assets/blockgame/data/config/music.json";

    private SoundManager soundManager;
    private MusicSoundInstance soundInstance;
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
    public void init() {
        try {
            InputStream inputStream = MusicManager.class.getClassLoader().getResourceAsStream(DATA_RESOURCE_PATH);
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

        // Play zone music when player enters zone.
        EnteredZoneEvent.EVENT.register((client, playerEntity, zone) -> {
            if(zone.getMusic() != null) {
                playMusic(zone.getMusic(), false, 0);
            }
            else {
                stopMusic(true);
            }
        });

        // Stop any zone music when player enters wilderness.
        EnteredWildernessEvent.EVENT.register((client, playerEntity) -> {
            stopMusic(true);
        });

        // Play boss music when a battle has begun.
        BossBattleCommencedEvent.EVENT.register(((boss) -> {
            playMusic(boss.getMusic(), false, 0);
        }));

        // Refresh the music when the player defeats a boss so that the victory music plays.
        BossBattleEndedEvent.EVENT.register(((boss) -> {
            if(currentMusic instanceof BattleMusic) {
                refresh();
            }
        }));

        // Refresh the music when the current day phase changes, and we're playing Time Of Day based music.
        DayPhaseChangedEvent.EVENT.register((dayPhase -> {
            if(currentMusic != null && currentMusic.getType().equals("TimeOfDay")) {
                refresh();
            }
        }));
    }

    @Override
    public void tick(MinecraftClient client) {
        if(soundManager == null) {
            soundManager = client.getSoundManager();
            return;
        }

        if(soundInstance == null) {
            playing = false;
            fading = false;
            return;
        }

        if(isPlaying() && client.world == null) {
            stopMusic(false);
            return;
        }

        // Hack around a stupid feature that stops all playing sounds permanently if the volume is set to 0 at one point
        float masterVolume = client.options.getSoundVolume(SoundCategory.MASTER);
        float musicVolume = client.options.getSoundVolume(SoundCategory.MUSIC);
        boolean shouldBeMuted = masterVolume < MathHelper.EPSILON || musicVolume < MathHelper.EPSILON;
        boolean shouldBeUnmuted = masterVolume > MathHelper.EPSILON && musicVolume > MathHelper.EPSILON;

        if(shouldBeMuted && isPlaying()) {
            muted = true;
        }
        else if (shouldBeUnmuted && muted) {
            muted = false;

            if(isPlaying()) {
                soundInstance = new MusicSoundInstance(new SoundEvent(currentMusic.getSoundId()), SoundCategory.MUSIC, 1f, 1f, client.player);
                soundManager.play(soundInstance);
            }
        }

        if(isFading()) {
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

    @Override
    public List<String> getDebugStats() {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("Is Playing: " + isPlaying());
        lines.add("Is Fading: " + isFading());
        lines.add("Is Muted: " + isMuted());
        lines.add("Is Refreshing: " + isRefreshing());
        lines.add("Current Music: " + (currentMusic != null ? currentMusic.getId() : "null"));
        lines.add("Desired Music: " + (desiredMusic != null ? desiredMusic.getId() : "null"));
        lines.add("Now Playing: " + ((soundInstance != null && soundInstance.getSound() != null) ? soundInstance.getSound().getIdentifier() : "null"));
        return lines;
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

            soundInstance = new MusicSoundInstance(new SoundEvent(mus.getSoundId()), SoundCategory.MUSIC, 1f, 1f, playerEntity);
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
}
