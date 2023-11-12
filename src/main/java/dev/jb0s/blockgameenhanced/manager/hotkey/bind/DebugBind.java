package dev.jb0s.blockgameenhanced.manager.hotkey.bind;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.eggs.ThorScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ActionResult;

import java.lang.reflect.Constructor;

public class DebugBind {
    public static ActionResult handlePressed(MinecraftClient client) {
        if(client.player == null) {
            return ActionResult.FAIL;
        }

        try {
            if(BlockgameEnhanced.DEBUG) {
                Class<?> modsScreenClass = Class.forName("dev.jb0s.blockgameenhanced.debug.ImGuiScreen");
                Constructor<?> constructor = modsScreenClass.getConstructor();
                Screen screen = (Screen)constructor.newInstance();

                client.setScreen(screen);
                return ActionResult.SUCCESS;
            }
        }
        catch (Exception e) {
            // only happens if this bind is performed on a release build which should be impossible
            client.setScreen(new ThorScreen(null));
            return ActionResult.FAIL;
        }
        
        return ActionResult.FAIL;
    }
}
