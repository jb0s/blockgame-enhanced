package dev.jb0s.blockgameenhanced.gamefeature.mmostats;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.chat.ReceiveChatMessageEvent;
import dev.jb0s.blockgameenhanced.event.gamefeature.mmostats.MMOStatsUpdatedEvent;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.ImmersiveIngameHud;
import dev.jb0s.blockgameenhanced.config.modules.IngameHudConfig;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public class MMOStatsGameFeature extends GameFeature {
    @Getter
    private int health;

    @Getter
    private int maxHealth;

    @Getter
    private int hunger;

    @Getter
    private float hydration;

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);

        ReceiveChatMessageEvent.EVENT.register(this::extractStatsFromMessage);
        ReceiveChatMessageEvent.EVENT.register(this::extractExpDataFromMessage);
    }

    private ActionResult extractStatsFromMessage(MinecraftClient minecraftClient, String message) {
        if(!(minecraftClient.inGameHud instanceof ImmersiveIngameHud)) {
            return ActionResult.PASS;
        }

        String[] split = message.split("\\|");
        if(split.length != 3) {
            return ActionResult.PASS;
        }

        String health = split[0].trim();
        String hunger = split[1].trim();
        String hydrate = split[2].trim();

        if(health.startsWith("❤") && hunger.startsWith("\uD83C\uDF56") && hydrate.startsWith("\uD83E\uDDEA")) {
            String[] hpSet = health.substring(2).split("/");
            String[] huSet = hunger.substring(3).split("/");
            String[] hySet = hydrate.substring(3).split("/");

            this.health = Integer.parseInt(hpSet[0]);
            this.hunger = Integer.parseInt(huSet[0]);
            maxHealth = Integer.parseInt(hpSet[1].trim());
            hydration = Float.parseFloat(hySet[0]);

            // Invoke event
            MMOStatsUpdatedEvent.EVENT.invoker().mmoStatsUpdated(this);

            // Surpress actionbar message or not depending on the circumstance
            boolean shouldSuppress = BlockgameEnhanced.getConfig().getIngameHudConfig().enableCustomHud && !BlockgameEnhanced.getConfig().getIngameHudConfig().showAdvancedStats;
            return shouldSuppress ? ActionResult.SUCCESS : ActionResult.PASS;
        }

        return ActionResult.PASS;
    }

    private ActionResult extractExpDataFromMessage(MinecraftClient minecraftClient, String message) {
        IngameHudConfig ighConfig = BlockgameEnhanced.getConfig().getIngameHudConfig();

        boolean isDisabled = !ighConfig.enableCustomHud || ighConfig.showProfessionExpInChat;
        if(!message.startsWith("[EXP]") || isDisabled) {
            return ActionResult.PASS;
        }

        if(minecraftClient.inGameHud instanceof ImmersiveIngameHud immersiveIngameHud) {
            String data = message.substring(6);
            String[] split = data.split(" - ");
            String[] professionInfo = split[0].split(" ");

            MMOProfession prof = MMOProfession.valueOf(professionInfo[0].trim().toUpperCase());
            float gained = Float.parseFloat(professionInfo[1].substring(1));

            immersiveIngameHud.getImmersiveExpPopupContainer().showExpPopup(prof, Float.parseFloat(split[1].replace("%", "")), gained);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
