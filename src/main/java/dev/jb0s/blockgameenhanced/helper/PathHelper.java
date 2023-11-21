package dev.jb0s.blockgameenhanced.helper;

import lombok.SneakyThrows;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class PathHelper {
    private static final String BLOCKGAME_FOLDER_NAME = "blockgame";
    private static final Path GAME_FOLDER_PATH = Path.of(FabricLoader.getInstance().getConfigDir() + "/" + BLOCKGAME_FOLDER_NAME + "/");

    @SneakyThrows
    public static Path getBlockgamePath() {
        // Create the game folder directory in case it does not exist
        if(Files.notExists(GAME_FOLDER_PATH)) {
            Files.createDirectory(GAME_FOLDER_PATH);
        }

        return Path.of(GAME_FOLDER_PATH + "/");
    }
}
