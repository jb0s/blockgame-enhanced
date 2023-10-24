package dev.jb0s.blockgameenhanced.helper;


import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


public class HudTextHelper {
    public static final int positiveTextColor = 0x55FF55; // Minecraft Green
    public static final int neutralTextColor = 0xFFFFFF; // Minecraft White
    public static final int negativeTextColor = 0xFF5555; // Minecraft Red

    public static MutableText getTitleHudText(String title, int textColor) {
        // Setup Color Styles
        Style configColor = Style.EMPTY.withColor(textColor).withBold(true);

        return new LiteralText(title).setStyle(configColor);
    }

    public static MutableText getCoinHudText(int coins, int coinColor, int textColor) {
        // Setup Color Styles
        Style configCoinColor = Style.EMPTY.withColor(coinColor);
        Style configColor = Style.EMPTY.withColor(textColor);

        return new LiteralText("Coin's: ").setStyle(configColor).append(
                new LiteralText(String.valueOf(coins)).setStyle(configCoinColor)
        );
    }

    public static MutableText getProfessionHudText(String profession, float curLvl, float bonus, float total, float average, int textColor) {
        // Setup Color Styles
        Style configColor = Style.EMPTY.withColor(textColor);
        Style positiveColor = Style.EMPTY.withColor(positiveTextColor);
        Style neutralColor = Style.EMPTY.withColor(neutralTextColor);

        //Base Profession String
        MutableText returnText = new LiteralText(profession);

        if (curLvl > 0f) {
            // Add lvl progress
            returnText.append(getFormattedPercentage(" ≈", curLvl));
        }

        // Add Spacer & Color based on the Users choice
        returnText.append(" |").setStyle(configColor);

        // Add Bonus String and the color based on the value
        returnText.append(new LiteralText(
                bonus > 0f ? getFormattedPercentage(" +", bonus) : " 0%"
        ).setStyle(
                bonus > 0f ? positiveColor : neutralColor
        ));

        // Add the Total and Average Values
        returnText.append(new LiteralText(" | "+ formatNumber(total) + " | " + formatNumber(average) + "⌀").setStyle(configColor));

        return returnText;
    }

    public static String getFormattedPercentage(String prefix, float value) {
        return prefix + String.format("%.2f", value) + "%";
    }

    public static String formatNumber(Float input){
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
        df.applyPattern("###,###,###.0");
        return df.format(input);
    }

    public static float expValueFromString(String message){
        return valueFromString(message, "+", " EXP");
    }

    public static float curLvlValueFromString(String message){
        return valueFromString(message, "- ", "%");
    }

    public static int coinValueFromString(String message){
        return (int) valueFromString(message, "deposited ", " ");
    }

    private static float valueFromString(String message, String searchPrefix, String searchSuffix){
        StringBuilder value = new StringBuilder();
        //System.out.println(searchPrefix + " <--> " + message + " <--> " + searchSuffix);
        for (int i = message.lastIndexOf(searchPrefix) + searchPrefix.length(); i < message.lastIndexOf(searchSuffix); i++) {
            value.append(message.charAt(i));
        }
        //System.out.println(value);
        if (value.isEmpty()) return 0f;
        return Float.parseFloat(value.toString());
    }






}
