package dev.jb0s.blockgameenhanced.manager.hotkey.bind;

import dev.jb0s.blockgameenhanced.helper.ExpHudDataHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public class ToggleExpHudBind {
    public static ActionResult handlePressed(MinecraftClient minecraftClient) {
        // Toggle Exp Hud Render
        ExpHudDataHelper.hideOverlay = !ExpHudDataHelper.hideOverlay;

        return ActionResult.SUCCESS;
    }
}
