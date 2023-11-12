package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.pickups;

import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.ImmersiveWidget;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImmersivePickupStream extends ImmersiveWidget {
    private final LinkedHashMap<Text, ImmersivePickup> pickupHashMap;

    public ImmersivePickupStream(InGameHud inGameHud) {
        super(inGameHud);
        pickupHashMap = new LinkedHashMap<>();
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, float tickDelta) {
        int i = 0;

        if(!pickupHashMap.isEmpty()) {
            Iterator<Map.Entry<Text, ImmersivePickup>> it = pickupHashMap.entrySet().iterator();

            while(it.hasNext()) {
                Map.Entry<Text, ImmersivePickup> entry = it.next();
                int sx = x - entry.getValue().getWidth();
                int sy = y + ((entry.getValue().getHeight() + 5) * i);
                entry.getValue().render(matrices, sx, sy, tickDelta);
                i++;

                if(entry.getValue().getInactivityTicks() >= 120) {
                    it.remove();
                }
            }
        }
    }

    @Override
    public int getWidth() {
        int winningWidth = 0;
        for (Map.Entry<Text, ImmersivePickup> entry : pickupHashMap.entrySet()) {
            if(entry.getValue().getWidth() > winningWidth) {
                winningWidth = entry.getValue().getWidth();
            }
        }

        return winningWidth;
    }

    @Override
    public int getHeight() {
        return 25 * pickupHashMap.size();
    }

    @Override
    public void tick() {
        for (Map.Entry<Text, ImmersivePickup> entry : pickupHashMap.entrySet()) {
            entry.getValue().tick();
        }
    }

    public void addPickup(ItemStack itemStack, int amount) {
        if(pickupHashMap.containsKey(itemStack.getName())) {
            pickupHashMap.get(itemStack.getName()).addAmount(amount);
            pickupHashMap.get(itemStack.getName()).setInactivityTicks(0);
            return;
        }

        ImmersivePickup pickup = new ImmersivePickup(getInGameHud(), itemStack.getName(), itemStack, amount);
        pickupHashMap.put(itemStack.getName(), pickup);
    }
}
