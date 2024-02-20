package me.emafire003.dev.lightwithin.compat.coloredglowlib;

import me.emafire003.dev.coloredglowlib.ColoredGlowLibAPI;
import me.emafire003.dev.coloredglowlib.ColoredGlowLibMod;
public class CGLCompat {

    public static ColoredGlowLibAPI getLib(){
        return ColoredGlowLibMod.getColoredGlowLib();
    }

    public static String getModID(){
        return "coloredglowlib";
    }
}
