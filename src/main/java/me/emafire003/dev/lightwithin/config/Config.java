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

    public static boolean CHECK_SURROUNDED;
    public static int SURROUNDED_AMOUNT;
    public static double SURROUNDED_ALLIES_MULTIPLIER;
    public static int SURROUNDED_DISTANCE;
    public static boolean CHECK_SURROUNDING_MOBS_HEALTH;
    public static int SURROUNDING_HEALTH_THRESHOLD;

    public static int HP_PERCENTAGE_SELF;
    public static int HP_PERCENTAGE_ALLIES;
    public static int HP_PERCENTAGE_OTHER;

    public static boolean CHECK_ARMOR_DURABILITY;
    public static int DUR_PERCENTAGE_SELF;
    public static int DUR_PERCENTAGE_ALLIES;
    public static int DUR_PERCENTAGE_OTHER;

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

        configs.addKeyValuePair(new Pair<>("check_surrounded", true), "Does the player need to be surrounded to trigger light?");
        configs.addKeyValuePair(new Pair<>("surrounded_amount", 5), "How many hostile entities needs to be near a player to be considered surrounded?");
        configs.addKeyValuePair(new Pair<>("surrounded_allies_multiplier", 2), "When checking if allies are surrounded, how much to multiply the default value above?");
        configs.addKeyValuePair(new Pair<>("surrounded_distance", 5), "How far to check (in blocks) for hostile entities?");
        configs.addKeyValuePair(new Pair<>("check_surrounding_mobs_health", true), "Do a check on the mobs health. If below a certain threshold, stop considering in the surrounded count");
        configs.addKeyValuePair(new Pair<>("surrounding_health_threshold", 15), "The hp percentage below which mobs won't be considered in the surrounding count anymore (like 15, 20, 50)");

        configs.addKeyValuePair(new Pair<>("hp_percentage_self", 25), "The hp percentage below which the light will be triggerable if the target is SELF (like 15, 20, 50) (in some cases it may not apply)");
        configs.addKeyValuePair(new Pair<>("hp_percentage_allies", 50), "The hp percentage below which the light will be triggerable if the target is ALLIES (like 15, 20, 50) (in some cases it may not apply)");
        configs.addKeyValuePair(new Pair<>("hp_percentage_other", 50), "The hp percentage below which the light will be triggerable if the target is OTHER/Passive mobs (like 15, 20, 50) (in some cases it may not apply)");

        configs.addKeyValuePair(new Pair<>("check_armor_durability", false), "Do a check on the armor durability as well. Warning: I'd advise raising the Health percentage if this is enabled");
        configs.addKeyValuePair(new Pair<>("dur_percentage_self", 5), "The armor durability percentage below which the light will be triggerable if the target is SELF (like 15, 20, 50) (in some cases it may not apply)");
        configs.addKeyValuePair(new Pair<>("dur_percentage_allies", 10), "The armor durability percentage below which the light will be triggerable if the target is ALLIES (like 15, 20, 50) (in some cases it may not apply)");
        configs.addKeyValuePair(new Pair<>("dur_percentage_other", 10), "The armor durability percentage below which the light will be triggerable if the target is OTHER/Passive mobs (like 15, 20, 50) (in some cases it may not apply)");

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

        CHECK_SURROUNDED = CONFIG.getOrDefault("check_surrounded", true);
        SURROUNDED_AMOUNT = CONFIG.getOrDefault("surrounded_amount", 5);
        SURROUNDED_ALLIES_MULTIPLIER = CONFIG.getOrDefault("surrounded_allies_multiplier", 2);
        SURROUNDED_DISTANCE = CONFIG.getOrDefault("surrounded_distance", 5);
        CHECK_SURROUNDING_MOBS_HEALTH = CONFIG.getOrDefault("check_surrounding_mobs_health", false);
        SURROUNDING_HEALTH_THRESHOLD = CONFIG.getOrDefault("surrounding_health_threshold", 15);

        HP_PERCENTAGE_SELF = CONFIG.getOrDefault("hp_percentage_self", 25);
        HP_PERCENTAGE_ALLIES = CONFIG.getOrDefault("hp_percentage_allies", 50);
        HP_PERCENTAGE_OTHER = CONFIG.getOrDefault("hp_percentage_other", 50);

        CHECK_ARMOR_DURABILITY = CONFIG.getOrDefault("check_armor_durability", false);
        DUR_PERCENTAGE_SELF = CONFIG.getOrDefault("dur_percentage_self", 5);
        DUR_PERCENTAGE_ALLIES = CONFIG.getOrDefault("dur_percentage_allies", 10);
        DUR_PERCENTAGE_OTHER = CONFIG.getOrDefault("dur_percentage_other", 10);


    }
}

