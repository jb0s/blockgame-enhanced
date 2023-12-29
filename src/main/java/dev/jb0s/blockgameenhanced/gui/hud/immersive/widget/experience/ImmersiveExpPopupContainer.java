package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.experience;

import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.ImmersiveWidget;
import dev.jb0s.blockgameenhanced.gamefeature.mmostats.MMOProfession;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImmersiveExpPopupContainer extends ImmersiveWidget {
    private final LinkedHashMap<MMOProfession, ImmersiveExpPopup> popupHashMap;

    public ImmersiveExpPopupContainer(InGameHud inGameHud) {
        super(inGameHud);
        popupHashMap = new LinkedHashMap<>();
    }

    @Override
    public synchronized void render(DrawContext context, int x, int y, float tickDelta) {
        int i = 0;

        if(!popupHashMap.isEmpty()) {
            for(Map.Entry<MMOProfession, ImmersiveExpPopup> entry : popupHashMap.entrySet()) {
                int sx = x - (entry.getValue().getWidth() / 2);
                int sy = y - ((entry.getValue().getHeight() + 10) * i);
                entry.getValue().render(context, sx, sy, tickDelta);
                i++;
            }

            popupHashMap.values().removeIf((popup) -> popup.getInactivityTicks() >= 120);
        }
    }

    @Override
    public synchronized void tick() {
        for (Map.Entry<MMOProfession, ImmersiveExpPopup> entry : popupHashMap.entrySet()) {
            entry.getValue().tick();
        }
    }

    public synchronized void showExpPopup(MMOProfession profession, float percent, float gained) {
        if(popupHashMap.containsKey(profession)) {
            popupHashMap.get(profession).setPercentage(percent);
            popupHashMap.get(profession).setInactivityTicks(0);
            popupHashMap.get(profession).addGained(gained);
            return;
        }

        ImmersiveExpPopup popup = new ImmersiveExpPopup(getInGameHud(), profession, percent, gained);
        popupHashMap.put(profession, popup);
    }
}
