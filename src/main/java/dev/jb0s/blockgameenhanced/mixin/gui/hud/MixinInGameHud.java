package dev.jb0s.blockgameenhanced.mixin.gui.hud;

import dev.jb0s.blockgameenhanced.gui.hud.FadeHud;
import dev.jb0s.blockgameenhanced.gui.hud.PartyHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {
    @Shadow @Final private MinecraftClient client;
    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(method = "render", at = @At("RETURN"))
    public void postRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        // Render HUDs
        PartyHud.render(context, tickDelta);

        // Fade-in effect in the first second of lifetime
        if(client.player != null && client.player.age <= 20) {
            FadeHud.render(context, client);
        }
    }

    /*@ModifyArg(method = "addChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ClientChatListener;onChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;Ljava/util/UUID;)V"))
    public Text editChatMessage(MessageType type, Text message, UUID sender) {
        boolean isFromDeveloper = sender.equals(UUID.fromString("5c36ccfa-4250-45b2-b0b5-22ef12fd2f17"));

        // If this message is not sent by mod developer then ignore event
        if(!isFromDeveloper) {
            return message;
        }

        Text prefix = Text.of("§7[§6Java Enthusiast§7] ");
        return prefix.copy().append(message);
    }*/
}
