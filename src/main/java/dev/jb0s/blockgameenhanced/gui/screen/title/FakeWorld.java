package dev.jb0s.blockgameenhanced.gui.screen.title;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.OptionalLong;

public class FakeWorld extends ClientWorld {
    public FakeWorld() {
        super(new FakeClientPlayNetHandler(),
                new Properties(Difficulty.EASY, false, true),
                World.OVERWORLD,
                new RegistryEntry.Direct<>(new DimensionType(OptionalLong.empty(), true, false, false, true, 1.0, false, false, 16, 16, 16,  BlockTags.INFINIBURN_OVERWORLD, DimensionTypes.OVERWORLD_ID, 17, null)),
                Integer.MAX_VALUE,
                Integer.MAX_VALUE, () -> null,
                MinecraftClient.getInstance().worldRenderer,
                false,
                0L);
    }
}