package dev.jb0s.blockgameenhanced.gamefeature.updateprompter;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.client.ClientScreenChanged;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.gui.screen.UpdateScreen;
import dev.jb0s.blockgameenhanced.gui.screen.title.TitleScreen;
import dev.jb0s.blockgameenhanced.update.GitHubRelease;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class UpdatePrompterGameFeature extends GameFeature {

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);

        ClientScreenChanged.EVENT.register(this::onClientScreenChanged);
    }

    private void onClientScreenChanged(MinecraftClient client, Screen screen) {
        boolean isMenuScreen = screen instanceof TitleScreen || screen instanceof net.minecraft.client.gui.screen.TitleScreen;
        if(!isMenuScreen) return;

        // If update is available and unanswered, show that up first
        GitHubRelease update = BlockgameEnhancedClient.getAvailableUpdate();
        if(update != null && !UpdateScreen.isAnswered()) {
            client.setScreen(new UpdateScreen(update));
        }
    }
}
