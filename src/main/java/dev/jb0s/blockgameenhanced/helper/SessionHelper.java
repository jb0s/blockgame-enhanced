package dev.jb0s.blockgameenhanced.helper;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

import java.util.UUID;

public class SessionHelper {
    private static final YggdrasilAuthenticationService authService = new YggdrasilAuthenticationService(MinecraftClient.getInstance().getNetworkProxy(), UUID.randomUUID().toString());
    private static final YggdrasilUserAuthentication userAuth = (YggdrasilUserAuthentication) authService.createUserAuthentication(Agent.MINECRAFT);
    private static final YggdrasilMinecraftSessionService minecraftSessionService = (YggdrasilMinecraftSessionService) authService.createMinecraftSessionService();

    public static GameProfile getGameProfileFromSession(Session session) {
        String serverId = UUID.randomUUID().toString();
        try {
            minecraftSessionService.joinServer(session.getProfile(), session.getAccessToken(), serverId);
            GameProfile profile = minecraftSessionService.hasJoinedServer(session.getProfile(), serverId, null);
            if (profile.isComplete()) {
                return profile;
            }
        } catch (AuthenticationException e) {
            return null;
        }
        return null;
    }
}
