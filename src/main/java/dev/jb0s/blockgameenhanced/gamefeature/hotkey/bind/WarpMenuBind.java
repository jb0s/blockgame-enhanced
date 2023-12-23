package dev.jb0s.blockgameenhanced.gamefeature.hotkey.bind;

import dev.jb0s.blockgameenhanced.gui.screen.WarpScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public class WarpMenuBind {
    public static ActionResult handlePressed(MinecraftClient client) {
        if(client.player == null) {
            return ActionResult.FAIL;
        }

        client.setScreen(new WarpScreen());
        return ActionResult.SUCCESS;
    }
}
