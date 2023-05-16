package dev.jb0s.blockgameenhanced.manager.hotkey.bind;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public class BackpackBind {
    public static ActionResult handlePressed(MinecraftClient client) {
        if(client.player == null) {
            return ActionResult.FAIL;
        }

        client.player.sendChatMessage("/backpack");
        return ActionResult.SUCCESS;
    }
}
