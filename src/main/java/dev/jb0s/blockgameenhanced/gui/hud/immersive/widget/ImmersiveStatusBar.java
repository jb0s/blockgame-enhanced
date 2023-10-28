package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.manager.adventure.AdventureZone;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ImmersiveStatusBar extends ImmersiveWidget {
    public ImmersiveStatusBar(InGameHud inGameHud) {
        super(inGameHud);
    }

    @Override
    public int getWidth() {
        return getInGameHud().scaledWidth;
    }

    @Override
    public int getHeight() {
        return 22;
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, float tickDelta) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // Dark background
        DrawableHelper.fill(matrices, x, y, x + getWidth(), y + getHeight(), 0xCF000000);

        // Draw texts
        x += 20;
        y = getInGameHud().scaledHeight - ((getHeight() / 2) + (textRenderer.fontHeight / 2));
        textRenderer.draw(matrices, Text.of("Balance: §65,470.55$"), x, y, 0xFFFFFFFF);

        x += 135;
        AdventureZone zone = BlockgameEnhancedClient.getAdventureZoneManager().getCurrentZone();
        if(zone != null) {
            textRenderer.draw(matrices, "§7▪ " + new TranslatableText(String.format("region.blockgame.%s.%s", zone.getWorld(), zone.getId().toLowerCase())).getString(), x, y, 0xFFFFFFFF);
        }
        else {
            textRenderer.draw(matrices, Text.of("§7▪ §fWilderness"), x, y, 0xFFFFFFFF);
        }
    }
}
