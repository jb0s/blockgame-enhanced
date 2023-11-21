package dev.jb0s.blockgameenhanced;

import dev.jb0s.blockgameenhanced.helper.PathHelper;
import dev.jb0s.blockgameenhanced.helper.ResourceHelper;
import dev.jb0s.blockgameenhanced.config.modules.ModConfig;
import lombok.Getter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class BlockgameEnhanced implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("BlockgameEnhanced");
    public static final boolean DEBUG = System.getenv("bge-debug") != null;

    @Getter
    private static boolean modmenuPresent = false;

    @Getter
    private static boolean notkerMmoPresent = false;

    @Getter
    private static boolean optifinePresent = false;

    @Getter
    private static ModConfig config;

    @Override
    public void onInitialize() {
        // Cloth Config
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        // Detect if the ModMenu mod is present.
        if(FabricLoader.getInstance().isModLoaded("modmenu")) {
            modmenuPresent = true;
        }

        // Detect if Notker's McMMO Durability Viewer is present.
        if(FabricLoader.getInstance().isModLoaded("mcmmo_durability_viewer")) {
            notkerMmoPresent = true;
        }

        // Detect if OptiFine is present with OptiFabric.
        if(FabricLoader.getInstance().isModLoaded("optifabric")) {
            optifinePresent = true;
        }

        // Extract shit if OptiFine
        if(BlockgameEnhanced.isOptifinePresent()) {
            try {
                String destPath = MinecraftClient.getInstance().runDirectory.getAbsolutePath() + "/saves/Empty";

                // If we have already installed OptiFine Compat, we don't need to do so again
                if(Files.exists(Path.of(destPath))) {
                    BlockgameEnhanced.LOGGER.info("BlockgameOFCompat is already installed");
                    return;
                }

                String path = ResourceHelper.exportResource("/BlockgameOFCompat.zip");
                BlockgameEnhanced.LOGGER.info("Extracted BlockgameOFCompat to " + path);
                BlockgameEnhanced.LOGGER.info("Extracting BlockgameOFCompat to " + destPath);

                ResourceHelper.unzip(path, destPath);
                new File(path).delete();
            }
            catch (Exception e) {
                BlockgameEnhanced.LOGGER.error("Failed to extract BlockgameOFCompat: " + e.getMessage());
            }
        }
    }
}
