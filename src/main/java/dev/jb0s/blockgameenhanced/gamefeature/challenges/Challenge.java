package dev.jb0s.blockgameenhanced.gamefeature.challenges;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public abstract class Challenge {

    /**
     * Actions to perform during the 5-second countdown.
     */
    public void preStart() {
    }

    /**
     * Actions to perform when the 5-second countdown finishes.
     */
    public void start() {
    }

    /**
     * Actions to perform when the player has finished the challenge.
     * This function has to be called manually.
     */
    public void finish() {
    }

    /**
     * Shortcut to send a command to the server on behalf of the player.
     * @param command Command to execute
     */
    protected void runCommand(String command) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity cpe = mc.player;
        if(cpe == null) return;

        cpe.sendChatMessage("/" + command);
    }
}
