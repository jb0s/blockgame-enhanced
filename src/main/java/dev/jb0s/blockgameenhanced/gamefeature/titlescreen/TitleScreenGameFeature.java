package dev.jb0s.blockgameenhanced.gamefeature.titlescreen;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.client.ClientScreenChanged;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.gui.screen.title.TitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;

public class TitleScreenGameFeature extends GameFeature {

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);
        ClientScreenChanged.EVENT.register(this::onClientScreenChanged);
    }

    private void onClientScreenChanged(MinecraftClient client, Screen screen) {

        // Don't do any of this if the user doesn't want a custom title screen
        if(!BlockgameEnhanced.getConfig().getAccessibilityConfig().enableCustomTitleScreen)  {
            return;
        }

        if(screen instanceof net.minecraft.client.gui.screen.TitleScreen || screen instanceof MultiplayerScreen) {
            client.setScreen(new TitleScreen());
        }
    }
}
