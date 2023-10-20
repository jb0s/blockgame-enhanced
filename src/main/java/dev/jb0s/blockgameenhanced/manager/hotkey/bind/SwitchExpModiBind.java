package dev.jb0s.blockgameenhanced.manager.hotkey.bind;

import dev.jb0s.blockgameenhanced.helper.ExpHudDataHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public class SwitchExpModiBind {
    public static ActionResult handlePressed(MinecraftClient minecraftClient) {

        //Switch Exp Hud Modi
        ExpHudDataHelper.showGlobal = !ExpHudDataHelper.showGlobal;

        return ActionResult.SUCCESS;
    }
}
