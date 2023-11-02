package dev.jb0s.blockgameenhanced.gui.screen;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.gui.screen.title.TitleScreen;
import dev.jb0s.blockgameenhanced.manager.config.modules.ModConfig;
import dev.jb0s.blockgameenhanced.manager.update.GitHubRelease;
import lombok.Getter;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class UpdateScreen extends Screen {

    @Getter
    private static boolean answered;

    private GitHubRelease release;
    private Text MESSAGE_TEXT_1;

    private static final Text HEADER_TEXT = Text.translatable("menu.blockgame.update.header");
    private static final Text MESSAGE_TEXT_2  = Text.translatable("menu.blockgame.update.message.2");
    private static final Text UPDATE_BUTTON_TEXT = Text.translatable("menu.blockgame.update.button.update");
    private static final Text UPDATE_SNOOZE_TEXT = Text.translatable("menu.blockgame.update.button.snooze");
    private static final Text UPDATE_IGNORE_TEXT = Text.translatable("menu.blockgame.update.button.ignore");

    public UpdateScreen(GitHubRelease release) {
        super(HEADER_TEXT);
        this.release = release;

        // Fuck Java
        MESSAGE_TEXT_1 = Text.translatable("menu.blockgame.update.message.1", (release != null ? release.tag_name : "v?.?.?"));
    }

    @Override
    public void init() {
        addButtons(height - 75);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        // Render message
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 50, 16777215);
        context.drawCenteredTextWithShadow(textRenderer, MESSAGE_TEXT_1, width / 2, 70, 16777215);
        context.drawCenteredTextWithShadow(textRenderer, MESSAGE_TEXT_2, width / 2, 70 + textRenderer.fontHeight + 3, 16777215);

        super.render(context, mouseX, mouseY, delta);
    }

    protected void addButtons(int y) {
        addDrawableChild(ButtonWidget.builder(UPDATE_BUTTON_TEXT, (button) -> {
            Util.getOperatingSystem().open(release.html_url);
        }).position(width / 2 - 155, y).size(150, 20).build());
        addDrawableChild(ButtonWidget.builder(UPDATE_SNOOZE_TEXT, (button) -> {
            answer();
        }).position(width / 2 - 155 + 160, y).size(150, 20).build());
        addDrawableChild(ButtonWidget.builder(UPDATE_IGNORE_TEXT, (button) -> {
            // Turn off update checks in user config
            BlockgameEnhanced.getConfig().getAccessibilityConfig().enableUpdateChecker = false;
            AutoConfig.getConfigHolder(ModConfig.class).save();

            answer();
        }).position(width / 2 - 155, y + 25).size(310, 20).build());
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
