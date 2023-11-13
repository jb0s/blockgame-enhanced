package dev.jb0s.blockgameenhanced.gamefeature.immersivehud;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.client.ClientInitEvent;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.ImmersiveIngameHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;

public class ImmersiveHUDGameFeature extends GameFeature {

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);
        ClientInitEvent.EVENT.register(this::onClientInit);
    }

    /**
     * Applies Immersive UI to the client if enabled.
     * @param client
     * @param runArgs
     */
    private void onClientInit(MinecraftClient client, RunArgs runArgs) {
        client.inGameHud = new ImmersiveIngameHud(client);
    }

    @Override
    public boolean isEnabled() {
        return BlockgameEnhanced.getConfig().getIngameHudConfig().enableCustomHud;
    }
}
