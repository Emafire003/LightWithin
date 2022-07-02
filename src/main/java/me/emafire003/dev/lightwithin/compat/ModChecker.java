package me.emafire003.dev.lightwithin.compat;

import java.util.HashMap;

public class ModChecker {
    private static HashMap<String, Boolean> loaded_mods = new HashMap<>();

    public static boolean isLoaded(String mod_id){
        return loaded_mods.get(mod_id);
    }

    public static void setLoaded(String mod_id, boolean loaded){
        loaded_mods.put(mod_id, true);
    }
}
