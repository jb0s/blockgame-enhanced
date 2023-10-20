package dev.jb0s.blockgameenhanced.helper;


import dev.jb0s.blockgameenhanced.manager.config.structure.MMOItemModifiers;

public class ExpHudDataHelper {
    // Toggle for the Exp Hud Render
    public static boolean hideOverlay = false;
    // Toggle for switching between Total and Sample
    //TODO Change to hour based system
    public static boolean showGlobal = true;


    public static final Integer DEFAULT_MAX_SAMPLE_VALUE = 128;
    public static final Integer DEFAULT_BASE_BONUS_EXP = 10;

    public static int DEFAULT_TEXT_HEIGHT = 8;





    public static final String[] professionNames = {
            "Archaeology",
            "Fishing",
            "Herbalism",
            "Logging",
            "Mining",
            "Runecarving",
            "Einherjar"};
    public static  final String[] professionSetNames = {
            "ARCHAEOLOGIST_",
            "FISHERMAN_",
            "BOTANIST_",
            "LUMBERJACK_",
            "MINER_"};

    public static int[] professionExpIndexes = new int[professionNames.length];
    public static float[] professionTotalExpValues = new float[professionNames.length];
    public static float[] professionTotalAverageValues = new float[professionNames.length];

    public static float[] professionSampleTotalExpValues = new float[professionNames.length];
    public static float[] professionSampleAverages = new float[professionNames.length];
    public static float[][] professionLastExpValues = new float[professionNames.length][DEFAULT_MAX_SAMPLE_VALUE];

    public static float[] professionLevelValues = new float[professionNames.length];


    public static final String[] nbtKeyNames = {
            MMOItemModifiers.ADD_EXP_ARCHAEOLOGY.tag(),
            MMOItemModifiers.ADD_EXP_FISHING.tag(),
            MMOItemModifiers.ADD_EXP_HERBALISM.tag(),
            MMOItemModifiers.ADD_EXP_LOGGING.tag(),
            MMOItemModifiers.ADD_EXP_MINING.tag(),
            MMOItemModifiers.ADD_EXP_RUNECARVING.tag(),
            MMOItemModifiers.ADD_EXP.tag()
    };

    public static float[] equipmentBonusExpValues = new float[professionNames.length];




    public static int coins = 0;

    public static float baseClassExp = 1f;

    public static int getHudBackgroundBorderSize() {
        return DEFAULT_TEXT_HEIGHT / 2;
    }

    public static void addCoin(String message){
        coins += HudTextHelper.coinValueFromString(message);
    }

    public static void addExp(String message){
        // Loop trough Professions and find match
        for (int p = 0; p < professionNames.length; p++) {
            if (message.contains(professionNames[p])){
                // add base class exp for professions except gained class exp
                int classProfession = professionNames.length - 1;
                if (p != classProfession) {
                    // Adds the 1 Class exp
                    addExpToProfessionArrays(baseClassExp, classProfession);
                }
                float currentExp = HudTextHelper.expValueFromString(message);
                professionLevelValues[p] = HudTextHelper.curLvlValueFromString(message);
                addExpToProfessionArrays(currentExp, p);
            }
        }
    }

    private static void addExpToProfessionArrays(float currentExp, int p) {
        professionTotalExpValues[p] += currentExp;

        int index = professionExpIndexes[p];
        // Make sure index is inside Array Boundary
        if (index >= DEFAULT_MAX_SAMPLE_VALUE) {
            index = index % DEFAULT_MAX_SAMPLE_VALUE;
        }
        // Increment the count
        professionExpIndexes[p]++;

        // subtract the old Value that's about to override
        professionSampleTotalExpValues[p] -= professionLastExpValues[p][index];
        // Add/override the current exp to the array [Profession type][Value]
        professionLastExpValues[p][index] = currentExp;
        // Add current exp to the Sum for the Average
        professionSampleTotalExpValues[p] += currentExp;

        // Calculate the Average so the draw event doesn't have to calculate it every time
        // last Sample Average
        professionSampleAverages[p] = professionSampleTotalExpValues[p] / Math.min(DEFAULT_MAX_SAMPLE_VALUE, professionExpIndexes[p]);
        // Total Average
        professionTotalAverageValues[p] = professionTotalExpValues[p] / professionExpIndexes[p];
    }
}
