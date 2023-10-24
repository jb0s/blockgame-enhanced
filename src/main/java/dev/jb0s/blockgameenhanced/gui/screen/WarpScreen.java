package dev.jb0s.blockgameenhanced.gui.screen;

import dev.jb0s.blockgameenhanced.event.adventurezone.EnteredWildernessEvent;
import dev.jb0s.blockgameenhanced.event.chat.CommandSuggestionsEvent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.LinkedHashMap;
import java.util.Map;

public class WarpScreen extends Screen {
    private final TranslatableText SUBHEADER = new TranslatableText("menu.blockgame.warp.subheader");
    private final TranslatableText BUTTON_TOWN = new TranslatableText("menu.blockgame.warp.button.town");
    private final TranslatableText BUTTON_YGGDRASIL = new TranslatableText("menu.blockgame.warp.button.yggdrasil");
    private final TranslatableText BUTTON_ORIGIN = new TranslatableText("menu.blockgame.warp.button.origin");
    private final TranslatableText BUTTON_CANCEL = new TranslatableText("menu.blockgame.warp.button.cancel");
    private final TranslatableText BUTTON_KROGNAR = new TranslatableText("menu.blockgame.warp.button.krognar");
    private final TranslatableText BUTTON_SUNKEN = new TranslatableText("menu.blockgame.warp.button.sunken");
    private final TranslatableText BUTTON_MYRKHEIM = new TranslatableText("menu.blockgame.warp.button.myrkheim");
    private final TranslatableText BUTTON_ROTTENMAW = new TranslatableText("menu.blockgame.warp.button.rotten_maw");
    private final TranslatableText BUTTON_NEITH = new TranslatableText("menu.blockgame.warp.button.neith");
    private final TranslatableText BUTTON_ARENA = new TranslatableText("menu.blockgame.warp.button.arena");

    private final LinkedHashMap<TranslatableText, String> WARP_OPTIONS = new LinkedHashMap<>() {{
        put(BUTTON_TOWN, "/t spawn");
        put(BUTTON_YGGDRASIL, "/warp Yggdrasil");
        put(BUTTON_ORIGIN, "/warp Origin");
        put(BUTTON_MYRKHEIM, "/warp Myrkheim");
        put(BUTTON_KROGNAR, "/warp Krognars_Bastion");
        put(BUTTON_SUNKEN, "/warp Sunken_Cells");
        put(BUTTON_ROTTENMAW, "/warp Rotten_Maw");
        put(BUTTON_NEITH, "/warp Neith");
        put(BUTTON_ARENA, "/warp Arena");
    }};

    public WarpScreen() {
        super(new TranslatableText("menu.blockgame.warp.header"));
    }

    @Override
    protected void init() {
        RequestCommandCompletionsC2SPacket pak = new RequestCommandCompletionsC2SPacket(this.hashCode(), "warp ");
        client.getNetworkHandler().sendPacket(pak);

        int buttonWidth = 200;
        int buttonHeight = 20;
        int columnSizeY = 5;

        int buttonSpacingX = buttonWidth + 6;
        int buttonSpacingY = buttonHeight + 4;

        int totalOptions = WARP_OPTIONS.size();
        int totalColumn = totalOptions / columnSizeY;
        int globalXOffset = (buttonSpacingX * totalColumn) / 2;
        int listStartingY = (height / 2) - ((buttonSpacingY * 5) / 2);

        // Add warp options
        int i = 0;
        for (Map.Entry<TranslatableText, String> set : WARP_OPTIONS.entrySet()) {
            Text btnText = set.getKey();

            int xOrigin = width / 2 - 100;
            int columnIndex = i / columnSizeY;
            int xPos = xOrigin + (buttonSpacingX * columnIndex);
            int yPos = listStartingY + (buttonSpacingY * (i % columnSizeY));

            addDrawableChild(new ButtonWidget(xPos - globalXOffset, yPos, buttonWidth, buttonHeight, btnText, (button) -> {
                close();
                client.mouse.lockCursor();

                ClientPlayerEntity p = client.player;
                if(p != null) {
                    p.sendChatMessage(set.getValue());
                }
            }));

            i++;
        }

        // Cancel Button
        int y = height / 10;
        addDrawableChild(new ButtonWidget(width / 2 - 100, height - y - 20, 200, 20, BUTTON_CANCEL, (button) -> {
            close();
            client.mouse.lockCursor();
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        int y = height / 10;
        drawTextCentered(matrices, title, y, 0xFAFAFA);
        drawTextCentered(matrices, SUBHEADER, y + 11, 0x5E5E5E);

        super.render(matrices, mouseX, mouseY, delta);
    }

    private void drawTextCentered(MatrixStack matrices, Text text, int y, int color) {
        int textWidth = textRenderer.getWidth(text);
        int x = (width / 2) - (textWidth / 2);
        textRenderer.draw(matrices, text, x, y, color);
    }
}
