package dev.jb0s.blockgameenhanced.helper;

import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.client.MinecraftClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathHelper {
    private static final String MINECRAFT_FOLDER_NAME = ".minecraft";
    private static final String BLOCKGAME_FOLDER_NAME = ".blockgame";
    private static final Path GAME_FOLDER_PATH = Paths.get(getAppdataPath(), BLOCKGAME_FOLDER_NAME);

    @Getter
    private static final Path minecraftFolderPath = Paths.get(getAppdataPath(), MINECRAFT_FOLDER_NAME);

    @SneakyThrows
    public static Path getBlockgamePath() {
        // Create the game folder directory in case it does not exist
        if(Files.notExists(GAME_FOLDER_PATH)) {
            Files.createDirectory(GAME_FOLDER_PATH);
        }

        return GAME_FOLDER_PATH;
    }

    public static String getAppdataPath() {
        if(MinecraftClient.IS_SYSTEM_MAC) {
            return System.getProperty("user.home") + "/Library/Application Support";
        }

        return System.getenv("APPDATA");
    }
}
