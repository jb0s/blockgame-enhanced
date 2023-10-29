package dev.jb0s.blockgameenhanced.manager.hotkey.bind;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.manager.party.PartyManager;
import dev.jb0s.blockgameenhanced.manager.party.PartyMember;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

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
