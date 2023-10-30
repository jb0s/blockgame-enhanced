package dev.jb0s.blockgameenhanced.manager.mmocore;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.event.chat.ReceiveChatMessageEvent;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.ImmersiveIngameHud;
import dev.jb0s.blockgameenhanced.manager.Manager;
import dev.jb0s.blockgameenhanced.manager.mmocore.profession.MMOProfession;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class MMOCoreManager extends Manager {
    @Getter
    private int health;

    @Getter
    private int maxHealth;

    @Getter
    private int hunger;

    @Getter
    private float hydration;

    @Override
    public void init() {
        ReceiveChatMessageEvent.EVENT.register(this::extractStatsFromMessage);
        ReceiveChatMessageEvent.EVENT.register(this::extractExpDataFromMessage);
    }

    @Override
    public List<String> getDebugStats() {
        ArrayList<String> lines = new ArrayList<>();

        lines.add("HP: " + health + "/" + maxHealth);
        lines.add("Hunger: " + hunger + "/20");
        lines.add("Hydration: " + hydration + "/20");

        return lines;
    }

    private ActionResult extractStatsFromMessage(MinecraftClient minecraftClient, String message) {
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
            return BlockgameEnhanced.getConfig().getIngameHudConfig().enableCustomHud ? ActionResult.SUCCESS : ActionResult.PASS;
        }

        return ActionResult.PASS;
    }

    private ActionResult extractExpDataFromMessage(MinecraftClient minecraftClient, String message) {
        if(!message.startsWith("[EXP]") || !BlockgameEnhanced.getConfig().getIngameHudConfig().enableCustomHud) {
            return ActionResult.PASS;
        }

        String data = message.substring(6);
        String[] split = data.split(" - ");
        String[] professionInfo = split[0].split(" ");

        if(minecraftClient.inGameHud instanceof ImmersiveIngameHud immersiveIngameHud) {
            if(professionInfo[0].trim().equals("Einherjar")) {
                return ActionResult.SUCCESS;
            }

            MMOProfession prof = MMOProfession.valueOf(professionInfo[0].trim().toUpperCase());
            immersiveIngameHud.getImmersiveExpPopupContainer().showExpPopup(prof, Float.parseFloat(split[1].replace("%", "")));
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
