package dev.jb0s.blockgameenhanced.gamefeature.chatchannels;

import net.minecraft.text.Text;

public class ChatChannel {
    public final String name;
    public final String command;
    /**
     * This is basically for /party chat, if `false` we prefix all messages with the `command`
     */
    public final boolean canSwitch;
    public final Text formattedName;
    public boolean enabled = true;
    public boolean isTemporary = false;

    ChatChannel(String name, String command, boolean canSwitch, Text formattedName) {
        this.name = name;
        this.command = command;
        this.canSwitch = canSwitch;
        this.formattedName = formattedName;
    }

    ChatChannel(String name, String command, boolean canSwitch, boolean enabled, Text formattedName) {
        this(name, command, canSwitch, formattedName);
        this.enabled = enabled;
    }

    ChatChannel(String name, Text formattedName) {
        this(name, "", false, formattedName);
        this.isTemporary = true;
    }
}
