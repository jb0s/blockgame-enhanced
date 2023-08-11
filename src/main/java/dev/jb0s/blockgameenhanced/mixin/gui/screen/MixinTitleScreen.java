package dev.jb0s.blockgameenhanced.mixin.gui.screen;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.gui.screen.UpdateScreen;
import dev.jb0s.blockgameenhanced.manager.update.GitHubRelease;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        GitHubRelease update = BlockgameEnhancedClient.getAvailableUpdate();
        if(update != null && !UpdateScreen.isAnswered()) {
            client.setScreen(new UpdateScreen(update));
            return;
        }

        // If the user has custom title screens enabled, we abandon this screen and show our custom one instead.
        if(BlockgameEnhanced.getConfig().getAccessibilityConfig().enableCustomTitleScreen) {
            client.setScreen(new dev.jb0s.blockgameenhanced.gui.screen.title.TitleScreen());
        }
    }
}
