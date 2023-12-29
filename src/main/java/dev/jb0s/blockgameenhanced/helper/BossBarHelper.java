package dev.jb0s.blockgameenhanced.helper;

import lombok.Getter;
import lombok.Setter;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;

import java.util.Map;
import java.util.UUID;

public class BossBarHelper {
    @Getter
    @Setter
    private static Map<UUID, ClientBossBar> bossBars = Maps.newLinkedHashMap();

    public static boolean isBossBarOnScreen(BossBar bossBar) {
        for (BossBar bar : bossBars.values()) {
            if(bar == bossBar)
                return true;
        }

        return false;
    }
}
