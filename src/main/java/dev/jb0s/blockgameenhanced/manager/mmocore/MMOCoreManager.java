package dev.jb0s.blockgameenhanced.manager.mmocore;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.event.chat.ReceiveChatMessageEvent;
import dev.jb0s.blockgameenhanced.event.chat.ReceiveFormattedChatMessageEvent;
import dev.jb0s.blockgameenhanced.gui.hud.immersive.ImmersiveIngameHud;
import dev.jb0s.blockgameenhanced.manager.Manager;
import dev.jb0s.blockgameenhanced.manager.config.modules.ChatConfig;
import dev.jb0s.blockgameenhanced.manager.config.modules.IngameHudConfig;
import dev.jb0s.blockgameenhanced.manager.mmocore.profession.MMOProfession;
import lombok.Getter;
import net.minecraft.block.NoteBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

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
        ReceiveFormattedChatMessageEvent.EVENT.register(this::extractMentionedPlayersFromMessage);
    }

    private ActionResult extractMentionedPlayersFromMessage(MinecraftClient minecraftClient, Text message) {
        if (!(minecraftClient.inGameHud instanceof ImmersiveIngameHud)) {
            return ActionResult.PASS;
        }

        ChatConfig hudConfig = BlockgameEnhanced.getConfig().getChatConfig();
        if (!hudConfig.enableMentions) {
            return ActionResult.PASS;
        }

        int ColonIndex = message.getString().indexOf(":");
        if (ColonIndex == -1) {
            return ActionResult.PASS;
        }

        String playerName = minecraftClient.getSession().getUsername();
        List<String> aliasList = Arrays.asList(hudConfig.mentionAliases.toLowerCase().split(","));
        String currentPlayerName = minecraftClient.player.getName().getString();

        MutableText processedMessage = new LiteralText("");
        boolean mentionFound = false;

        boolean isMessage = false;

        for (Text sibling : message.getSiblings()) {
            if (sibling instanceof LiteralText) {
                LiteralText literalText = (LiteralText) sibling;
                Style originalStyle = sibling.getStyle();
                String[] words = literalText.getString().split(" ", -1);

                for (int i = 0; i < words.length; i++) {
                    String word = words[i];

                    if (word.contains(":")) {
                        isMessage = true;
                    }

                    MutableText wordText = new LiteralText(word).setStyle(originalStyle);

                    if(isMessage) {
                        // Check if the word is a mention, but not the current player's name
                        if (word.equalsIgnoreCase(playerName) || aliasList.contains(word.toLowerCase())) {
                            wordText = wordText.formatted(Formatting.YELLOW, Formatting.ITALIC);
                            mentionFound = true;
                        }
                    }

                    processedMessage.append(wordText);
                    if (i < words.length - 1) {
                        processedMessage.append(" ");
                    }
                }
            } else {
                processedMessage.append(sibling.copy());
            }
        }

        if (mentionFound) {
            minecraftClient.inGameHud.getChatHud().addMessage(processedMessage);

            if (hudConfig.enableMentionSound && minecraftClient.world != null && minecraftClient.player != null) {
                playMentionSound(minecraftClient);
            }
        }

        return mentionFound ? ActionResult.CONSUME : ActionResult.PASS;
    }

    private void playMentionSound(MinecraftClient minecraftClient) {
        BlockPos playerPos = minecraftClient.player.getBlockPos();
        PositionedSoundInstance soundInstance = new PositionedSoundInstance(
                SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.PLAYERS, 1.0F, 1.0F, playerPos
        );
        minecraftClient.getSoundManager().play(soundInstance);
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
