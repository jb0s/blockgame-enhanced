package dev.jb0s.blockgameenhanced;

import dev.jb0s.blockgameenhanced.manager.config.modules.ModConfig;
import lombok.Getter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockgameEnhanced implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("blockgameenhanced");

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
    }
}
