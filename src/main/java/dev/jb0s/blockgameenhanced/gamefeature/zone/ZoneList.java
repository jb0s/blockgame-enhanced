package dev.jb0s.blockgameenhanced.gamefeature.zone;

public class ZoneList {
    private Zone[] zones;

    public Zone[] getZones() {
        if(zones == null)
            zones = new Zone[0];

        return zones;
    }
}
