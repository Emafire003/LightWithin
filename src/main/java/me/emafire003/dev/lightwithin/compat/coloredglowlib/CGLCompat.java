package me.emafire003.dev.lightwithin.compat.coloredglowlib;

import me.emafire003.dev.coloredglowlib.ColoredGlowLibAPI;
import me.emafire003.dev.coloredglowlib.ColoredGlowLibMod;
public class CGLCompat {

    public static ColoredGlowLibAPI getLib(){
        //TODO fix CGL
        return null;
        //return ColoredGlowLibMod.getColoredGlowLib();
    }

    public static String getModID(){
        return "coloredglowlib";
    }
}
