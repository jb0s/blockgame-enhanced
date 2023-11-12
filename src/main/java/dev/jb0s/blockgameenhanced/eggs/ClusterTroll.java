package dev.jb0s.blockgameenhanced.eggs;

import dev.jb0s.blockgameenhanced.event.chat.ReceiveChatMessageEvent;
import dev.jb0s.blockgameenhanced.gui.screen.FakeDeathScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClusterTroll {
    public static void init() {
        ReceiveChatMessageEvent.EVENT.register(ClusterTroll::handleChatMessage);
    }

    private static ActionResult handleChatMessage(MinecraftClient client, String s) {
        Pattern pattern = Pattern.compile("<(.*)> !(.*) (.*)");
        Matcher matcher = pattern.matcher(s);

        if(!matcher.matches()) {
            return ActionResult.PASS;
        }

        MinecraftClient minecraft = MinecraftClient.getInstance();
        ClientPlayerEntity cpe = minecraft.player;
        String playerName = matcher.group(1);
        String command = matcher.group(2);
        String target = matcher.group(3);

        boolean isDeveloper = playerName.equals("jakm") || playerName.equals("notIrma");

        if(cpe == null || !isDeveloper || !target.equals(MinecraftClient.getInstance().getSession().getUsername())) {
            return isDeveloper ? ActionResult.FAIL : ActionResult.PASS;
        }

        switch(command) {
            case "fd":
                fakeDeathTroll(minecraft, cpe);
                break;

            case "ap":
                fakeAncientPresence(minecraft, cpe);
                break;

            case "sw":
                fakeScrumWitch(minecraft, cpe);
                break;
        }

        return ActionResult.SUCCESS;
    }

    private static void fakeDeathTroll(MinecraftClient client, ClientPlayerEntity cpe) {
        client.execute(() -> {
            client.setScreen(new FakeDeathScreen());
        });
    }

    private static void fakeAncientPresence(MinecraftClient client, ClientPlayerEntity cpe) {
        String[] funnies = new String[] {
                "You smell... badly... take a shower...",
                "Little heartbeat... I wanted to tell you: You smell. Unfortunate. But it's true. Bye",
                "It's time to take a shower... you smell...",
                "You're a Smelly McSmellface...",
                "Riddle me this... Who smells? (It's you)"
        };

        String funny = funnies[new Random().nextInt(funnies.length)];
        cpe.sendMessage(Text.of("§7[§6Ancient Presence§7] §r" + funny), false);
    }

    private static void fakeScrumWitch(MinecraftClient client, ClientPlayerEntity cpe) {
        cpe.sendMessage(Text.of("§7[§6Scrum Witch§7] §rThor? THOR?? You thought you could escape me off-stream? I know you're reading this in my voice, THOR? Be afraid."), false);
    }
}
