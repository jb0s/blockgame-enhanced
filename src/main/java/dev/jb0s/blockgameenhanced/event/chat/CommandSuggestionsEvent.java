package dev.jb0s.blockgameenhanced.event.chat;

import com.mojang.brigadier.suggestion.Suggestions;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

public interface CommandSuggestionsEvent {
    Event<CommandSuggestionsEvent> EVENT = EventFactory.createArrayBacked(CommandSuggestionsEvent.class, (listeners) -> (client, completionsId, suggestions) -> {
        for (CommandSuggestionsEvent listener : listeners) {
            listener.commandSuggestions(client, completionsId, suggestions);
        }
    });

    void commandSuggestions(MinecraftClient client, int completionsId, Suggestions suggestions);
}
