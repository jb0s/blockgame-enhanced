package dev.jb0s.blockgameenhanced.gui.screen;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.gui.screen.title.TitleScreen;
import dev.jb0s.blockgameenhanced.config.modules.ModConfig;
import dev.jb0s.blockgameenhanced.update.GitHubRelease;
import lombok.Getter;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class UpdateScreen extends Screen {

    @Getter
    private static boolean answered;

    private GitHubRelease release;
    private Text MESSAGE_TEXT_1;

    private static final Text HEADER_TEXT = new TranslatableText("menu.blockgame.update.header");
    private static final Text MESSAGE_TEXT_2  = new TranslatableText("menu.blockgame.update.message.2");
    private static final Text UPDATE_BUTTON_TEXT = new TranslatableText("menu.blockgame.update.button.update");
    private static final Text UPDATE_SNOOZE_TEXT = new TranslatableText("menu.blockgame.update.button.snooze");
    private static final Text UPDATE_IGNORE_TEXT = new TranslatableText("menu.blockgame.update.button.ignore");

    public UpdateScreen(GitHubRelease release) {
        super(HEADER_TEXT);
        this.release = release;

        // Fuck Java
        MESSAGE_TEXT_1 = new TranslatableText("menu.blockgame.update.message.1", (release != null ? release.tag_name : "v?.?.?"));
    }

    @Override
    public void init() {
        addButtons(height - 75);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        // Render message
        drawCenteredText(matrices, textRenderer, title, width / 2, 50, 0xFFFFFF);
        drawCenteredText(matrices, textRenderer, MESSAGE_TEXT_1, width / 2, 70, 0xFFFFFF);
        drawCenteredText(matrices, textRenderer, MESSAGE_TEXT_2, width / 2, 70 + textRenderer.fontHeight + 3, 0xFFFFFF);
        drawCenteredText(matrices, textRenderer, Text.of("NOTE: You will be redirected to Modrinth from now on. Hold shift to use GitHub."), width / 2, 70 + ((textRenderer.fontHeight + 3) * 2) + 7, 0x757575);

        super.render(matrices, mouseX, mouseY, delta);
    }

    protected void addButtons(int y) {
        addDrawableChild(new ButtonWidget(width / 2 - 155, y, 150, 20, UPDATE_BUTTON_TEXT, (button) -> {
            if(hasShiftDown()) {
                Util.getOperatingSystem().open(release.html_url);
            }
            else {
                String ver = release.tag_name.substring(1);
                String url = String.format("https://modrinth.com/mod/blockgame-enhanced/versions/%s", ver);
                Util.getOperatingSystem().open(url);
            }
        }));
        addDrawableChild(new ButtonWidget(width / 2 - 155 + 160, y, 150, 20, UPDATE_SNOOZE_TEXT, (button) -> {
            answer();
        }));
        addDrawableChild(new ButtonWidget(width / 2 - 155, y + 25, 310, 20, UPDATE_IGNORE_TEXT, (button) -> {
            // Turn off update checks in user config
            BlockgameEnhanced.getConfig().getAccessibilityConfig().enableUpdateChecker = false;
            AutoConfig.getConfigHolder(ModConfig.class).save();

            answer();
        }));
    }

    /**
     * Function that handles user answering no on the update prompt.
     */
    private void answer() {
        assert client != null;
        answered = true; // Don't ask again during this session

        boolean useCustomTitleScreen = BlockgameEnhanced.getConfig().getAccessibilityConfig().enableCustomTitleScreen;
        client.setScreen(useCustomTitleScreen ? new TitleScreen() : new net.minecraft.client.gui.screen.TitleScreen());
    }
}
