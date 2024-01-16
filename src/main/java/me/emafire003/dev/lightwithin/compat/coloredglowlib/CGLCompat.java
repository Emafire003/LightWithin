package me.emafire003.dev.lightwithin.compat.coloredglowlib;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.ColoredGlowLibMod;
import me.emafire003.dev.coloredglowlib.util.Color;


public class CGLCompat {
    static ColoredGlowLib cgl = ColoredGlowLibMod.getLib();

    public static ColoredGlowLib getLib(){
        return cgl;
    }

    public static Color fromHex(String color){
        return Color.translateFromHEX(color);
    }

    public static String getModID(){
        return "coloredglowlib";
    }

    public static String toHex(int r, int g, int b){
        return Color.translateToHEX(r, g, b);
    }

    public static String toHex(Color color){
        return Color.translateToHEX(color.r, color.g, color.b);
    }
}
