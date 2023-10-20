package dev.jb0s.blockgameenhanced.manager.hotkey;

import dev.jb0s.blockgameenhanced.manager.Manager;
import dev.jb0s.blockgameenhanced.manager.hotkey.bind.*;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class HotkeyManager extends Manager {
    private static final ArrayList<Hotkey> HOTKEYS = new ArrayList<>();

    @Override
    public void init() {
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.blockgame.warp_menu", WarpMenuBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.blockgame.auction_house", AuctionHouseBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.blockgame.stats", StatsBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.blockgame.profile", ProfileBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "key.blockgame.ranks", RanksBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "key.blockgame.party", PartyBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.blockgame.backpack", BackpackBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, "key.blockgame.disposal", DisposalBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, "key.blockgame.deposit", DepositBind::handlePressed); // this is such a shit bind
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "key.blockgame.debug", DebugBind::handlePressed);
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "key.blockgame.toggle_exp_hud", ToggleExpHudBind::handlePressed); // Toggle Exp Hud Render
        bind(InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_HOME, "key.blockgame.switch_exp_hud", SwitchExpModiBind::handlePressed); // Switch between hud modes
    }

    @Override
    public void tick(MinecraftClient client) {
        for (Hotkey hotkey : HOTKEYS) {
            while(hotkey.keyBinding().wasPressed()) {
                hotkey.action().pressed(client);
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
