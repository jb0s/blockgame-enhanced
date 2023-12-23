package dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.pickups;

import dev.jb0s.blockgameenhanced.gui.hud.immersive.widget.ImmersiveWidget;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

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
        if(!pickupHashMap.isEmpty()) {
            ImmersivePickup[] list = getPickupsSafe();

            for (int i = 0; i < list.length; i++) {
                ImmersivePickup entry = list[i];
                int sx = x - entry.getWidth();
                int sy = y + ((entry.getHeight() + 5) * i);
                entry.render(matrices, sx, sy, tickDelta);
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
        if(pickupHashMap.isEmpty()) {
            return;
        }

        // Clear inactive shit
        pickupHashMap.entrySet().removeIf(x -> x.getValue().getInactivityTicks() > 120);

        // Tick pickups after
        ImmersivePickup[] list = getPickupsSafe();
        for (ImmersivePickup immersivePickup : list) {
            immersivePickup.tick();
        }
    }

    public void addPickup(ItemStack itemStack, int amount) {
        if(pickupHashMap.containsKey(itemStack.getName())) {

            // BUG: If we don't do this check the count will be doubled.
            // This is because the godforsaken server sends two packets when picking up a single item. (≖､≖╬)
            if(pickupHashMap.get(itemStack.getName()).getInactivityTicks() == 0) {
                return;
            }

            pickupHashMap.get(itemStack.getName()).addAmount(amount);
            pickupHashMap.get(itemStack.getName()).setInactivityTicks(0);
            return;
        }

        ImmersivePickup pickup = new ImmersivePickup(getInGameHud(), itemStack.getName(), itemStack, amount);
        pickupHashMap.put(itemStack.getName(), pickup);
    }

    /**
     * Gets a clone of the pickup hashmap to avoid multithread madness.
     * todo: Replace with a better solution that doesn't impact memory. (not like this game isn't garbage memory wise anyways)
     */
    private ImmersivePickup[] getPickupsSafe() {
        return pickupHashMap.values().toArray(new ImmersivePickup[0]);
    }
}
