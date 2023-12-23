package dev.jb0s.blockgameenhanced.gamefeature.hotkey.bind;

import dev.jb0s.blockgameenhanced.event.gamefeature.hotkey.PingHotkeyPressedEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public class PingBind {
    public static ActionResult handlePressed(MinecraftClient client) {
        if(client.player == null) {
            return ActionResult.FAIL;
        }

        PingHotkeyPressedEvent.EVENT.invoker().pingHotkeyPressed(client);
        return ActionResult.SUCCESS;
    }
}
