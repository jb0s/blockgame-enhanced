package dev.jb0s.blockgameenhanced.config.modules;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "jukebox")
public class JukeboxConfig implements ConfigData {
  @ConfigEntry.Gui.Tooltip
  public boolean enableJukebox;

  @ConfigEntry.Gui.Tooltip
  public boolean vanillaInWilderness;

  public JukeboxConfig() {
    enableJukebox = true;
    vanillaInWilderness = true;
  }
}
