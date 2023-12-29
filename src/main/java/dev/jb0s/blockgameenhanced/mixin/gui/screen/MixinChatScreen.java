package dev.jb0s.blockgameenhanced.mixin.gui.screen;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.config.modules.ChatChannelsConfig;
import dev.jb0s.blockgameenhanced.event.gamefeature.chatchannels.ChatChannelRequestedEvent;
import dev.jb0s.blockgameenhanced.event.gamefeature.chatchannels.ChatChannelToggledEvent;
import dev.jb0s.blockgameenhanced.event.gamefeature.chatchannels.ChatChannelUpdatedEvent;
import dev.jb0s.blockgameenhanced.gamefeature.chatchannels.ChatChannel;
import dev.jb0s.blockgameenhanced.gamefeature.chatchannels.ChatChannelsGameFeature;
import dev.jb0s.blockgameenhanced.gui.widgets.FlexibleButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
@Environment(EnvType.CLIENT)
public class MixinChatScreen {
    @Unique
    private static final int KEYCODE_ENTER = 257;
    @Unique
    private static final int KEYCODE_KP_ENTER = 335;

    private static final ChatChannelsConfig CONFIG = BlockgameEnhanced.getConfig().getChatChannelsConfig();

    @Shadow
    protected TextFieldWidget chatField;
    @Unique
    protected FlexibleButtonWidget toggleButton;
    @Unique
    private ChatChannel selectedChannel;

    @Unique
    void addButton() {
        ChatScreen target = (ChatScreen) (Object) this;
        chatField.setX(CONFIG.compactButton ? 16 : 56);
        chatField.setWidth(target.width - chatField.getX());

        toggleButton = new FlexibleButtonWidget(2, chatField.getY() - 2, CONFIG.compactButton ? 12 : 52, 12, Text.literal(""), (button) -> {
            ChatChannelToggledEvent.EVENT.invoker().chatChannelToggled(ChatChannelToggledEvent.Direction.NEXT);
        });
        toggleButton.setRightClickAction((button) -> {
            ChatChannelToggledEvent.EVENT.invoker().chatChannelToggled(ChatChannelToggledEvent.Direction.PREV);
        });
        target.addDrawableChild(toggleButton);
    }

    @Unique
    void removeButton() {
        ChatScreen target = (ChatScreen) (Object) this;
        chatField.setX(4);
        chatField.setWidth(target.width - 4);
        target.remove(toggleButton);
        toggleButton = null;
    }

    @Inject(
            method = "init",
            at = @At("TAIL")
    )
    protected void init(CallbackInfo ci) {
        ChatChannelUpdatedEvent.EVENT.register((ChatChannelsGameFeature gameFeature) -> {
            selectedChannel = gameFeature.getSelectedChannel();
            if (toggleButton == null && selectedChannel != null) {
                addButton();
            } else if (toggleButton != null && selectedChannel == null) {
                removeButton();
            }

            if (toggleButton != null) {
                setButtonMessage(false);
            }
        });
        ChatChannelRequestedEvent.EVENT.invoker().chatChannelRequested();
    }

    @Unique
    private void setButtonMessage(boolean strikethrough) {
        Text newMessage = selectedChannel.formattedName;
        if (CONFIG.compactButton) {
            newMessage = Text.literal(selectedChannel.formattedName.asTruncatedString(1))
                    .setStyle(selectedChannel.formattedName.getStyle());
        }

        if (strikethrough) {
            newMessage = newMessage.copy().formatted(Formatting.STRIKETHROUGH);
        }

        toggleButton.setMessage(newMessage);
    }

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (toggleButton == null) {
            return;
        }

        toggleButton.render(context, mouseX, mouseY, delta);
    }

    @Inject(
            method = "onChatFieldUpdate",
            at = @At("TAIL")
    )
    private void onChatFieldUpdate(String chatText, CallbackInfo ci) {
        if (!CONFIG.enable) {
            return;
        }

        boolean isCommand = chatText.startsWith("/");
        boolean hasStrikethrough = toggleButton.getMessage().getStyle().isStrikethrough();
        if (isCommand && !hasStrikethrough) {
            // visual indication that we are not sending this to the currently selected channel
            setButtonMessage(true);
        } else if (!isCommand && hasStrikethrough) {
            setButtonMessage(false);
        }
    }

    @Inject(
            method = "keyPressed",
            at = @At("HEAD"),
            cancellable = true
    )
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!CONFIG.enable) {
            return;
        }


        if (keyCode != KEYCODE_ENTER && keyCode != KEYCODE_KP_ENTER) {
            return;
        }


        String string = chatField.getText().trim();
        boolean isCommand = string.startsWith("/");
        ChatScreen target = (ChatScreen) (Object) this;
        // important to ignore anything starting with a slash, otherwise the command will be sent to the channel
        if (selectedChannel != null && !selectedChannel.canSwitch && !isCommand) {
            target.sendMessage(String.format("%s%s", selectedChannel.command, string), false);
        } else {
            target.sendMessage(string, false);
        }

        if (CONFIG.closeChatAfterMessage) {
            MinecraftClient.getInstance().setScreen(null);
        } else {
            chatField.setText("");
        }

        cir.setReturnValue(true);
        cir.cancel();
    }
}
