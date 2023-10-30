package dev.jb0s.blockgameenhanced.manager.hotkey.bind;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.manager.party.PartyManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public class PingBind {
    public static ActionResult handlePressed(MinecraftClient client) {
        if(client.player == null) {
            return ActionResult.FAIL;
        }

        PartyManager partyManager = BlockgameEnhancedClient.getPartyManager();
        partyManager.tryPing();
        return ActionResult.SUCCESS;
    }
}
