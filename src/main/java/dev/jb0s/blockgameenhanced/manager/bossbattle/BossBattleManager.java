package dev.jb0s.blockgameenhanced.manager.bossbattle;

import dev.jb0s.blockgameenhanced.event.bossbattle.BossBattleCommencedEvent;
import dev.jb0s.blockgameenhanced.event.bossbattle.BossBattleEndedEvent;
import dev.jb0s.blockgameenhanced.helper.BossBarHelper;
import dev.jb0s.blockgameenhanced.helper.MathHelper;
import dev.jb0s.blockgameenhanced.manager.Manager;
import dev.jb0s.blockgameenhanced.manager.adventure.AdventureZoneBoss;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;

import java.util.ArrayList;
import java.util.List;

public class BossBattleManager extends Manager {
    private ClientBossBar currentBattleBossBar;

    @Getter
    private BattleState currentBattleState;

    @Getter
    private AdventureZoneBoss currentBattle;

    @Override
    public void init() {
        currentBattleState = BattleState.NO_BATTLE;
    }

    @Override
    public void tick(MinecraftClient client) {
        switch (currentBattleState) {
            case PENDING_START -> tickPendingBattle(client);
            case IN_PROGRESS -> tickInProgress(client);
        }
    }

    /**
     * Tick function that specifically handles the battle pending start phase.
     */
    private void tickPendingBattle(MinecraftClient client) {
        // We shouldn't be waiting for a pending battle if we don't have any info on said battle
        if(currentBattle == null) {
            currentBattleState = BattleState.NO_BATTLE;
            return;
        }

        for (ClientBossBar bossBar : BossBarHelper.getBossBars().values()) {
            // Check boss bar name. If it matches with the boss we're waiting for, we're ready to begin the battle.

            if(bossBar.getName().getString().startsWith(currentBattle.getBoss())) {
                currentBattleBossBar = bossBar;
                beginBossBattle();
                return;
            }
        }
    }

    /**
     * Tick function only used while a battle is in progress.
     * @param client The Minecraft client.
     */
    private void tickInProgress(MinecraftClient client) {
        if(!BossBarHelper.isBossBarOnScreen(currentBattleBossBar) || currentBattleBossBar.getPercent() <= 0.01f) {
            endBossBattle();
        }
    }

    /**
     * Sets the current battle state to in progress.
     * If there is no current battle, this function does nothing.
     */
    private void beginBossBattle() {
        if(currentBattle == null) {
            return;
        }

        currentBattleState = BattleState.IN_PROGRESS;
        BossBattleCommencedEvent.EVENT.invoker().bossBattleCommenced(currentBattle);
    }

    /**
     * Ends the ongoing boss battle.
     */
    private void endBossBattle() {
        currentBattleState = BattleState.BATTLE_ENDED;
        BossBattleEndedEvent.EVENT.invoker().bossBattleEnded(currentBattle);
        currentBattle = null;
    }

    /**
     * Sets the current battle and sets battle state to pending start.
     * @param boss The boss the player is going to battle.
     */
    public void setCurrentBattle(AdventureZoneBoss boss) {
        currentBattle = boss;
        currentBattleState = boss != null ? BattleState.PENDING_START : BattleState.NO_BATTLE;
    }

    @Override
    public List<String> getDebugStats() {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("Current Battle: " + (currentBattle != null ? currentBattle.getBoss() : "null"));
        lines.add("Current Battle State: " + currentBattleState.toString());
        lines.add("Boss Bars:");
        for (ClientBossBar bossBar : BossBarHelper.getBossBars().values()) {
            lines.add("- " + bossBar.getName().getString() + " (" + bossBar.getPercent() + "%)");
        }
        return lines;
    }
}
