package dev.jb0s.blockgameenhanced.gamefeature.challenges;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.gamefeature.challenges.ChallengeStartedEvent;
import dev.jb0s.blockgameenhanced.event.screen.ScreenInitEvent;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.challenges.speedrun.SpeedrunKrognarsChallenge;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

public class ChallengesGameFeature extends GameFeature {

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);

        ScreenInitEvent.END.register(this::onClientScreenChanged);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public void startChallenge(Challenge challenge) {
        challenge.preStart();
        challenge.start();
        ChallengeStartedEvent.EVENT.invoker().challengeStartedEvent(challenge);
    }

    /**
     * Replaces the "Open to LAN" button with a "Challenges" button on the Game Menu
     * @param screen Screen instance
     */
    private void onClientScreenChanged(Screen screen) {
        if(screen instanceof GameMenuScreen) {
            final List<ClickableWidget> buttons = Screens.getButtons(screen);
            ClickableWidget shareToLanBtn = null;

            for (ClickableWidget button : buttons) {
                boolean isShareToLanBtn = buttonTextEquals(button, "menu.shareToLan");

                if (isShareToLanBtn) {
                    shareToLanBtn = button;
                }
            }

            if(shareToLanBtn != null) {
                screen.remove(shareToLanBtn);
                screen.addDrawableChild(new ButtonWidget(shareToLanBtn.x, shareToLanBtn.y, shareToLanBtn.getWidth(), shareToLanBtn.getHeight(), Text.of("Challenges"), button -> {
                    startChallenge(new SpeedrunKrognarsChallenge());
                }));
            }
        }
    }

    private static boolean buttonTextEquals(ClickableWidget button, String translationKey) {
        Text text = button.getMessage();
        return text instanceof TranslatableText && ((TranslatableText) text).getKey().equals(translationKey);
    }
}
