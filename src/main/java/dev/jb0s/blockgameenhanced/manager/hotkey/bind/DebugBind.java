package dev.jb0s.blockgameenhanced.manager.hotkey.bind;

import dev.jb0s.blockgameenhanced.gui.hud.DebugHud;
import dev.jb0s.blockgameenhanced.gui.screen.WarpScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public class DebugBind {
    public static ActionResult handlePressed(MinecraftClient client) {
        if(client.player == null) {
            return ActionResult.FAIL;
        }

        boolean debugHudVisible = DebugHud.isVisible();
        DebugHud.setVisible(!debugHudVisible);
        return ActionResult.SUCCESS;
    }
}
