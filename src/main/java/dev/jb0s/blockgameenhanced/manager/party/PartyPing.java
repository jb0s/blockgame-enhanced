package dev.jb0s.blockgameenhanced.manager.party;

import dev.jb0s.blockgameenhanced.helper.DebugHelper;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vector4f;

public class PartyPing {
    @Getter
    private PartyMember partyMember;

    @Getter
    @Setter
    private Vec3d location;

    @Getter
    @Setter
    private String world;

    @Getter
    @Setter
    private Vector4f screenSpacePos;

    public PartyPing(PartyMember partyMember, Vec3d location, String world) {
        this.partyMember = partyMember;
        setLocation(location);
        setWorld(world);
    }

    public boolean isHovered() {
        MinecraftClient minecraft = MinecraftClient.getInstance();

        // You should only be able to hover-to-delete your own pings
        if(!getPartyMember().getPlayerName().equals(minecraft.getSession().getUsername())) {
            return false;
        }

        // Fix crash bug somehow
        if(getScreenSpacePos() == null) {
            return false;
        }

        Window window = minecraft.getWindow();
        float cx = window.getWidth() / 2.f;
        float cy = window.getHeight() / 2.f;
        float dist = Math.abs(getScreenSpacePos().getX() - cx) + Math.abs(getScreenSpacePos().getY() - cy);
        return dist < 27;
    }
}
