package dev.jb0s.blockgameenhanced.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;

public class WarpScreen extends Screen {
    private final Text SUBHEADER = Text.translatable("menu.blockgame.warp.subheader");
    private final Text BUTTON_TOWN = Text.translatable("menu.blockgame.warp.button.town");
    private final Text BUTTON_YGGDRASIL = Text.translatable("menu.blockgame.warp.button.yggdrasil");
    private final Text BUTTON_ORIGIN = Text.translatable("menu.blockgame.warp.button.origin");
    private final Text BUTTON_CANCEL = Text.translatable("menu.blockgame.warp.button.cancel");
    private final Text BUTTON_KROGNAR = Text.translatable("menu.blockgame.warp.button.krognar");
    private final Text BUTTON_SUNKEN = Text.translatable("menu.blockgame.warp.button.sunken");
    private final Text BUTTON_MYRKHEIM = Text.translatable("menu.blockgame.warp.button.myrkheim");
    private final Text BUTTON_ROTTENMAW = Text.translatable("menu.blockgame.warp.button.rotten_maw");
    private final Text BUTTON_NEITH = Text.translatable("menu.blockgame.warp.button.neith");
    private final Text BUTTON_ARENA = Text.translatable("menu.blockgame.warp.button.arena");

    private final LinkedHashMap<Text, String> WARP_OPTIONS = new LinkedHashMap<>() {{
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
        super(Text.translatable("menu.blockgame.warp.header"));
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
        for (Map.Entry<Text, String> set : WARP_OPTIONS.entrySet()) {
            Text btnText = set.getKey();

            int xOrigin = width / 2 - 100;
            int columnIndex = i / columnSizeY;
            int xPos = xOrigin + (buttonSpacingX * columnIndex);
            int yPos = listStartingY + (buttonSpacingY * (i % columnSizeY));

            addDrawableChild(ButtonWidget.builder(btnText, (button) -> {
                close();
                client.mouse.lockCursor();

                ClientPlayNetworkHandler nw = client.getNetworkHandler();
                if(nw != null) {
                    nw.sendChatMessage(set.getValue());
                }
            }).position(xPos - globalXOffset, yPos).size(buttonWidth, buttonHeight).build());

            i++;
        }

        // Cancel Button
        int y = height / 10;
        addDrawableChild(ButtonWidget.builder(BUTTON_CANCEL, (button) -> {
            close();
            client.mouse.lockCursor();
        }).position(width / 2 - 100, height - y - 20).size(200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int y = height / 10;
        drawTextCentered(context, title, y, 0xFAFAFA);
        drawTextCentered(context, SUBHEADER, y + 11, 0x5E5E5E);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawTextCentered(DrawContext context, Text text, int y, int color) {
        int textWidth = textRenderer.getWidth(text);
        int x = (width / 2) - (textWidth / 2);
        context.drawText(textRenderer, text, x, y, color, false);
    }
}
