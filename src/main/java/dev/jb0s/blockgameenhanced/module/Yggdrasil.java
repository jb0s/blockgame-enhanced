package dev.jb0s.blockgameenhanced.module;

import dev.jb0s.blockgameenhanced.manager.music.PositionedMusicSoundInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Yggdrasil {

    /**
     * Callback that handles spawning custom Portals in Yggdrasil when the player switches to Yggdrasil.
     * @param world The world that we've just changed to. This is not necessarily Yggdrasil.
     */
    public static void handleWorldChanged(World world) {
        if(world == null /*|| !world.getRegistryKey().getValue().getPath().equals("overworld")*/) {
            return;
        }

        //var soundId = new Identifier("blockgame", "snd.zone.yggdrasil.animus_portal");
        //var event = new SoundEvent(soundId);
        //var soundInstance = new PositionedMusicSoundInstance(event, SoundCategory.BLOCKS, 1, 1, new BlockPos(-20, 62, 0));

        //MinecraftClient.getInstance().getSoundManager().play(soundInstance);
    }
}
