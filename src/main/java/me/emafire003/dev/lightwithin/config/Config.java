package me.emafire003.dev.lightwithin.config;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class Config {
    public static SimpleConfig CONFIG;
    private static ConfigProvider configs;

    //box expansion amount while searching for other entities, like when checking for allies or targets near the player
    public static int AREA_OF_SEARCH_FOR_ENTITIES;

    public static double COOLDOWN_MULTIPLIER;
    public static double DURATION_MULTIPLIER;

    public static boolean LUXINTUS_BYPASS_COOLDOWN;
    public static boolean LUXCOGNITA_BYPASS_COOLDOWN;
    public static boolean LUXIMUTUA_BYPASS_COOLDOWN;

    public static boolean PLAYER_GLOWS;

    public static void registerConfigs() {
        configs = new ConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(LightWithin.MOD_ID + "_config").provider(configs).request();

        assignConfigs();
        LOGGER.info("All " + configs.getConfigsList().size() + " have been set properly");
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("area_of_search_for_entities", 6), "The box radius in which other entities (such as allies or targets) will be searched");
        configs.addKeyValuePair(new Pair<>("cooldown_multiplier", 1), "Use this to extend or shorten the cooldown of the light powers effects in general (use <1 values to diminish the cooldown >1 to augment it)");
        configs.addKeyValuePair(new Pair<>("duration_multiplier", 1), "Use this to extend or shorten the duration of the light powers effects in general (use <1 values to diminish the cooldown >1 to augment it)");

        configs.addKeyValuePair(new Pair<>("luxintus_bypass_cooldown", true), "Does eating a Luxintus Berry bypass the cooldown?");
        configs.addKeyValuePair(new Pair<>("luxcognita_bypass_cooldown", true), "Does eating a Luxcognita Berry bypass the cooldown?");
        configs.addKeyValuePair(new Pair<>("luxmutua_bypass_cooldown", false), "Does eating a Luxmutua Berry bypass the cooldown?");

        configs.addKeyValuePair(new Pair<>("player_glows", true), "Does the player glow when the light activates?");

    }

    public static void reloadConfig(){
        registerConfigs();
        LOGGER.info("All " + configs.getConfigsList().size() + " have been reloaded properly");

    }

    private static void assignConfigs() {
        LUXINTUS_BYPASS_COOLDOWN = !CONFIG.getOrDefault("luxintus_bypass_cooldown", true);
        LUXCOGNITA_BYPASS_COOLDOWN = !CONFIG.getOrDefault("luxcognita_bypass_cooldown", true);
        LUXIMUTUA_BYPASS_COOLDOWN = !CONFIG.getOrDefault("luxmutua_bypass_cooldown", false);

        AREA_OF_SEARCH_FOR_ENTITIES = CONFIG.getOrDefault("area_of_search_for_entities", 6);
        COOLDOWN_MULTIPLIER = CONFIG.getOrDefault("cooldown_multiplier", 1);
        DURATION_MULTIPLIER = CONFIG.getOrDefault("duration_multiplier", 1);

        PLAYER_GLOWS = CONFIG.getOrDefault("player_glows", true);
    }
}

