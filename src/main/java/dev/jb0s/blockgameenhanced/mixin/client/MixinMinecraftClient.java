package dev.jb0s.blockgameenhanced.mixin.client;

import dev.jb0s.blockgameenhanced.event.client.ClientDisconnectionEvents;
import dev.jb0s.blockgameenhanced.event.client.ClientLifetimeEvents;
import dev.jb0s.blockgameenhanced.event.client.ClientScreenChanged;
import dev.jb0s.blockgameenhanced.event.sound.MusicTypeAccessedEvent;
import dev.jb0s.blockgameenhanced.event.world.WorldUpdatedEvent;
import lombok.SneakyThrows;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.MusicSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Inject(method = "getMusicType", at = @At("RETURN"), cancellable = true)
    public void getMusicType(CallbackInfoReturnable<MusicSound> cir) {
        MusicSound musicSound = MusicTypeAccessedEvent.EVENT.invoker().musicTypeAccessed();
        if (musicSound != null) {
            cir.setReturnValue(musicSound);
        }
    }

    @Inject(method = "setWorld", at = @At("RETURN"))
    public void setWorld(ClientWorld world, CallbackInfo ci) {
        WorldUpdatedEvent.EVENT.invoker().worldUpdated(world);
    }

    @Inject(method = "setScreen", at = @At("RETURN"))
    public void setScreen(Screen screen, CallbackInfo ci) {
        MinecraftClient thisMinecraft = (MinecraftClient) (Object) this;
        ClientScreenChanged.EVENT.invoker().onScreenChanged(thisMinecraft, screen);
    }

    @SneakyThrows
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(RunArgs args, CallbackInfo ci) {
        MinecraftClient thisMinecraft = (MinecraftClient) (Object) this;
        ClientLifetimeEvents.INIT.invoker().onClientInit(thisMinecraft, args);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    public void preDisconnect(Screen screen, CallbackInfo ci) {
        MinecraftClient thisMinecraft = (MinecraftClient) (Object) this;
        ClientDisconnectionEvents.PRE_DISCONNECT.invoker().onClientPreDisconnect(thisMinecraft);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("RETURN"))
    public void postDisconnect(Screen screen, CallbackInfo ci) {
        MinecraftClient thisMinecraft = (MinecraftClient) (Object) this;
        ClientDisconnectionEvents.POST_DISCONNECT.invoker().onClientPostDisconnect(thisMinecraft);
    }

    @Inject(method = "stop", at = @At("HEAD"))
    public void stop(CallbackInfo ci) {
        MinecraftClient thisMinecraft = (MinecraftClient) (Object) this;
        ClientLifetimeEvents.STOP.invoker().onClientStop(thisMinecraft);
    }

    @Inject(method = "close", at = @At("HEAD"))
    public void close(CallbackInfo ci) {
        MinecraftClient thisMinecraft = (MinecraftClient) (Object) this;
        ClientLifetimeEvents.CLOSE.invoker().onClientClose(thisMinecraft);
    }
}
