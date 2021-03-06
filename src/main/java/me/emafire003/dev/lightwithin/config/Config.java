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

    public static int HEAL_MAX_POWER;
    public static int HEAL_MAX_DURATION;
    public static int HEAL_MIN_POWER;
    public static int HEAL_MIN_DURATION;

    public static int DEFENSE_MAX_POWER;
    public static int DEFENSE_MAX_DURATION;
    public static int DEFENSE_MIN_POWER;
    public static int DEFENSE_MIN_DURATION;

    public static int STRENGTH_MAX_POWER;
    public static int STRENGTH_MAX_DURATION;
    public static int STRENGTH_MIN_POWER;
    public static int STRENGTH_MIN_DURATION;

    public static int BLAZING_MAX_POWER;
    public static int BLAZING_MAX_DURATION;
    public static int BLAZING_MIN_POWER;
    public static int BLAZING_MIN_DURATION;
    public static double BLAZING_CRIT_MULTIPLIER;
    public static int BLAZING_CRIT_FIRE_MULTIPLIER;
    public static int BLAZING_ALL_DAMAGE_BONUS;
    public static int BLAZING_DEFAULT_DAMAGE;

    public static int FROST_MAX_POWER;
    public static int FROST_MAX_DURATION;
    public static int FROST_MIN_POWER;
    public static int FROST_MIN_DURATION;
    public static double FROST_FREEZE_RES_DURATION_MULTIPLIER;

    public static boolean SHOULD_CHECK_BLOCKS;
    public static boolean STRUCTURE_GRIEFING;

    public static boolean TARGET_FEEDBACK;
    public static boolean RESET_ON_JOIN;

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

        configs.addKeyValuePair(new Pair<>("heal_max_power", 8), "The maximum power multiplier (aka level of the effect)");
        configs.addKeyValuePair(new Pair<>("heal_max_duration", 10), "The maximum duration of the effect");
        configs.addKeyValuePair(new Pair<>("heal_min_power", 2), "The minimum power multiplier (aka level of the effect)");
        configs.addKeyValuePair(new Pair<>("heal_min_duration", 4), "The minimum duration of the effect");

        configs.addKeyValuePair(new Pair<>("defense_max_power", 6), "The maximum power multiplier (aka level of the effect)");
        configs.addKeyValuePair(new Pair<>("defense_max_duration", 10), "The maximum duration of the effect");
        configs.addKeyValuePair(new Pair<>("defense_min_power", 2), "The minimum power multiplier (aka level of the effect)");
        configs.addKeyValuePair(new Pair<>("defense_min_duration", 5), "The minimum duration of the effect");

        configs.addKeyValuePair(new Pair<>("strength_max_power", 6), "The maximum power multiplier (aka level of the effect)");
        configs.addKeyValuePair(new Pair<>("strength_max_duration", 10), "The maximum duration of the effect");
        configs.addKeyValuePair(new Pair<>("strength_min_power", 2), "The minimum power multiplier (aka level of the effect)");
        configs.addKeyValuePair(new Pair<>("strength_min_duration", 5), "The minimum duration of the effect");

        configs.addKeyValuePair(new Pair<>("blazing_default_damage", 2), "The default damage of the light when activates. It will be multiplied by the power multiplier, and also the target will be set on fire so be careful");
        configs.addKeyValuePair(new Pair<>("blazing_max_power", 5), "The maximum power multiplier (aka by how much the default damage gets multiplied)");
        configs.addKeyValuePair(new Pair<>("blazing_max_duration", 15), "The maximum duration of the fire");
        configs.addKeyValuePair(new Pair<>("blazing_min_power", 1), "The minimum power multiplier (aka by how much the default damage gets multiplied)");
        configs.addKeyValuePair(new Pair<>("blazing_min_duration", 5), "The minimum duration of the fire");
        configs.addKeyValuePair(new Pair<>("blazing_crit_multiplier", 1.5), "The damage multiplier for a critical hit (must be >= 1)");
        configs.addKeyValuePair(new Pair<>("blazing_crit_fire_multiplier", 2), "The multiplier of the duration in seconds, for which the target(s) will be set on fire (must be >= 1)");
        configs.addKeyValuePair(new Pair<>("blazing_all_damage_bonus", 3), "The damage bonus (2 = 1 hearth) when the target is ALL");

        configs.addKeyValuePair(new Pair<>("frost_max_power", 8), "The maximum power multiplier (aka the AMOUNT of freeze damage inflicted upon activation. 2 = 1 hearth)");
        configs.addKeyValuePair(new Pair<>("frost_max_duration", 18), "The maximum duration of the frost effect");
        configs.addKeyValuePair(new Pair<>("frost_min_power", 1), "The minimum power multiplier (aka the AMOUNT of freeze damage inflicted upon activation. 2 = 1 hearth)");
        configs.addKeyValuePair(new Pair<>("frost_min_duration", 5), "The minimum duration of the frost effect");
        configs.addKeyValuePair(new Pair<>("frost_freeze_res_duration_multiplier", 2.0), "The multiplier of the duration of the Freeze Resistance status effect given to allies or self. Set to 1 to use the default duration.");


        configs.addKeyValuePair(new Pair<>("should_check_blocks", true), "Should the blocks near the player be checked for the light activation? Could impact on performance");
        configs.addKeyValuePair(new Pair<>("structure_griefing", true), "If set to false will prevent lights from spawning structures on activation (I'd suggest leaving it to true)");

        configs.addKeyValuePair(new Pair<>("command_target_feedback", true), "Should a message be sent the target of a command, such us when changing its innerlight?");
        configs.addKeyValuePair(new Pair<>("reset_on_join", false), "Should the InnerLight be compleatly resetted upon joining the server/world again? Useful after an update of the mod that added new Light Types");
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

        HEAL_MAX_POWER = CONFIG.getOrDefault("heal_max_power", 8);
        HEAL_MAX_DURATION = CONFIG.getOrDefault("heal_max_duration", 10);
        HEAL_MIN_POWER = CONFIG.getOrDefault("heal_min_power", 2);
        HEAL_MIN_DURATION = CONFIG.getOrDefault("heal_min_duration", 4);

        DEFENSE_MAX_POWER = CONFIG.getOrDefault("defense_max_power", 6);
        DEFENSE_MAX_DURATION = CONFIG.getOrDefault("defense_max_duration", 10);
        DEFENSE_MIN_POWER = CONFIG.getOrDefault("defense_min_power", 2);
        DEFENSE_MIN_DURATION = CONFIG.getOrDefault("defense_min_duration", 5);

        STRENGTH_MAX_POWER = CONFIG.getOrDefault("strength_max_power", 6);
        STRENGTH_MAX_DURATION = CONFIG.getOrDefault("strength_max_duration", 10);
        STRENGTH_MIN_POWER = CONFIG.getOrDefault("strength_min_power", 2);
        STRENGTH_MIN_DURATION = CONFIG.getOrDefault("strength_min_duration", 5);

        BLAZING_MAX_POWER = CONFIG.getOrDefault("blazing_max_power", 5);
        BLAZING_MAX_DURATION = CONFIG.getOrDefault("blazing_max_duration", 15);
        BLAZING_MIN_POWER = CONFIG.getOrDefault("blazing_min_power", 1);
        BLAZING_MIN_DURATION = CONFIG.getOrDefault("blazing_min_duration", 5);
        BLAZING_CRIT_MULTIPLIER = CONFIG.getOrDefault("blazing_crit_multiplier", 1.5);
        BLAZING_CRIT_FIRE_MULTIPLIER = CONFIG.getOrDefault("blazing_crit_fire_multiplier", 2);
        BLAZING_ALL_DAMAGE_BONUS = CONFIG.getOrDefault("blazing_all_damage_bonus", 3);
        BLAZING_DEFAULT_DAMAGE = CONFIG.getOrDefault("blazing_default_damage", 2);

        FROST_MAX_POWER = CONFIG.getOrDefault("frost_max_power", 8);
        FROST_MAX_DURATION = CONFIG.getOrDefault("frost_max_duration", 18);
        FROST_MIN_POWER = CONFIG.getOrDefault("frost_min_power", 1);
        FROST_MIN_DURATION = CONFIG.getOrDefault("frost_min_duration", 5);

        SHOULD_CHECK_BLOCKS = CONFIG.getOrDefault("should_check_blocks", true);
        STRUCTURE_GRIEFING = CONFIG.getOrDefault("structure_griefing", true);

        TARGET_FEEDBACK = CONFIG.getOrDefault("command_target_feedback", true);

        RESET_ON_JOIN = CONFIG.getOrDefault("reset_on_join", false);


    }
}

