package dev.jb0s.blockgameenhanced.mixin.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.gamefeature.mmovendor.MMOVendor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(GenericContainerScreen.class)
public class MixinGenericContainerScreen extends HandledScreen<GenericContainerScreenHandler> implements ScreenHandlerProvider<GenericContainerScreenHandler> {
    @Shadow @Final private int rows;

    private static final Identifier TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private static final Text LOOT_ALL_BUTTON = Text.translatable("menu.blockgame.container.plunder");

    public MixinGenericContainerScreen(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init(); // right. forgot about that

        // If this is not a regular chest we're interacting with, don't add the loot all button
        String titleStr = title.getString();
        boolean isValidContainer = titleStr.endsWith("Plunder") || titleStr.endsWith("Chest") || titleStr.endsWith("Shulker Box") || titleStr.endsWith("Barrel");
        if(!titleStr.isEmpty() && !isValidContainer) {
            return;
        }

        // If the user has disabled the Loot All button, cancel here
        if(!BlockgameEnhanced.getConfig().getAccessibilityConfig().enableLootAllButton) {
            return;
        }

        int originX = width / 2;
        int originY = height / 2;
        int btnWidth = 50;
        int btnHeight = 13;

        int x = originX + 34;
        int y = originY - (backgroundHeight / 2) + 3;
        addDrawableChild(ButtonWidget.builder(LOOT_ALL_BUTTON, (button) -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                ClientPlayerEntity p = mc.player;

                for(int i = 0; i < 9 * rows; ++i) {
                    mc.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.QUICK_MOVE, p);
                }
            })
            .dimensions(x, y, btnWidth, btnHeight)
            .build()
        );

        // Just in case. Myrkheim chests are instanced, but disappear every reset
        // so there's a chance of losing items. Plus we most likely want to just
        // loot anyways
        if (titleStr.endsWith("Plunder"))
            return;

        y += 18 * rows + 14;
        addDrawableChild(ButtonWidget.builder(Text.literal("Store All"), (button) -> {
                    MinecraftClient mc = MinecraftClient.getInstance();
                    ClientPlayerEntity p = mc.player;

                    for (int i = 9 * rows; i < 9 * (rows + 3); i++) {
                        Slot currentSlot = handler.slots.get(i);
                        boolean isBackpack = false;
                        List<Text> nameSiblings = currentSlot.getStack().getName().getSiblings();
                        if (!nameSiblings.isEmpty())
                            for (Text sibling : nameSiblings)
                                if (sibling.contains(Text.literal("Backpack")))
                                    isBackpack = true;

                        if (!isBackpack)
                            mc.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.QUICK_MOVE, p);
                    }
                })
                .dimensions(x, y, btnWidth, btnHeight)
                .build()
        );
    }

    /**
     * Reimplementation from GenericContainerScreen
     * @param context n/a
     * @param delta n/a
     * @param mouseX n/a
     * @param mouseY n/a
     */
    @Override
    public void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        Identifier tex = TEXTURE;

        Pattern pattern = Pattern.compile("(.*) \\(\\d/\\d\\)");
        Matcher matcher = pattern.matcher(getTitle().getString());

        MMOVendor vendor = null;

        // This is a vendor, show vendor specific ui texture
        if(BlockgameEnhanced.DEBUG) {
            if(matcher.matches()) {
                String vendorName = matcher.group(1).trim();
                vendor = MMOVendor.getByName(vendorName);

                if(vendor != null) {
                    tex = new Identifier("blockgame", String.format("textures/gui/container/%s.png", vendor.getUi()));
                    backgroundWidth = 256;
                    titleX = -32;
                }
            }
            else if(getTitle().getString().equals("Auction House")) {
                tex = new Identifier("blockgame", "textures/gui/container/auction_house.png");
                backgroundWidth = 256;
                titleX = 8;
            }
        }

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, tex);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(tex, i, j, 0, 0, this.backgroundWidth, this.rows * 18 + 17);
        context.drawTexture(tex, i, j + this.rows * 18 + 17, 0, 126, this.backgroundWidth, 96);

        if(vendor != null) {
            //InventoryScreen.drawEntity(context, x + 192, y + 201, 26, (float)(this.x + 192) - mouseX, (float)(this.y + 203 - 50) - mouseY, client.player);

            PlayerEntity vendorEntity = vendor.getVendorEntity();
            if(vendorEntity != null) {
                //InventoryScreen.drawEntity(context, x - 16, y + 115, 26, (float)(this.x - 16) - mouseX, (float)(this.y + 115 - 50) - mouseY, vendorEntity);
            }
        }
    }
}
