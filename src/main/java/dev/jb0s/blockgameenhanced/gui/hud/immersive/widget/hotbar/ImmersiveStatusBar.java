package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.hotbar;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.ImmersiveWidget;
import dev.jb0s.blockgameenhanced.manager.adventure.AdventureZone;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;

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
    public void render(DrawContext context, int x, int y, float tickDelta) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // Dark background
        context.fill(x, y, x + getWidth(), y + getHeight(), 0xCF000000);

        // Draw texts
        x += 20;
        y = getInGameHud().scaledHeight - ((getHeight() / 2) + (textRenderer.fontHeight / 2));
        context.drawText(textRenderer, Text.of("Balance: §65,470.55$"), x, y, 0xFFFFFFFF, false);

        x += 135;
        AdventureZone zone = BlockgameEnhancedClient.getAdventureZoneManager().getCurrentZone();
        if(zone != null) {
            context.drawText(textRenderer, "§7▪ " + Text.translatable(String.format("region.blockgame.%s.%s", zone.getWorld(), zone.getId().toLowerCase())).getString(), x, y, 0xFFFFFFFF, false);
        }
        else {
            context.drawText(textRenderer, Text.of("§7▪ §fWilderness"), x, y, 0xFFFFFFFF, false);
        }
    }
}
