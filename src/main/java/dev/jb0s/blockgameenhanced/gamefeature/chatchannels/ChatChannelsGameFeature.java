package dev.jb0s.blockgameenhanced.gamefeature.chatchannels;

import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.BlockgameEnhancedClient;
import dev.jb0s.blockgameenhanced.config.modules.ChatChannelsConfig;
import dev.jb0s.blockgameenhanced.event.chat.ReceiveChatMessageEvent;
import dev.jb0s.blockgameenhanced.event.gamefeature.chatchannels.ChatChannelRequestedEvent;
import dev.jb0s.blockgameenhanced.event.gamefeature.chatchannels.ChatChannelToggledEvent;
import dev.jb0s.blockgameenhanced.event.gamefeature.chatchannels.ChatChannelUpdatedEvent;
import dev.jb0s.blockgameenhanced.event.gamefeature.party.PartyUpdatedEvent;
import dev.jb0s.blockgameenhanced.gamefeature.GameFeature;
import dev.jb0s.blockgameenhanced.gamefeature.party.PartyGameFeature;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Stream;

public class ChatChannelsGameFeature extends GameFeature implements ReceiveChatMessageEvent, PartyUpdatedEvent, ChatChannelToggledEvent, ChatChannelRequestedEvent {
    private final static String CHANNEL_MESSAGE_PREFIX = "[TownyChat] You are now talking in ";
    private final static String ALREADY_IN_MESSAGE_PREFIX = "[TownyChat] You are already in ";
    private final static LinkedHashMap<String, ChatChannel> CHANNELS = new LinkedHashMap<>();
    private final static ChatChannel GENERAL_CHANNEL;

    private final static ChatChannelsConfig CONFIG = BlockgameEnhanced.getConfig().getChatChannelsConfig();

    static {
        GENERAL_CHANNEL = new ChatChannel("general", "/towny:g", true, new LiteralText("General").formatted(Formatting.WHITE));
        CHANNELS.put("general", GENERAL_CHANNEL);
        CHANNELS.put("party", new ChatChannel("party", "@", false, false, new LiteralText("Party").formatted(Formatting.LIGHT_PURPLE)));
        CHANNELS.put("town", new ChatChannel("town", "/towny:tc", true, new LiteralText("Town").formatted(Formatting.AQUA)));
        CHANNELS.put("nation", new ChatChannel("nation", "/towny:nc", true, new LiteralText("Nation").formatted(Formatting.YELLOW)));
        CHANNELS.put("alliance", new ChatChannel("alliance", "/towny:ac", true, new LiteralText("Alliance").formatted(Formatting.GREEN)));
        CHANNELS.put("local", new ChatChannel("local", "/towny:lc", true, new LiteralText("Local").formatted(Formatting.WHITE)));
    }

    private ChatChannel selectedChannel;

    public ChatChannel getSelectedChannel() {
        return selectedChannel;
    }

    private void setSelectedChannel(ChatChannel channel) {
        if (!CONFIG.enable) {
            return;
        }

        selectedChannel = channel;
        ChatChannelUpdatedEvent.EVENT.invoker().chatChannelUpdatedEvent(this);
    }


    @Override
    public void init(MinecraftClient minecraftClient, BlockgameEnhancedClient blockgameClient) {
        super.init(minecraftClient, blockgameClient);

        ReceiveChatMessageEvent.EVENT.register(this);
        PartyUpdatedEvent.EVENT.register(this);
        ChatChannelToggledEvent.EVENT.register(this);
        ChatChannelRequestedEvent.EVENT.register(this);
    }

    @Override
    public ActionResult receiveChatMessage(MinecraftClient client, String message) {
        if (!CONFIG.enable) {
            return ActionResult.PASS;
        }

        String channelName = null;
        if (message.startsWith(CHANNEL_MESSAGE_PREFIX)) {
            channelName = message.substring(CHANNEL_MESSAGE_PREFIX.length());
        } else if (message.startsWith(ALREADY_IN_MESSAGE_PREFIX)) {
            // this is a fallback in case we can't access a channel and the UI desyncs
            channelName = message.substring(ALREADY_IN_MESSAGE_PREFIX.length());
        }

        if (channelName != null) {
            ChatChannel channel = CHANNELS.get(channelName);

            // if the channel is not in the map, create a temporary one so the UI doesn't break
            if (channel == null) {
                channel = new ChatChannel(channelName, Text.of(StringUtils.capitalize(channelName)));
            }
            setSelectedChannel(channel);
        }

        return ActionResult.PASS;
    }

    @Override
    public void partyUpdatedEvent(PartyGameFeature gameFeature) {
        boolean isInParty = gameFeature.getPartyMembers() != null;
        ChatChannel partyChannel = CHANNELS.get("party");
        if (partyChannel != null) {
            partyChannel.enabled = isInParty;

            if (selectedChannel.equals(partyChannel) && !isInParty) {
                getMinecraftClient().player.sendChatMessage(GENERAL_CHANNEL.command);
                setSelectedChannel(GENERAL_CHANNEL);
            }
        }
    }

    @Override
    public void chatChannelToggled(Direction direction) {
        if (!CONFIG.enable || selectedChannel == null) {
            return;
        }

        if (selectedChannel.isTemporary) {
            // a temporary channel means we don't know it = user switched with a command
            // no idea where we are in the list, just go back to general
            getMinecraftClient().player.sendChatMessage(GENERAL_CHANNEL.command);
            setSelectedChannel(GENERAL_CHANNEL);
            return;
        }

        ArrayList<ChatChannel> channelValues = new ArrayList<>(CHANNELS.values().stream().toList());

        if (direction == Direction.PREV) {
            Collections.reverse(channelValues);
        }

        List<ChatChannel> channels = Stream.concat(channelValues.stream(), channelValues.stream()).toList();

        int selectedIndex = -1;
        int index = 0;
        while (index < channels.size()) {
            if (channels.get(index).equals(selectedChannel)) {
                selectedIndex = index;
                index += 1;
                continue;
            }

            if (selectedIndex > -1 && channels.get(index).enabled) {
                ChatChannel channel = channels.get(index);
                if (channel.canSwitch) {
                    getMinecraftClient().player.sendChatMessage(channel.command);
                } else {
                    setSelectedChannel(channels.get(index));

                    if (CONFIG.showPartyMessageInChat) {
                        Text channelName = new LiteralText(selectedChannel.name).setStyle(selectedChannel.formattedName.getStyle());
                        Text chatMessage = new LiteralText(CHANNEL_MESSAGE_PREFIX).append(channelName);
                        getMinecraftClient().player.sendMessage(chatMessage, false);
                    }
                }
                return;
            }

            index += 1;
        }
    }

    @Override
    public void chatChannelRequested() {
        if (!CONFIG.enable) {
            return;
        }

        ChatChannelUpdatedEvent.EVENT.invoker().chatChannelUpdatedEvent(this);
    }
}
