package dev.jb0s.blockgameenhanced.gui.screen.title;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.eggs.thor.ThorScreen;
import dev.jb0s.blockgameenhanced.manager.config.ConfigManager;
import dev.jb0s.blockgameenhanced.manager.update.GitHubRelease;
import dev.jb0s.blockgameenhanced.manager.update.UpdateManager;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.lang.reflect.Constructor;

public class TitleScreen extends Screen {
    private static final Identifier BLOCKGAME_LOGO_TEXTURE = new Identifier("blockgame", "textures/gui/title/blockgame.png");
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("blockgame", "textures/gui/title/titlescreen.png");
    private final TranslatableText BUTTON_PLAY = new TranslatableText("menu.blockgame.title.play");
    private final TranslatableText BUTTON_DEBUG = new TranslatableText("menu.blockgame.title.debug");
    private final TranslatableText BUTTON_WEBSITE = new TranslatableText("menu.blockgame.title.website");
    private final TranslatableText BUTTON_WIKI = new TranslatableText("menu.blockgame.title.wiki");
    private final TranslatableText WATERMARK = new TranslatableText("menu.blockgame.title.watermark");
    private final TranslatableText QUIT_TITLE = new TranslatableText("menu.blockgame.quit.title");
    private final TranslatableText QUIT_DESCRIPTION = new TranslatableText("menu.blockgame.quit.description");

    private FakePlayer fakePlayer;
    private int eggClicks;

    public TitleScreen() {
        super(Text.of("Title Screen"));
    }

    @Override
    public void init() {

        // If we load this screen and the user doesn't have custom title screens enabled, (this usually happens when the user has just changed this setting)
        // then we want to abandon this title screen and load the vanilla one. This system is kinda hacky but meh.
        if(!BlockgameEnhanced.getConfig().getAccessibilityConfig().enableCustomTitleScreen) {
            client.setScreen(new net.minecraft.client.gui.screen.TitleScreen());
            return;
        }

        ConfigManager configManager = BlockgameEnhancedClient.getConfigManager();

        // Initialize player
        fakePlayer = new FakePlayer();
        fakePlayer.getInventory().readNbt(configManager.getInventorySnapshot());

        // Initialize ui
        initButtons();
        initCopyright();
        initWatermark();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        renderPlayer(mouseX, mouseY);
        renderLogo(matrices, width, 1, 30);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        int sw = width;
        int sh = height;
        if(sh >= sw) {
            sw = (int)(sh * 1.77f);
        }
        if(sw >= sh) {
            sh = (int)(sw * 0.56f);
        }
        if(sh < height) {
            int missing = height - sh;
            sh += missing;
            sw += missing * 1.77f;
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, 0, sw, sh, sw, sh);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    /**
     * Renders the logo on the main menu.
     * @param matrices The MatrixStack to render on.
     * @param screenWidth The width of the screen.
     * @param alpha The alpha to render the logo at.
     * @param y2 The Y coordinate to render the logo at.
     */
    public void renderLogo(MatrixStack matrices, int screenWidth, float alpha, int y2) {
        RenderSystem.setShaderTexture(0, BLOCKGAME_LOGO_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        int textureWidth = (int)(232 / 1.3f);
        int textureHeight = (int)(66 / 1.3f);
        int i = (screenWidth / 4) - textureWidth / 2;
        DrawableHelper.drawTexture(matrices, i, y2, 0, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Renders the player preview on the title screen.
     * @param mouseX The screen space X-coordinate of the mouse, which the player will look at.
     * @param mouseY The screen space Y-coordinate of the mouse, which the player will look at.
     */
    private void renderPlayer(int mouseX, int mouseY) {
        try {
            int size = height / 5;
            int x = width - (size * 2);
            int y = height / 2 + (int)(size * 1.5f);
            InventoryScreen.drawEntity(x, y, size, -mouseX + x, -mouseY + y - size * 2 + size / 2f, fakePlayer);
        }
        catch(Exception e) {
            // cry, weep, shit pants and cum
            // this usually happens when player uses mods that alter player appearance
        }
    }

    /**
     * Initializes button widgets.
     */
    private void initButtons() {
        assert client != null;
        int l = (height / 2) - 7;

        // Add Website Button
        addDrawableChild(new ButtonWidget(width / 4 - 75, l + 24, 74, 20, BUTTON_WEBSITE, (button) -> Util.getOperatingSystem().open("https://blockgame.info")));

        // Add Wiki Button
        addDrawableChild(new ButtonWidget(width / 4 + 1, l + 24, 74, 20, BUTTON_WIKI, (button) -> Util.getOperatingSystem().open("https://blockgame.fandom.com/wiki/BlockGame_Wiki")));

        // Add Play Button
        addDrawableChild(new ButtonWidget(width / 4 - 75, l, 150, 20, BUTTON_PLAY, (button) -> ConnectScreen.connect(this, this.client, ServerAddress.parse("mc.blockgame.info"), new ServerInfo("BlockGame", "mc.blockgame.info", false))));

        int bottomRowYOffset = 0;
        if(BlockgameEnhanced.isModmenuPresent()) {
            bottomRowYOffset = 24;

            addDrawableChild(new ButtonWidget(width / 4 - 75, l + 48, 150, 20, Text.of("Mods"), (button) -> {
                // Detect if ModMenu mod is present
                try
                {
                    Class<?> modsScreenClass = Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");
                    Constructor<?> constructor = modsScreenClass.getConstructor(Screen.class);
                    Screen screen = (Screen)constructor.newInstance(this);

                    client.setScreen(screen);
                }
                catch (Exception e)
                {
                    client.getToastManager().add(new SystemToast(SystemToast.Type.PACK_LOAD_FAILURE, Text.of("Error"), Text.of(e.getMessage())));
                    client.setScreen(new ThorScreen(this));
                }
            }));
        }

        // Add Options Button
        addDrawableChild(new ButtonWidget(width / 4 - 75, l + 48 + bottomRowYOffset, 74, 20, new TranslatableText("menu.options"), (button) -> {
            assert client != null;
            client.setScreen(new OptionsScreen(this, client.options));
        }));

        // Add Quit Game Button
        addDrawableChild(new ButtonWidget(width / 4 + 1, l + 48 + bottomRowYOffset, 74, 20, new TranslatableText("menu.quit"), (button) -> {
            client.scheduleStop();
        }));
    }

    /**
     * Initializes the copyright notice from Mojang.
     */
    private void initCopyright() {
        assert client != null;
        int i = this.textRenderer.getWidth(net.minecraft.client.gui.screen.TitleScreen.COPYRIGHT);
        int j = this.width - i - 2;

        addDrawableChild(new PressableTextWidget(j, this.height - 22, i, 10, net.minecraft.client.gui.screen.TitleScreen.COPYRIGHT, (button) -> client.setScreen(new CreditsScreen(false, Runnables.doNothing())), textRenderer));
    }

    /**
     * Initializes the mod credit watermark.
     */
    private void initWatermark() {
        assert client != null;
        int i = this.textRenderer.getWidth(WATERMARK);
        int j = this.width - i - 2;
        this.addDrawableChild(new PressableTextWidget(j, this.height - 10, i, 10, WATERMARK, (button) -> {
            eggClicks++;
            if(eggClicks % 7 == 0) {
                client.setScreen(new ThorScreen(this));
            }
        }, this.textRenderer));
    }
}
