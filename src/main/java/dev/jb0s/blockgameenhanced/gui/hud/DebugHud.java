package dev.jb0s.blockgameenhanced.gui.hud;

import com.google.common.base.Strings;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.manager.Manager;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DebugHud extends DrawableHelper {
    @Getter
    @Setter
    private static boolean visible;

    private static String goof;

    public static void render(MatrixStack matrices, TextRenderer textRenderer) {

        // Initialize random goofy quote
        if(goof == null || goof.isEmpty()) {
            try {
                String str = Files.readString(Paths.get(DebugHud.class.getClassLoader().getResource("assets/blockgame/texts/goofs.txt").toURI()));
                String[] arr = str.split(System.lineSeparator());

                int i = new Random().nextInt(arr.length);
                if(i == 21) i++; // yarrCult

                goof = arr[i];
            }
            catch (Exception e) {
                goof = "MISSINGNO.";
            }
        }

        // Only render the debug hud if visible
        if(!isVisible())
            return;

        List<Manager> allManagers = BlockgameEnhancedClient.getAllManagers();
        List<String> lines = new ArrayList<>();
        lines.add("Blockgame Enhanced v" + FabricLoader.getInstance().getModContainer("blockgameenhanced").get().getMetadata().getVersion().getFriendlyString());
        lines.add(allManagers.size() + " active manager(s)");
        lines.add("");

        for (Manager manager : allManagers) {
            List<String> managerLines = manager.getDebugStats();
            if(managerLines == null)
                continue;

            lines.add(manager.getClass().getSimpleName());
            lines.addAll(manager.getDebugStats());
            lines.add("");
        }

        lines.add(String.format("(%s)", goof));

        for (int i = 0; i < lines.size(); i++) {
            if (Strings.isNullOrEmpty(lines.get(i))) continue;
            int j = textRenderer.fontHeight;
            int k = textRenderer.getWidth(lines.get(i));
            int m = 2 + j * i;

            net.minecraft.client.gui.hud.DebugHud.fill(matrices, 1, m - 1, 2 + k + 1, m + j - 1, -1873784752);
            textRenderer.draw(matrices, lines.get(i), 1.0f, (float)m, 0xE0E0E0);
        }
    }
}
