package dev.jb0s.blockgameenhanced.mixin.network;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.chat.CommandSuggestionsEvent;
import dev.jb0s.blockgameenhanced.event.chat.ReceiveChatMessageEvent;
import dev.jb0s.blockgameenhanced.event.chat.SendChatMessageEvent;
import dev.jb0s.blockgameenhanced.event.screen.ScreenOpenedEvent;
import dev.jb0s.blockgameenhanced.event.screen.ScreenReceivedInventoryEvent;
import dev.jb0s.blockgameenhanced.manager.party.PartyManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
    public void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        ClientPlayNetworkHandler thisHandler = (ClientPlayNetworkHandler) (Object) this;
        NetworkThreadUtils.forceMainThread(packet, thisHandler, MinecraftClient.getInstance());

        ScreenReceivedInventoryEvent.EVENT.invoker().screenReceivedInventory(packet);

        // todo: move this to an event
        PartyManager pm = BlockgameEnhancedClient.getPartyManager();
        boolean pmAcceptedThisPacket = pm.handleInventoryUpdate(packet);
        boolean shouldBeDiscardedForPm = pm.isWaitingForPartyScreenOpen() || (pm.isWaitingForPartyScreenContent() && pm.getCurrentPayloadSyncId() != packet.getSyncId());
        if(pmAcceptedThisPacket || shouldBeDiscardedForPm) {
            ci.cancel();
        }
    }

    @Inject(method = "onOpenScreen", at = @At("HEAD"), cancellable = true)
    public void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        ClientPlayNetworkHandler thisHandler = (ClientPlayNetworkHandler) (Object) this;
        NetworkThreadUtils.forceMainThread(packet, thisHandler, MinecraftClient.getInstance());

        ScreenOpenedEvent.EVENT.invoker().screenOpened(packet);

        // todo: move this to the new event
        PartyManager pm = BlockgameEnhancedClient.getPartyManager();
        boolean pmAcceptedThisPacket = pm.handleScreenOpen(packet);
        boolean shouldBeDiscardedForPm = (pm.isWaitingForPartyScreenOpen() || pm.isWaitingForPartyScreenContent()) && pm.getCurrentPayloadSyncId() != packet.getSyncId();
        if(pmAcceptedThisPacket || shouldBeDiscardedForPm) {
            // Send a packet to the server saying we have closed the window, although we never opened it
            CloseHandledScreenC2SPacket pak = new CloseHandledScreenC2SPacket(packet.getSyncId());
            thisHandler.sendPacket(pak);

            // We're good now, cancel the rest of the packet handling
            ci.cancel();
        }
    }

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        ActionResult result = ReceiveChatMessageEvent.EVENT.invoker().receiveChatMessage(MinecraftClient.getInstance(), packet.content().getString());

        if(result != ActionResult.PASS) {
            ci.cancel();
        }
    }

    @Inject(method = "onCommandSuggestions", at = @At("HEAD"))
    public void onCommandSuggestions(CommandSuggestionsS2CPacket packet, CallbackInfo ci) {
        CommandSuggestionsEvent.EVENT.invoker().commandSuggestions(MinecraftClient.getInstance(), packet.getCompletionId(), packet.getSuggestions());
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"))
    public void sendChatMessage(String message, CallbackInfo ci) {
        SendChatMessageEvent.EVENT.invoker().sendChatMessage(MinecraftClient.getInstance().player, message);
    }

    @Inject(method = "sendChatCommand", at = @At("HEAD"))
    public void sendChatCommand(String message, CallbackInfo ci) {
        SendChatMessageEvent.EVENT.invoker().sendChatMessage(MinecraftClient.getInstance().player, "/" + message);
    }

    // todo: They changed the ping measuring implementation. This gives me hope.
    /*@Inject(method = "onPing", at = @At("HEAD"))
    public void onPing(PlayPingS2CPacket packet, CallbackInfo ci) {
        ServerPingEvent.EVENT.invoker().serverPing(packet.getParameter());
    }*/
}
