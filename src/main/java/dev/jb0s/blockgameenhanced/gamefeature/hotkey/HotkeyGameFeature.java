package dev.jb0s.blockgameenhanced.gamefeature.hotkey;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.hotkey.bind.*;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class HotkeyGameFeature extends GameFeature {
    private static final ArrayList<Hotkey> HOTKEYS = new ArrayList<>();

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);

        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.blockgame.warp_menu", WarpMenuBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.blockgame.auction_house", AuctionHouseBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.blockgame.stats", StatsBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.blockgame.profile", ProfileBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "key.blockgame.ranks", RanksBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "key.blockgame.party", PartyBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.blockgame.backpack", BackpackBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, "key.blockgame.disposal", DisposalBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, "key.blockgame.deposit", DepositBind::handlePressed); // this is such a shit bind
        bind(InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_MIDDLE, "key.blockgame.ping", PingBind::handlePressed);

        if(BlockgameEnhanced.DEBUG) {
            bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "key.blockgame.debug", DebugBind::handlePressed);
        }
    }

    @Override
    public void tick() {
        for (Hotkey hotkey : HOTKEYS) {
            while(hotkey.keyBinding().wasPressed()) {
                hotkey.action().pressed(getMinecraftClient());
            }
        }
    }

    /**
     * Bind a key to a HotkeyAction.
     * @param keyType The type of key to bind.
     * @param key The Key ID to bind.
     * @param translationKey The translation key for the hotkeys name.
     * @param func The callback to invoke when the key is pressed.
     */
    public void bind(InputUtil.Type keyType, int key, String translationKey, HotkeyAction func) {
        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(translationKey, keyType, key, "category.blockgame.keybinds"));
        HOTKEYS.add(new Hotkey(keyBinding, func));
    }
}
