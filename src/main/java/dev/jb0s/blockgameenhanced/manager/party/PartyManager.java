package dev.jb0s.blockgameenhanced.manager.party;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.event.chat.ReceiveChatMessageEvent;
import dev.jb0s.blockgameenhanced.event.chat.SendChatMessageEvent;
import dev.jb0s.blockgameenhanced.event.entity.otherplayer.OtherPlayerTickEvent;
import dev.jb0s.blockgameenhanced.event.party.PartyUpdatedEvent;
import dev.jb0s.blockgameenhanced.helper.TimeHelper;
import dev.jb0s.blockgameenhanced.manager.Manager;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PartyManager extends Manager {
    private static final int PARTY_UPDATE_INTERVAL = 200; // 200 ticks (10 seconds)
    private static final String PARTY_LIST_SCREEN_NAME = "Party";
    private static final String PARTY_CREATION_SCREEN_NAME = "Party Creation";
    private static final SoundEvent PARTY_MEMBER_DEATH_SOUND = new SoundEvent(new Identifier("blockgame", "mus.gui.combat.death"));

    // todo this is kind of hacky I don't like it. replace with a better system soon?
    private static final String[] TRIGGER_PHRASES = new String[] {
        "You sent a party invite to #",
        "# joined your party!",
        "You successfully joined #",
        "You were transfered the party ownership."
    };

    private static MinecraftClient client;

    @Getter
    private ArrayList<PartyMember> partyMembers;

    // Party Updates
    private boolean allowedToQueryServer;
    private int ticksSinceLastUpdate;
    private int currentPayloadSyncId = -1;
    private boolean isWaitingForPartyScreenOpen;
    private boolean isWaitingForPartyScreenContent;

    @Override
    public void init() {
        client = MinecraftClient.getInstance();

        // Subscribe to events
        ReceiveChatMessageEvent.EVENT.register(((client1, message) -> handleChatMessage(message)));
        SendChatMessageEvent.EVENT.register(((client1, message) -> handleSentChatMessage(message)));
        OtherPlayerTickEvent.EVENT.register(((client1, otherPlayer) -> handlePlayerHealth(otherPlayer.getGameProfile().getName(), (int)otherPlayer.getHealth(), (int)otherPlayer.getMaxHealth(), otherPlayer.isAlive())));
    }

    @Override
    public void tick(MinecraftClient client) {
        // If we've gotten disconnected or are not allowed to query the server,
        // we should reset our state completely, immediately.
        if(client.world == null || !isAllowedToQueryServer()) {
            if(partyMembers != null) {
                // We need to invoke this event to let all other managers know the party disbanded due to a disconnect
                PartyUpdatedEvent.EVENT.invoker().partyUpdated(0);
            }

            ticksSinceLastUpdate = 0;
            isWaitingForPartyScreenOpen = false;
            isWaitingForPartyScreenContent = false;
            currentPayloadSyncId = 0;
            partyMembers = null;
            return;
        }

        // Request a party update every time we exceed the interval,
        // unless the player currently has a screen up. This is to prevent desync.
        boolean preventUpdates = client.currentScreen != null;
        if(!isWaitingForPartyScreenOpen && !isWaitingForPartyScreenContent) {
            ticksSinceLastUpdate++;

            if(ticksSinceLastUpdate >= PARTY_UPDATE_INTERVAL && !preventUpdates) {
                ticksSinceLastUpdate = 0;
                requestPartyPayload();
            }
        }
    }

    @Override
    public List<String> getDebugStats() {
        ArrayList<String> lines = new ArrayList<>();
        if(client == null || client.world == null) {
            return lines;
        }

        lines.add("Is Allowed To Query: " + allowedToQueryServer);
        lines.add("Ticks Since Last Update: " + ticksSinceLastUpdate);
        lines.add("Is Waiting For Party Screen Open: " + isWaitingForPartyScreenOpen);
        lines.add("Is Waiting For Party Screen Content: " + isWaitingForPartyScreenContent);
        lines.add("Current Payload Sync ID: " + currentPayloadSyncId);
        lines.add("World Time No Modulo: " + client.world.getTimeOfDay());
        if(partyMembers != null) {
            lines.add("Party Members:");
            for (PartyMember pm : partyMembers) {
                PlayerListEntry p = pm.getPlayer();
                if(p == null) {
                    lines.add("- Invalid player" + String.format(" (%d until removal)", PARTY_UPDATE_INTERVAL - ticksSinceLastUpdate));
                    continue;
                }
                lines.add("- " + p.getProfile().getName() + String.format(" (%d/%d HP)", pm.getHealth(), pm.getMaxHealth()));
            }
        }
        else {
            lines.add("Not in a party");
        }
        return lines;
    }

    /**
     * Handles an incoming packet telling us to open a screen.
     * @param packet The incoming packet data.
     * @return Whether this packet had info that the party manager needed.
     */
    public boolean handleScreenOpen(OpenScreenS2CPacket packet) {

        // If the screen we've just opened is anything but a generic grid that could display party info, we don't wanna mess with the screens. It breaks shit.
        if(packet.getScreenHandlerType() != ScreenHandlerType.GENERIC_9X3 &&
           packet.getScreenHandlerType() != ScreenHandlerType.GENERIC_9X6) {
            return false;
        }

        // This is a weird one. If a menu is opened and an inventory packet for
        // a different menu comes through, it causes a shit ton of desync. The
        // only way to mitigate this without errors is to prevent any menus from
        // opening while we are waiting for the party inventory content packet.
        if(isWaitingForPartyScreenContent) {
            return true;
        }

        // If we're not waiting for a screen to open, we don't care. It's the user opening a menu.
        if(!isWaitingForPartyScreenOpen) {
            return false;
        }

        int syncId = packet.getSyncId();
        String name = packet.getName().asString();
        boolean isPartyCreationScreen = name.startsWith(PARTY_CREATION_SCREEN_NAME);
        boolean isPartyMembersScreen = name.startsWith(PARTY_LIST_SCREEN_NAME);

        // Disregard this packet if it's unrelated to the party manager
        if(!isPartyCreationScreen && !isPartyMembersScreen) {
            return false;
        }

        // Update values so we can wait for the inventory content packet
        currentPayloadSyncId = syncId;
        isWaitingForPartyScreenOpen = false;
        isWaitingForPartyScreenContent = true;
        return true;
    }

    /**
     * Handles an incoming packet telling us to update an inventory's content.
     * @param packet The incoming packet data.
     * @return Whether this packet had info that the party manager needed.
     */
    public boolean handleInventoryUpdate(InventoryS2CPacket packet) {
        // If we weren't waiting for an inventory content packet, this packet is worthless to us.
        if(!isWaitingForPartyScreenContent) {
            return false;
        }

        // If this inventory update is not for our party window, disregard it.
        if(currentPayloadSyncId != packet.getSyncId()) {
            return false;
        }

        // This packet is useful, start scanning for all player heads in the menu.
        // NOTE: We only search up until slot 27 because a player head will never go past that slot.
        ArrayList<PlayerListEntry> foundPlayers = new ArrayList<>();
        for(int i = 0; i < 38; i++) {
            ItemStack stack = packet.getContents().get(i);
            if(stack == null) {
                continue;
            }

            // If the item in this slot is a skull, see if we can find a player that owns it.
            if(stack.getItem() instanceof SkullItem) {
                String name = stack.getName().getString();
                PlayerListEntry playerEntry = client.getNetworkHandler().getPlayerListEntry(name);

                // If there was no player found for this skull's owner, nothing we can do.
                if(playerEntry == null) {
                    continue;
                }

                // We found a player associated with this skull, save the player.
                foundPlayers.add(playerEntry);
            }
        }

        // Iterate through the list of players we found from the skulls
        // and begin initialization routines for new party members if any.
        for (PlayerListEntry ple : foundPlayers) {
            if(!isPlayerInParty(ple)) {
                handlePlayerJoinedParty(ple);
            }
        }

        // Now that we have handled new players, we need to check if there's any players that left. This is done in 2 ways:
        // - We check if there's a party member in our list that is not logged onto the server anymore.
        // - We check if there's a party member in our list that is not in the foundPlayers list anymore.
        if(partyMembers != null) {
            Collection<PlayerListEntry> onlinePlayers = client.getNetworkHandler().getPlayerList();

            for(int i = 0; i < partyMembers.size(); i++) {
                PartyMember member = partyMembers.get(i);

                // Check if this party member has either left the game or was not found in the party member list.
                boolean hasLeftGame = member.getPlayer() == null || !onlinePlayers.contains(member.getPlayer());
                boolean hasLeftParty = !foundPlayers.contains(member.getPlayer());

                // If one of the two checks returned true, we should kick them out.
                if(hasLeftGame || hasLeftParty) {
                    handlePlayerExitedParty(member);
                    i--; // Shift index back to account for the removal just now.
                }
            }
        }

        // Disallow any further requests if we got no party members
        if(partyMembers == null) {
            allowedToQueryServer = false;
        }

        // Stop waiting for packets, this packet had all the info we needed.
        isWaitingForPartyScreenContent = false;
        return true;
    }

    /**
     * Routine that handles what needs to happen when we've detected a new player in our party.
     * @param player The PlayerListEntry of the player that has joined.
     */
    private void handlePlayerJoinedParty(PlayerListEntry player) {
        if(partyMembers == null) {
            partyMembers = new ArrayList<>();
        }

        // Add to party
        partyMembers.add(new PartyMember(player));

        // Toast notification saying that player joined
        boolean playerIsMe = player.getProfile().getName().equals(client.getSession().getProfile().getName());
        if(!playerIsMe) {
            Text toastTitle = new TranslatableText("hud.blockgame.toast.party.joined.title");
            Text toastDescription = new TranslatableText("hud.blockgame.toast.party.joined.description", player.getProfile().getName());
            client.getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, toastTitle, toastDescription));
        }

        // Invoke party updated event
        PartyUpdatedEvent.EVENT.invoker().partyUpdated(partyMembers.size());
    }

    /**
     * Routine that handles what needs to happen when we've detected a player has left the party.
     * @param member The PartyMember info of the player that has left.
     */
    private void handlePlayerExitedParty(PartyMember member) {
        if(partyMembers == null) {
            return;
        }

        if(partyMembers.size() == 1) {
            partyMembers = null;
            allowedToQueryServer = false;

            // Toast notification saying that party disbanded
            Text toastTitle = new TranslatableText("hud.blockgame.toast.party.disbanded.title");
            Text toastDescription = new TranslatableText("hud.blockgame.toast.party.disbanded.description");
            client.getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, toastTitle, toastDescription));

            // Invoke party updated event
            PartyUpdatedEvent.EVENT.invoker().partyUpdated(0);
        }
        else {
            partyMembers.remove(member);

            // Toast notification saying that player left
            Text toastTitle = new TranslatableText("hud.blockgame.toast.party.left.title");
            Text toastDescription = new TranslatableText("hud.blockgame.toast.party.left.description", member.getPlayerName());
            client.getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, toastTitle, toastDescription));

            // Invoke party updated event
            PartyUpdatedEvent.EVENT.invoker().partyUpdated(partyMembers.size());
        }
    }

    /**
     * Handle an incoming HP update from a player entity in the world.
     * Every ticking player entity sends this data to us, we only care about our party members though.
     * @param playerName The name of the player that is sending us their health data.
     * @param health The amount of health that they have. This is a vanilla value, 0 - 20.
     * @param maxHealth The maximum amount of health that they have. This is usually 20, but can be increased with max hp boost.
     * @param isAlive Whether the player entity is considered "alive" or not. This is more trustworthy than checking (health == 0).
     */
    public void handlePlayerHealth(String playerName, int health, int maxHealth, boolean isAlive) {
        if(partyMembers == null)
            return;

        // We don't care about this player's health if we're not in a party with them
        PartyMember member = getPartyMember(playerName);
        if(member == null)
            return;

        if(maxHealth > 50) {
            // Vanilla health in terms of Blockgame should never ever reach anything above 50 typically. That would be 25 hearts.
            // In case the server gives us an absurd value, we can just cap it down to 20 until it comes to its senses.
            maxHealth = 20;
        }

        // Update PartyMember stats
        member.setHealth(health);
        member.setMaxHealth(maxHealth);
        member.setLastUpdateSecond(TimeHelper.getSystemTimeUnix());

        // Death / respawn checks
        if(!isAlive && member.isAlive()) {
            member.setAlive(false);
            member.setHealth(0);

            // Play teammate death sound if allowed by user
            boolean shouldPlayDeathNotifySound = BlockgameEnhanced.getConfig().getPartyHudConfig().deathNotify;
            if(shouldPlayDeathNotifySound) {
                client.player.playSound(PARTY_MEMBER_DEATH_SOUND, SoundCategory.PLAYERS, .4f, 1f);
            }
        }
        else if (isAlive && !member.isAlive()) {
            member.setAlive(true);
        }
    }
    /**

     * Handle the player receiving a chat message.
     * This event is used to determine whether we should query the server for party info.
     * @param message The chat message that was sent.
     */
    public void handleChatMessage(String message) {
        for (String triggerStatement : TRIGGER_PHRASES) {
            boolean shouldCheckStart = triggerStatement.endsWith("#");
            String triggerString = triggerStatement.replace("#", "");

            // If the message we received contains an update trigger, update the ticksSinceLastUpdate to trigger a refresh ASAP.
            boolean messageContainsTrigger = (shouldCheckStart && message.startsWith(triggerString)) || (!shouldCheckStart && message.endsWith(triggerString));
            if(messageContainsTrigger) {
                allowedToQueryServer = true;
                ticksSinceLastUpdate = PARTY_UPDATE_INTERVAL;
            }
        }
    }

    public void handleSentChatMessage(String message) {
        // If the message that was sent starts with /party, and we're not allowed to query right now,
        // we want to allow querying at least once and start the query timer from 0. This gives the server
        // time to send us the party window. I know this is a race condition, however it is one that we likely won't lose.
        if(message.startsWith("/party") && !isAllowedToQueryServer()) {
            allowedToQueryServer = true;
            ticksSinceLastUpdate = 0;
        }
    }

    /**
     * Sends a /party command to the server and begins waiting for a response.
     */
    private void requestPartyPayload() {
        isWaitingForPartyScreenOpen = true;
        isWaitingForPartyScreenContent = false;
        client.player.sendChatMessage("/party");
    }

    /**
     * Get a player from our party member list by their username.
     * @param playerName The username of the player to search for.
     * @return The PartyMember info for the player in question, null if nothing was found.
     */
    private PartyMember getPartyMember(String playerName) {
        if(partyMembers == null)
            return null;

        for (PartyMember p : partyMembers) {
            if(p.getPlayerName().equals(playerName))
                return p;
        }

        return null;
    }

    /**
     * Get a player from our party member list by their PlayerListEntry.
     * @param player The PlayerListEntry of the player to search for.
     * @return The PartyMember info for the player in question, null if nothing was found.
     */
    private PartyMember getPartyMember(PlayerListEntry player) {
        return getPartyMember(player.getProfile().getName());
    }

    /**
     * Determines if a player is in our party or not.
     * @param playerName The username of the player to search for.
     * @return Whether a player going by this username exists in our party or not.
     */
    public boolean isPlayerInParty(String playerName) {
        return getPartyMember(playerName) != null;
    }

    /**
     * Determines if a player is in our party or not.
     * @param player The PlayerListEntry of the player to search for.
     * @return Whether a player going by this PlayerListEntry exists in our party or not.
     */
    public boolean isPlayerInParty(PlayerListEntry player) {
        return getPartyMember(player.getProfile().getName()) != null;
    }

    /**
     * Determines if a player is in our party or not.
     * @param player The player entity to perform the check on.
     * @return Whether the player that belongs to this player entity is in our party or not.
     */
    public boolean isPlayerInParty(AbstractClientPlayerEntity player) {
        return isPlayerInParty(player.getGameProfile().getName());
    }

    /**
     * Determines whether we are allowed to query the server.
     * We should be allowed to query the server always if we're in a party, if not we have no reason to.
     */
    private boolean isAllowedToQueryServer() {
        return partyMembers != null || allowedToQueryServer;
    }
}
