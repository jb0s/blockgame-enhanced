package dev.jb0s.blockgameenhanced.gamefeature.party;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.event.chat.ReceiveChatMessageEvent;
import dev.jb0s.blockgameenhanced.event.entity.otherplayer.OtherPlayerTickEvent;
import dev.jb0s.blockgameenhanced.event.entity.player.PlayerTickEvent;
import dev.jb0s.blockgameenhanced.event.gamefeature.hotkey.PingHotkeyPressedEvent;
import dev.jb0s.blockgameenhanced.event.gamefeature.party.PartyPingEvent;
import dev.jb0s.blockgameenhanced.event.gamefeature.party.PartyUpdatedEvent;
import dev.jb0s.blockgameenhanced.event.screen.ScreenOpenedEvent;
import dev.jb0s.blockgameenhanced.event.screen.ScreenReceivedInventoryEvent;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.helper.DebugHelper;
import dev.jb0s.blockgameenhanced.helper.MathHelper;
import dev.jb0s.blockgameenhanced.helper.TimeHelper;
import lombok.Getter;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartyGameFeature extends GameFeature {
    private static final String PARTY_LIST_SCREEN_NAME = "Party";
    private static final SoundEvent PARTY_MEMBER_DEATH_SOUND = new SoundEvent(new Identifier("blockgame", "mus.gui.combat.death"));
    private static final String JOINED_PARTY_MESSAGE_REGEX = "(.*) joined your party!";
    private static final String LEFT_PARTY_MESSAGE_REGEX = "(.*) has left the party.";
    private static final String LEFT_GAME_MESSAGE_REGEX = "(.*) left the game";

    @Getter
    private ArrayList<PartyMember> partyMembers;

    @Getter
    private HashMap<PartyMember, PartyPing> partyPings;

    @Getter
    private int currentPayloadSyncId = -1;

    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);

        // Subscribe to events
        ReceiveChatMessageEvent.EVENT.register(((client1, message) -> handleChatMessage(message)));
        OtherPlayerTickEvent.EVENT.register(((client1, otherPlayer) -> handlePlayerHealth(otherPlayer, (int)otherPlayer.getHealth(), (int)otherPlayer.getMaxHealth(), otherPlayer.isAlive())));
        PlayerTickEvent.EVENT.register(((client1, player) -> handlePlayerHealth(player, (int)player.getHealth(), (int)player.getMaxHealth(), player.isAlive())));
        WorldRenderEvents.END.register(ctx -> preRenderPings(ctx.matrixStack(), ctx.projectionMatrix(), ctx.tickDelta()));
        ScreenOpenedEvent.EVENT.register(this::handleScreenOpen);
        ScreenReceivedInventoryEvent.EVENT.register(this::handleInventoryUpdate);
        PingHotkeyPressedEvent.EVENT.register(this::tryPing);
    }

    @Override
    public void tick() {
        // If we've gotten disconnected or are not allowed to query the server,
        // we should reset our state completely, immediately.
        if(getMinecraftClient().world == null) {
            if(partyMembers != null) {
                // We need to invoke this event to let all other managers know the party disbanded due to a disconnect
                PartyUpdatedEvent.EVENT.invoker().partyUpdatedEvent(this);
            }

            currentPayloadSyncId = -1;
            partyMembers = null;
            partyPings = null;
        }
    }

    public void preRenderPings(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta) {
        if(partyPings == null) return;

        Matrix4f x = matrices.peek().getPositionMatrix();
        for(PartyPing ping : partyPings.values()) {
            ping.setScreenSpacePos(MathHelper.worldToScreenSpace(ping.getLocation(), x, projectionMatrix));
        }
    }

    /**
     * Handles an incoming packet telling us to open a screen.
     * @param packet The incoming packet data.
     * @return Whether this packet had info that the party manager needed.
     */
    public ActionResult handleScreenOpen(OpenScreenS2CPacket packet) {
        // If the screen we've just opened is anything but a generic grid that could display party info, we don't wanna mess with the screens. It breaks shit.
        if(packet.getScreenHandlerType() != ScreenHandlerType.GENERIC_9X3 && packet.getScreenHandlerType() != ScreenHandlerType.GENERIC_9X6) {
            return ActionResult.PASS;
        }

        int syncId = packet.getSyncId();
        String name = packet.getName().asString();
        boolean isPartyMembersScreen = name.startsWith(PARTY_LIST_SCREEN_NAME);

        // Disregard this packet if it's unrelated to the party manager
        if(!isPartyMembersScreen) {
            return ActionResult.PASS;
        }

        // Update values so we can wait for the inventory content packet
        currentPayloadSyncId = syncId;
        return ActionResult.PASS;
    }

    /**
     * Handles an incoming packet telling us to update an inventory's content.
     * @param packet The incoming packet data.
     * @return Whether this packet had info that the party manager needed.
     */
    public ActionResult handleInventoryUpdate(InventoryS2CPacket packet) {
        // If this inventory update is not for our party window, disregard it.
        if(currentPayloadSyncId != packet.getSyncId()) {
            return ActionResult.PASS;
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
                PlayerListEntry playerEntry = getMinecraftClient().getNetworkHandler().getPlayerListEntry(name);

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
            Collection<PlayerListEntry> onlinePlayers = getMinecraftClient().getNetworkHandler().getPlayerList();

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

        currentPayloadSyncId = -1;
        return ActionResult.PASS;
    }

    /**
     * Routine that handles what needs to happen when we've detected a new player in our party.
     * @param player The PlayerListEntry of the player that has joined.
     */
    private void handlePlayerJoinedParty(PlayerListEntry player) {
        if(partyMembers == null) {
            partyMembers = new ArrayList<>();
            partyPings = new HashMap<>();
        }

        // Don't allow duplicate party members
        if(isPlayerInParty(player)) {
            return;
        }

        // Add to party
        partyMembers.add(new PartyMember(player));

        // Toast notification saying that player joined
        boolean playerIsMe = player.getProfile().getName().equals(getMinecraftClient().getSession().getProfile().getName());
        if(!playerIsMe) {
            Text toastTitle = new TranslatableText("hud.blockgame.toast.party.joined.title");
            Text toastDescription = new TranslatableText("hud.blockgame.toast.party.joined.description", player.getProfile().getName());
            getMinecraftClient().getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, toastTitle, toastDescription));
        }

        // Invoke party updated event
        PartyUpdatedEvent.EVENT.invoker().partyUpdatedEvent(this);
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
            partyPings = null;

            // Toast notification saying that party disbanded
            Text toastTitle = new TranslatableText("hud.blockgame.toast.party.disbanded.title");
            Text toastDescription = new TranslatableText("hud.blockgame.toast.party.disbanded.description");
            getMinecraftClient().getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, toastTitle, toastDescription));

            // Invoke party updated event
            PartyUpdatedEvent.EVENT.invoker().partyUpdatedEvent(this);
        }
        else {
            partyMembers.remove(member);
            partyPings.remove(member);

            // Toast notification saying that player left
            Text toastTitle = new TranslatableText("hud.blockgame.toast.party.left.title");
            Text toastDescription = new TranslatableText("hud.blockgame.toast.party.left.description", member.getPlayerName());
            getMinecraftClient().getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, toastTitle, toastDescription));

            // Invoke party updated event
            PartyUpdatedEvent.EVENT.invoker().partyUpdatedEvent(this);
        }
    }

    /**
     * Handle an incoming HP update from a player entity in the world.
     * Every ticking player entity sends this data to us, we only care about our party members though.
     * @param player The entity of the player that is sending us their health data.
     * @param health The amount of health that they have. This is a vanilla value, 0 - 20.
     * @param maxHealth The maximum amount of health that they have. This is usually 20, but can be increased with max hp boost.
     * @param isAlive Whether the player entity is considered "alive" or not. This is more trustworthy than checking (health == 0).
     */
    public void handlePlayerHealth(PlayerEntity player, int health, int maxHealth, boolean isAlive) {
        if(partyMembers == null)
            return;

        // We don't care about this player's health if we're not in a party with them
        PartyMember member = getPartyMember(player.getGameProfile().getName());
        if(member == null) {
            // Unset glowing flag if set
            if(player.getFlag(2)) {
                player.setFlag(2, false);
                DebugHelper.debugMessage("setting flag 2 to false");
            }
            return;
        }

        if(maxHealth > 55) {
            // Vanilla health in terms of Blockgame should never ever reach anything above 55 typically. That would be 27.5 hearts.
            // In case the server gives us an absurd value, we can just cap it down to 20 until it comes to its senses.
            maxHealth = 20;
        }

        // Update PartyMember stats
        member.setHealth(health);
        member.setMaxHealth(maxHealth);
        member.setLastUpdateSecond(TimeHelper.getSystemTimeUnix());

        // Set entity glowing flag if unset
        if(!player.getFlag(2)) {
            player.setFlag(2, true);
            DebugHelper.debugMessage("setting flag 2 to true");
        }

        // Death / respawn checks
        if(!isAlive && member.isAlive()) {
            member.setAlive(false);
            member.setHealth(0);

            // Play teammate death sound if allowed by user
            boolean shouldPlayDeathNotifySound = BlockgameEnhanced.getConfig().getPartyHudConfig().deathNotify;
            if(shouldPlayDeathNotifySound) {
                getMinecraftClient().player.playSound(PARTY_MEMBER_DEATH_SOUND, SoundCategory.PLAYERS, .4f, 1f);
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
    public ActionResult handleChatMessage(String message) {
        Pattern joinedPattern = Pattern.compile(JOINED_PARTY_MESSAGE_REGEX);
        Pattern leftPattern = Pattern.compile(LEFT_PARTY_MESSAGE_REGEX);
        Pattern leftGamePattern = Pattern.compile(LEFT_GAME_MESSAGE_REGEX);
        Matcher joinedMatcher = joinedPattern.matcher(message);
        Matcher leftMatcher = leftPattern.matcher(message);
        Matcher leftGameMatcher = leftGamePattern.matcher(message);

        // Check for chat notifications
        if(joinedMatcher.matches()) {
            String playerName = joinedMatcher.group(1);
            PlayerListEntry ple = getMinecraftClient().getNetworkHandler().getPlayerListEntry(playerName);
            handlePlayerJoinedParty(ple);
            return ActionResult.PASS;
        }
        if(leftMatcher.matches() || leftGameMatcher.matches()) {
            String playerName = (leftMatcher.matches() ? leftMatcher : leftGameMatcher).group(1);
            PartyMember member = getPartyMember(playerName);
            if(member != null) {
                handlePlayerExitedParty(member);
            }

            return ActionResult.PASS;
        }

        // Check for pings
        if(message.startsWith("[Party] ") && getMinecraftClient().world != null) {
            String[] args = message.split(" ~ ");
            if(args.length < 2) return ActionResult.PASS;

            if(args[1].equals("Ping")) {
                if(args.length < 3) return ActionResult.PASS;
                String pmb = args[0].substring(8, args[0].indexOf(":"));
                String[] loc = args[2].substring(1, args[2].length() - 1).split(", ");
                Vec3d pos = new Vec3d(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]));
                String wld = args[3];

                // Store ping data
                PartyMember partyMember = getPartyMember(pmb);
                if(partyPings.containsKey(partyMember)) {
                    PartyPing ping = partyPings.get(partyMember);
                    ping.setLocation(pos);
                    ping.setWorld(wld);
                }
                else {
                    PartyPing ping = new PartyPing(partyMember, pos, wld);
                    partyPings.put(partyMember, ping);
                }

                // Play sound indicating new ping data if worlds match
                boolean markerWorldMatchesPlayer = partyPings.get(partyMember).getWorld().equals(getMinecraftClient().world.getRegistryKey().getValue().getPath());
                boolean configAllowsMarkerSound = BlockgameEnhanced.getConfig().getPartyHudConfig().markNotify;
                ClientPlayerEntity clientPlayerEntity = getMinecraftClient().player;
                if(markerWorldMatchesPlayer && configAllowsMarkerSound && clientPlayerEntity != null) {
                    Vec3d clampedPos = MathHelper.clampMagnitude(pos.subtract(clientPlayerEntity.getPos()), 0.0, 5.0).add(clientPlayerEntity.getPos());
                    getMinecraftClient().world.playSound(clampedPos.x, clampedPos.y, clampedPos.z, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.65f, 0.75f, false);
                }

                PartyPingEvent.EVENT.invoker().partyPingEvent(this);
                return ActionResult.SUCCESS;
            }

            if(args[1].equals("Unping")) {
                String pmb = args[0].substring(8, args[0].indexOf(":"));
                PartyMember partyMember = getPartyMember(pmb);
                partyPings.remove(partyMember);
                PartyPingEvent.EVENT.invoker().partyPingEvent(this);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    /**
     * Attempts to ping/unping a location.
     */
    public void tryPing(MinecraftClient client) {
        ClientPlayerEntity cpe = getMinecraftClient().player;
        if(cpe == null || partyPings == null) return;

        HitResult hit = cpe.raycast(1000.f, 0.0f, false);
        if(hit.getType().equals(HitResult.Type.BLOCK)) {
            PartyPing existingPing = partyPings.get(getPartyMember(getMinecraftClient().getSession().getUsername()));

            // If we're pinging where a ping already exists, unping instead
            if(existingPing != null && existingPing.isHovered()) {
                cpe.sendChatMessage("@~ Unping");
            }
            else {
                cpe.sendChatMessage(String.format("@~ Ping ~ " + hit.getPos().toString() + " ~ " + cpe.world.getRegistryKey().getValue().getPath()));
            }
        }
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
}
