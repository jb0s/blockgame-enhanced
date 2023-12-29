package dev.jb0s.blockgameenhanced.gamefeature.hotkey.bind;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public class BackpackBind {
    public static ActionResult handlePressed(MinecraftClient client) {
        if(client.getNetworkHandler() == null) {
            return ActionResult.FAIL;
        }

        client.getNetworkHandler().sendChatCommand("backpack");
        return ActionResult.SUCCESS;
    }
}
