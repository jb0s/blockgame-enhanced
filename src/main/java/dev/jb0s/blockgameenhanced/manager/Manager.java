package dev.jb0s.blockgameenhanced.manager;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public abstract class Manager {
    public Manager() {
        bind();
    }

    /**
     * Called when the manager is ready for shit to go down.
     * (aka after the constructor is finished doing its thing.)
     */
    public void init() {
    }

    /**
     * Called at the end of every client tick.
     * @param client The MinecraftClient that ticked us.
     */
    public void tick(MinecraftClient client) {
    }

    /**
     * Used to display useful debugging information when the F4 debug menu is up.
     * @implNote Make sure to separate with newlines.
     */
    public List<String> getDebugStats() {
        return null;
    }

    /**
     * Creates an instance of this manager and binds its events to the game.
     * This is called in the constructor and should not be called manually.
     */
    private void bind() {
        BlockgameEnhancedClient.getAllManagers().add(this);

        // Invoke custom init routine after we're done with the essentials
        init();
    }
}
