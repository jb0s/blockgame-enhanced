package dev.jb0s.blockgameenhanced.manager.discordrpc;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.event.discordrpc.DiscordReadyEvent;
import dev.jb0s.blockgameenhanced.event.party.PartyUpdatedEvent;
import dev.jb0s.blockgameenhanced.manager.Manager;
import dev.jb0s.blockgameenhanced.manager.config.modules.ModConfig;
import dev.jb0s.blockgameenhanced.manager.config.structure.DiscordRPCPrivacy;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;

import java.util.UUID;

public class DiscordRichPresenceManager extends Manager {
    private static final IPCClient client = new IPCClient(1099682079723237386L);
    private static final RichPresence.Builder builder = new RichPresence.Builder();

    private static final int SETTING_REFRESH_STEP = 20;
    private static final String DETAILS_MENU = "In the menus";
    private static final String DETAILS_PLAYING = "In-game";
    private static final String STATE_PARTY = "In a party";
    private static final int MAX_PARTY_SIZE = 8;

    private int timer;

    // State cache to persist through privacy settings
    private boolean connected;
    private boolean isInGame;
    private int partySize;
    private DiscordRPCPrivacy lastPrivacyLevel;

    @Override
    public void init() {
        client.setListener(new BlockgameIPCListener());

        // Listen for ready event
        DiscordReadyEvent.EVENT.register((a) -> handleReady());

        // Update rich presence upon join / disconnect / party update
        ClientPlayConnectionEvents.JOIN.register((a, b, c) -> setInGame(true));
        ClientPlayConnectionEvents.DISCONNECT.register((a, b) -> setInGame(false));
        PartyUpdatedEvent.EVENT.register(this::setPartySize);

        // Connect
        safeConnect();
    }

    @Override
    public void tick(MinecraftClient client) {
        timer++;

        DiscordRPCPrivacy rpcPrivacyLevel = BlockgameEnhanced.getConfig().getPrivacyConfig().getDiscordRPCPrivacy();
        if(timer >= SETTING_REFRESH_STEP) {
            // If the RPC Privacy level has been adjusted since last refresh, send a refresh to Discord respecting the new settings.
            if(lastPrivacyLevel != rpcPrivacyLevel) {
                refreshPresence();
            }

            lastPrivacyLevel = rpcPrivacyLevel;
            timer = 0;
        }
    }

    private void handleReady() {
        builder.setDetails(DETAILS_MENU)
                .setLargeImage("logo", "Blockgame Enhanced Mod");

        client.sendRichPresence(builder.build());
    }

    /**
     * Makes the rich presence switch between "In the menus" and "In-game".
     * @param inGame Should the rich presence say "In-game" or "In the menus"?
     */
    public void setInGame(boolean inGame) {
        try {
            isInGame = inGame;

            String string = inGame ? DETAILS_PLAYING : DETAILS_MENU;
            boolean privacyAllowsForDetails = BlockgameEnhanced.getConfig().getPrivacyConfig().getDiscordRPCPrivacy().getValue() > 1;

            builder.setDetails(privacyAllowsForDetails ? string : "");
            client.sendRichPresence(builder.build());
        }
        catch (Exception e) {
            // cry about it
            BlockgameEnhanced.LOGGER.info("Failed to send Rich Presence update! ({})", e.getMessage());
        }
    }

    /**
     * Sets the amount of players in a party on the rich presence. Hidden if size is 0.
     * @param size The amount of members in the party.
     */
    public void setPartySize(int size) {
        try {
            partySize = size;

            boolean privacyAllowsForPartyInfo = BlockgameEnhanced.getConfig().getPrivacyConfig().getDiscordRPCPrivacy().getValue() > 2;
            if(size == 0 || !privacyAllowsForPartyInfo) {
                builder.setState("");
                builder.setParty(null, 0, 0, 0);
                client.sendRichPresence(builder.build());
                return;
            }

            builder.setState(STATE_PARTY);
            builder.setParty(UUID.randomUUID().toString(), size, MAX_PARTY_SIZE, 0);
            client.sendRichPresence(builder.build());
        }
        catch(Exception e) {
            // cry about it
            BlockgameEnhanced.LOGGER.info("Failed to send Rich Presence update! ({})", e.getMessage());
        }
    }

    /**
     * Re-sends every piece of information to the rich presence.
     */
    public void refreshPresence() {
        boolean privacySettingsAllowConnect = BlockgameEnhanced.getConfig().getPrivacyConfig().getDiscordRPCPrivacy().getValue() > 0;
        if(privacySettingsAllowConnect && !connected) {
            safeConnect();
        }
        else if(!privacySettingsAllowConnect && connected) {
            safeDisconnect();
        }

        setInGame(isInGame);
        setPartySize(partySize);
    }

    /**
     * Simple method that connects to Discord and catches exceptions.
     */
    private void safeConnect() {
        try {
            boolean privacySettingsAllowConnect = BlockgameEnhanced.getConfig().getPrivacyConfig().getDiscordRPCPrivacy().getValue() > 0;
            if(privacySettingsAllowConnect) {
                client.connect(DiscordBuild.ANY);
                connected = true;
            }
        }
        catch (Exception e) {
            // cry
            BlockgameEnhanced.LOGGER.info("Failed to connect to Rich Presence! ({})", e.getMessage());
        }
    }

    /**
     * Simple method that disconnects from Discord and catches exceptions.
     */
    private void safeDisconnect() {
        try {
            if(connected) {
                client.close();
                connected = false;
            }
        }
        catch (Exception e) {
            // cry
        }
    }
}
