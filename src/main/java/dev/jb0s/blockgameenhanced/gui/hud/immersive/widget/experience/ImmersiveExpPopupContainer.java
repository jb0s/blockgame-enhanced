package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.experience;

import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.ImmersiveWidget;
import dev.jb0s.blockgameenhanced.gamefeature.mmostats.MMOProfession;
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
    public void render(MatrixStack matrices, int x, int y, float tickDelta) {
        int i = 0;

        if(!popupHashMap.isEmpty()) {
            for(Iterator<Map.Entry<MMOProfession, ImmersiveExpPopup>> it = popupHashMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<MMOProfession, ImmersiveExpPopup> entry = it.next();
                int sx = x - (entry.getValue().getWidth() / 2);
                int sy = y - ((entry.getValue().getHeight() + 10) * i);
                entry.getValue().render(matrices, sx, sy, tickDelta);
                i++;

                if(entry.getValue().getInactivityTicks() >= 120) {
                    it.remove();
                }
            }
        }
    }

    @Override
    public void tick() {
        for (Map.Entry<MMOProfession, ImmersiveExpPopup> entry : popupHashMap.entrySet()) {
            entry.getValue().tick();
        }
    }

    public void showExpPopup(MMOProfession profession, float percent, float gained) {
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
