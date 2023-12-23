package dev.jb0s.blockgameenhanced.event.gamefeature.challenges;

import dev.jb0s.blockgameenhanced.gamefeature.challenges.Challenge;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ChallengeStartedEvent {
    Event<ChallengeStartedEvent> EVENT = EventFactory.createArrayBacked(ChallengeStartedEvent.class, (listeners) -> (challenge) -> {
        for (ChallengeStartedEvent listener : listeners) {
            listener.challengeStartedEvent(challenge);
        }
    });

    void challengeStartedEvent(Challenge challenge);
}
