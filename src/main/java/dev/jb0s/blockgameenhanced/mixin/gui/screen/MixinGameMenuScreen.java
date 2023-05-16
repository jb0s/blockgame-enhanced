package dev.jb0s.blockgameenhanced.mixin.gui.screen;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(OptionsScreen.class)
public class MixinGameMenuScreen extends Screen {
    protected MixinGameMenuScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        final List<ClickableWidget> buttons = Screens.getButtons(this);
        for (int i = 0; i < buttons.size(); i++) {
            ClickableWidget button = buttons.get(i);
            boolean isResourcePackBtn = buttonTextEquals(button, "options.resourcepack");

            if(isResourcePackBtn) {
                buttons.add(i + 1, new ButtonWidget(button.x, button.y + 24, button.getWidth(), button.getHeight(), new TranslatableText("menu.blockgame.options"), (a) -> {
                    assert client != null;

                    Screen cfgScr = BlockgameEnhancedClient.getConfigManager().constructConfigScreen(this);
                    client.setScreen(cfgScr);
                }));
            }
        }
    }

    private static boolean buttonTextEquals(ClickableWidget button, String translationKey) {
        Text text = button.getMessage();
        return text instanceof TranslatableText && ((TranslatableText) text).getKey().equals(translationKey);
    }
}
