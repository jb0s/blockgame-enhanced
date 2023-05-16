package dev.jb0s.blockgameenhanced.manager.adventure;

public class AdventureZoneList {
    private AdventureZone[] adventureZones;

    public AdventureZone[] getAdventureZones() {
        if(adventureZones == null)
            adventureZones = new AdventureZone[0];

        return adventureZones;
    }
}
