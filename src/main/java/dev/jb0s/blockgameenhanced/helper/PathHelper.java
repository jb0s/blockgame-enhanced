package dev.jb0s.blockgameenhanced.helper;

import lombok.Getter;
import lombok.SneakyThrows;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathHelper {
    private static final String MINECRAFT_FOLDER_NAME = ".minecraft";
    private static final String BLOCKGAME_FOLDER_NAME = "blockgame";
    private static final Path GAME_FOLDER_PATH = Path.of(FabricLoader.getInstance().getConfigDir() + "/"+BLOCKGAME_FOLDER_NAME);

    //@Getter
    //private static final Path minecraftFolderPath = Path.of(FabricLoader.getInstance().getConfigDir()+"../");

    @SneakyThrows
    public static Path getBlockgamePath() {
        // Create the game folder directory in case it does not exist
        if(Files.notExists(GAME_FOLDER_PATH)) {
            Files.createDirectory(GAME_FOLDER_PATH);
        }

        return Path.of(GAME_FOLDER_PATH+"/");
    }
}
