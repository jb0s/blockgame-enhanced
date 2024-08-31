package dev.jb0s.blockgameenhanced.gui.hud;

import com.google.common.base.Strings;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;

import dev.jb0s.blockgameenhanced.helper.DebugHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DebugHud {
    public static void render(DrawContext drawContext, TextRenderer textRenderer) {
        if(!BlockgameEnhanced.DEBUG) return;

        List<String> lines = getDebugLines();
        for (int i = 0; i < lines.size(); i++) {
            if (Strings.isNullOrEmpty(lines.get(i))) continue;
            int j = textRenderer.fontHeight;
            int m = 2 + j * i;

            drawContext.drawText(textRenderer, lines.get(i), 1, m, 0xE0E0E0, true);
        }
    }

    private static @NotNull List<String> getDebugLines()
    {
        List<GameFeature> gameFeatures = BlockgameEnhancedClient.getLoadedGameFeatures();
        List<String> lines = new ArrayList<>();
        lines.add("Blockgame Enhanced DEBUG MODE");
        lines.add(gameFeatures.size() + " game features loaded");
        lines.add("");

        for(GameFeature gf : gameFeatures) {
            List<String> gfDebug = gf.getDebugInfo();
            if(gfDebug != null && !gfDebug.isEmpty()) {
                lines.add(gf.getClass().getSimpleName());
                lines.addAll(gfDebug);
                lines.add("");
            }
        }
        return lines;
    }
}
