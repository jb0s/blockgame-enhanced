package dev.jb0s.blockgameenhanced.mixin.network;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.chat.ReceiveChatMessageEvent;
import dev.jb0s.blockgameenhanced.manager.party.PartyManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
    public void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        ClientPlayNetworkHandler thisHandler = (ClientPlayNetworkHandler) (Object) this;
        NetworkThreadUtils.forceMainThread(packet, thisHandler, client);

        PartyManager pm = BlockgameEnhancedClient.getPartyManager();
        boolean pmAcceptedThisPacket = pm.handleInventoryUpdate(packet);
        if(pmAcceptedThisPacket) {
            ci.cancel();
        }
    }

    @Inject(method = "onOpenScreen", at = @At("HEAD"))
    public void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        ClientPlayNetworkHandler thisHandler = (ClientPlayNetworkHandler) (Object) this;
        NetworkThreadUtils.forceMainThread(packet, thisHandler, client);

        PartyManager pm = BlockgameEnhancedClient.getPartyManager();
        boolean pmAcceptedThisPacket = pm.handleScreenOpen(packet);
        if(pmAcceptedThisPacket) {
            // Send a packet to the server saying we have closed the window, although we never opened it
            CloseHandledScreenC2SPacket pak = new CloseHandledScreenC2SPacket(packet.getSyncId());
            thisHandler.sendPacket(pak);

            // We're good now, cancel the rest of the packet handling
            ci.cancel();
        }
    }

    @Inject(method = "onGameMessage", at = @At("RETURN"))
    public void onOpenScreen(GameMessageS2CPacket packet, CallbackInfo ci) {
        ReceiveChatMessageEvent.EVENT.invoker().receiveChatMessage(client, packet.getMessage().getString());
    }
}
