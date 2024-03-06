package dev.jb0s.blockgameenhanced.config.modules;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.client.network.ServerInfo;

@Config(name = "server")
public class ServerConfig implements ConfigData {
  @ConfigEntry.Gui.Tooltip(count = 2)
  public boolean enableResourcePackPrompt;

  public ServerConfig() {
    enableResourcePackPrompt = true;
  }

  public static void updateServerInfo(ServerInfo serverInfo) {
    serverInfo.setResourcePackPolicy(BlockgameEnhanced.getConfig().getServerConfig().enableResourcePackPrompt
        ? ServerInfo.ResourcePackPolicy.PROMPT
        : ServerInfo.ResourcePackPolicy.ENABLED);
  }
}
