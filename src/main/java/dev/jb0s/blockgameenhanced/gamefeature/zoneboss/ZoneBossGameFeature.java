package dev.jb0s.blockgameenhanced.gamefeature.zoneboss;

import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.adventurezone.PlayerEnteredZoneEvent;
import dev.jb0s.blockgameenhanced.event.adventurezone.PlayerExitedZoneEvent;
import dev.jb0s.blockgameenhanced.event.bossbattle.BossBattleCommencedEvent;
import dev.jb0s.blockgameenhanced.event.bossbattle.BossBattleEndedEvent;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.zone.Zone;
import dev.jb0s.blockgameenhanced.gamefeature.zone.ZoneBoss;
import dev.jb0s.blockgameenhanced.helper.BossBarHelper;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.network.ClientPlayerEntity;

public class ZoneBossGameFeature extends GameFeature {
    private ClientBossBar currentBattleBossBar;

    @Getter
    private ZoneBossBattleState currentBattleState;

    @Getter
    private ZoneBoss currentBattle;

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);
        currentBattleState = ZoneBossBattleState.NO_BATTLE;

        PlayerEnteredZoneEvent.EVENT.register(this::onPlayerEnteredZone);
        PlayerExitedZoneEvent.EVENT.register(this::onPlayerExitedZone);
    }

    private void onPlayerExitedZone(MinecraftClient client, ClientPlayerEntity clientPlayerEntity, Zone zone) {
        if(zone.getBattle() != null) {
            setCurrentBattle(null);
        }
    }

    private void onPlayerEnteredZone(MinecraftClient client, ClientPlayerEntity clientPlayerEntity, Zone zone) {
        if(zone.getBattle() != null) {
            setCurrentBattle(zone.getBattle());
        }
    }

    @Override
    public void tick() {
        switch (currentBattleState) {
            case PENDING_START -> tickPendingBattle();
            case IN_PROGRESS -> tickInProgress();
        }
    }

    /**
     * Tick function that specifically handles the battle pending start phase.
     */
    private void tickPendingBattle() {
        // We shouldn't be waiting for a pending battle if we don't have any info on said battle
        if(currentBattle == null) {
            currentBattleState = ZoneBossBattleState.NO_BATTLE;
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
     */
    private void tickInProgress() {
        if(!BossBarHelper.isBossBarOnScreen(currentBattleBossBar) /*|| currentBattleBossBar.getPercent() <= 0.01f*/) {
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

        currentBattleState = ZoneBossBattleState.IN_PROGRESS;
        BossBattleCommencedEvent.EVENT.invoker().bossBattleCommenced(currentBattle);
    }

    /**
     * Ends the ongoing boss battle.
     */
    private void endBossBattle() {
        currentBattleState = ZoneBossBattleState.BATTLE_ENDED;
        BossBattleEndedEvent.EVENT.invoker().bossBattleEnded(currentBattle, currentBattleState);
        currentBattle = null;
    }

    /**
     * Sets the current battle and sets battle state to pending start.
     * @param boss The boss the player is going to battle.
     */
    public void setCurrentBattle(ZoneBoss boss) {
        currentBattle = boss;
        currentBattleState = boss != null ? ZoneBossBattleState.PENDING_START : ZoneBossBattleState.NO_BATTLE;
    }
}
