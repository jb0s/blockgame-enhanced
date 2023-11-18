package dev.jb0s.blockgameenhanced.gamefeature.challenges.speedrun;

import dev.jb0s.blockgameenhanced.gamefeature.challenges.Challenge;

public class SpeedrunKrognarsChallenge extends Challenge {
    @Override
    public void preStart() {
        runCommand("warp Krognars_Bastion");
    }


}
