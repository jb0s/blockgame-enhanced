package dev.jb0s.blockgameenhanced.manager.discordrpc;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.event.discordrpc.DiscordReadyEvent;
import dev.jb0s.blockgameenhanced.event.party.PartyUpdatedEvent;
import dev.jb0s.blockgameenhanced.manager.Manager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.util.UUID;

public class DiscordRichPresenceManager extends Manager {
    private static final IPCClient client = new IPCClient(1099682079723237386L);
    private static final RichPresence.Builder builder = new RichPresence.Builder();

    private static final String DETAILS_MENU = "In the menus";
    private static final String DETAILS_PLAYING = "In-game";
    private static final String STATE_PARTY = "In a party";
    private static final int MAX_PARTY_SIZE = 8;

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
        try {
            client.connect(DiscordBuild.ANY);
        }
        catch (Exception e) {
            // cry
            BlockgameEnhanced.LOGGER.info("Failed to connect to Rich Presence! ({})", e.getMessage());
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
            String string = inGame ? DETAILS_PLAYING : DETAILS_MENU;

            builder.setDetails(string);
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
            if(size == 0) {
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
}
