package dev.jb0s.blockgameenhanced.gamefeature.recipetracker;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.screen.ScreenOpenedEvent;
import dev.jb0s.blockgameenhanced.event.screen.ScreenReceivedInventoryEvent;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.KnowledgeBookItem;
import net.minecraft.item.SkullItem;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.util.ActionResult;

import java.util.List;

public class RecipeTrackerGameFeature extends GameFeature {

    @Getter
    private int listeningSyncId;

    private static final int RECIPE_PREVIEW_BACK_BUTTON_INDEX = 10;
    private static final int RECIPE_PREVIEW_TRACK_BUTTON_INDEX = 19;
    private static final int RECIPE_PREVIEW_CRAFT_BUTTON_INDEX = 28;

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);

        ScreenOpenedEvent.EVENT.register(this::handleScreen);
        ScreenReceivedInventoryEvent.EVENT.register(this::handleScreenInventory);
    }

    /**
     * Sets listeningSyncId to this screen's sync id if its name is "Recipe Preview"
     */
    private ActionResult handleScreen(OpenScreenS2CPacket packet) {
        if(packet.getName().getString().equals("Recipe Preview")) {
            listeningSyncId = packet.getSyncId();
        }

        return ActionResult.PASS;
    }

    private ActionResult handleScreenInventory(InventoryS2CPacket packet) {

        // Ensure that this inventory data belongs to the screen we're listening to
        if(packet.getSyncId() != listeningSyncId) {
            return ActionResult.PASS;
        }

        List<ItemStack> inv = packet.getContents();
        boolean hasBackButton = inv.get(RECIPE_PREVIEW_BACK_BUTTON_INDEX).getItem() instanceof SkullItem;
        boolean hasCraftButton = inv.get(RECIPE_PREVIEW_CRAFT_BUTTON_INDEX).getItem() instanceof KnowledgeBookItem;
        boolean isScreenValid = hasBackButton && hasCraftButton;

        // Ensure that this is a valid recipe preview screen
        if(!isScreenValid) {
            return ActionResult.PASS;
        }

        BlockgameEnhanced.LOGGER.info("Valid recipe screen!");
        return ActionResult.PASS;
    }
}
