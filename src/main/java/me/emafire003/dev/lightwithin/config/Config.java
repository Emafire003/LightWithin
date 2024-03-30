package me.emafire003.dev.lightwithin.config;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class Config {
    public static SimpleConfig CONFIG;
    private static ConfigProvider configs;

    private static final int ver = 4;
    public static int VERSION;

    //box expansion amount while searching for other entities, like when checking for allies or targets near the player
    public static int AREA_OF_SEARCH_FOR_ENTITIES;

    public static double COOLDOWN_MULTIPLIER;
    public static double DURATION_MULTIPLIER;

    public static boolean ADJUST_FOR_LOW_DURATION;
    public static int ADJUST_DUR_AMOUNT;
    public static int ADJUST_DUR_THRESHOLD;

    public static boolean LUXINTUS_BYPASS_COOLDOWN;
    public static boolean LUXCOGNITA_BYPASS_COOLDOWN;
    public static boolean LUXMUTUA_BYPASS_COOLDOWN;

    public static boolean PLAYER_GLOWS;

    public static int MIN_ALLIES_LOW;

    public static boolean CHECK_SURROUNDED;
    public static int SURROUNDED_AMOUNT;
    public static double SURROUNDED_ALLIES_MULTIPLIER;
    public static int SURROUNDED_DISTANCE;
    public static boolean CHECK_SURROUNDING_MOBS_HEALTH;
    public static int SURROUNDING_HEALTH_THRESHOLD;

    public static int HP_PERCENTAGE_SELF;
    public static int HP_PERCENTAGE_ALLIES;
    public static int HP_PERCENTAGE_VARIANT;
    public static int HP_PERCENTAGE_INCREMENT;

    public static boolean CHECK_ARMOR_DURABILITY;
    public static int DUR_PERCENTAGE_SELF;
    public static int DUR_PERCENTAGE_ALLIES;
    public static int DUR_PERCENTAGE_VARIANT;

    public static boolean ALWAYS_AFFECT_ALLIES;

    public static boolean SHOULD_CHECK_BLOCKS;
    public static boolean STRUCTURE_GRIEFING;
    public static boolean NON_FUNDAMENTAL_STRUCTURE_GRIEFING;
    //V2
    public static boolean REPLACEABLE_STRUCTURES;
    public static boolean KEEP_ESSENTIALS_STRUCTURES;

    public static boolean TARGET_FEEDBACK;

    public static boolean AUTO_LIGHT_ACTIVATION;

    public static boolean MULTIPLY_DURATION_LIMIT;
    public static boolean LIGHT_LOCKED_DEFAULT;
    public static boolean UNLOCK_WITH_LUXINTUS;

    //Added with version 3
    public static boolean LIGHT_DEFAULT_STATUS;

    public static boolean RESET_ON_JOIN;

    public static double DIV_SELF;
    public static boolean NOT_ALLY_THEN_ENEMY;

    public static int FALL_TRIGGER;

    public static int TRIGGER_BLOCK_RADIUS;

    public static void handleVersionChange(){
        int version_found = CONFIG.getOrDefault("version", ver);
        if(version_found != ver){
            LOGGER.warn("DIFFERENT CONFIG VERSION DETECTED, updating...");
            HashMap<String, String> config_old = CONFIG.getConfigCopy();
            try {
                CONFIG.delete();
                CONFIG = SimpleConfig.of(LightWithin.MOD_ID + "_config").provider(configs).request();
                HashMap<Pair<String, ?>, Pair<String, ?>> sub_map = new HashMap<>();

                CONFIG.getConfigCopy().forEach((key, value) -> sub_map.put(new Pair<>(key, value),  new Pair<>(key, config_old.get(key))));
                CONFIG.updateValues(sub_map);
            } catch (IOException e) {
                LOGGER.info("Could not delete config file");
                e.printStackTrace();
            }
        }
    }

    public static void registerConfigs() {
        configs = new ConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(LightWithin.MOD_ID + "_config").provider(configs).request();

        handleVersionChange();

        try{
            assignConfigs();
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.warn("ERROR! The config could not be read, generating a new one...");

            File source = LightWithin.PATH.resolve(LightWithin.MOD_ID + "_config.yml").toFile();
            File target = LightWithin.PATH.resolve(LightWithin.MOD_ID + "_corruptedorold_config.yml").toFile();
            try{
                FileUtils.copyFile(source, target);
                if(CONFIG.delete()){
                    LOGGER.info("Config deleted successfully");
                }else{
                    LOGGER.error("The config could not be deleted");
                }
            } catch (IOException f) {
                f.printStackTrace();
            }
            CONFIG = SimpleConfig.of(LightWithin.MOD_ID + "_config").provider(configs).request();
            assignConfigs();
            LOGGER.warn("Generated a new config file, make sure to configure it again!");
        }

        LOGGER.info("All " + configs.getConfigsList().size() + " have been set properly");
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("version", ver), "The version of the config. DO NOT CHANGE IT :D");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("area_of_search_for_entities", 6), "The box radius in which other entities (such as allies or targets) will be searched");
        configs.addKeyValuePair(new Pair<>("cooldown_multiplier", 1.0), "Use this to extend or shorten the cooldown of the light powers effects in general (use <1 values to shorten the cooldown >1 to extend it)");
        //the max value for this is 21 now, 16*1.3 = 20.8. The min one is 1. Unless it's adjusted.
        configs.addKeyValuePair(new Pair<>("duration_multiplier", 1.3), "Use this to extend or shorten the duration of the light powers effects in general (WARNING: Values below 1 are possible but not recommended)");
        configs.addKeyValuePair(new Pair<>("multiply_duration_limit", true), "Should the max duration values (see below) be multiplied by the duration multiplier?");
        configs.addKeyValuePair(new Pair<>("player_glows", true), "Does the player glow when the light activates?");
        configs.addKeyValuePair(new Pair<>("always_affect_allies", false), "Should every ally be affected by the effect of a light triggering? For example, should an ally at full health be healed by the heal light of the caster?");
        configs.addKeyValuePair(new Pair<>("div_self", 2), "By how much should the power be divided for applying the effect of the ALLIES to the caster? Set to 1 to disable");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("adjust_for_low_duration", false), "Should a very short duration value be adjusted to make it longer? (Bear in mind that each light has a duration minimum, configurable below)");
        configs.addKeyValuePair(new Pair<>("adjust_dur_amount", 5), "How many extra seconds should be added to the low duration as adjustment. (Requires true above) (Also used with 0 and errors, regardless of the setting above)");
        configs.addKeyValuePair(new Pair<>("adjust_dur_threshold", 4), "Which values to consider being very low. (For example, durations under 4 are considered low and to be adjusted if the setting is enabled)");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("luxintus_bypass_cooldown", true), "Does eating a Luxintus Berry bypass the cooldown?");
        configs.addKeyValuePair(new Pair<>("luxcognita_bypass_cooldown", true), "Does eating a Luxcognita Berry bypass the cooldown?");
        configs.addKeyValuePair(new Pair<>("luxmutua_bypass_cooldown", false), "Does eating a Luxmutua Berry bypass the cooldown?");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("check_surrounded", true), "Should being surrounded be considered to trigger light?");
        configs.addKeyValuePair(new Pair<>("surrounded_amount", 5), "How many hostile entities needs to be near a player to be considered surrounded?");
        configs.addKeyValuePair(new Pair<>("surrounded_allies_multiplier", 2.0), "When checking if allies are surrounded, how much to multiply the default value above?");
        configs.addKeyValuePair(new Pair<>("surrounded_distance", 5), "How far to check (in blocks) for hostile entities? (Higher values may mean more lag. A lot higher tho)");
        configs.addKeyValuePair(new Pair<>("check_surrounding_mobs_health", true), "Do a check on the mobs health. If below a certain threshold, stop considering in the surrounded count");
        configs.addKeyValuePair(new Pair<>("surrounding_health_threshold", 15), "The hp percentage below which mobs won't be considered in the surrounding count anymore (like 15, 20, 50)");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("hp_percentage_self", 30), "The hp percentage below which the light will be triggerable if the target is SELF (like 15, 20, 50) (in some cases it may not apply)");
        configs.addKeyValuePair(new Pair<>("hp_percentage_allies", 50), "The hp percentage below which the light will be triggerable if the target is ALLIES (like 15, 20, 50) (in some cases it may not apply)");
        configs.addKeyValuePair(new Pair<>("hp_percentage_variant", 50), "The hp percentage below which the light will be triggerable if the target is VARIANT/Passive mobs for example (like 15, 20, 50) (in some cases it may not apply)");
        configs.addKeyValuePair(new Pair<>("hp_percentage_increment", 20), "The hp percentage to add to differentiante from very low and low health");
        configs.addKeyValuePair(new Pair<>("min_allies_low", 1), "How many allies near the player should be on low health for them to trigger a light activation?");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("check_armor_durability", true), "Should the armor durability be considered to trigger light?");
        configs.addKeyValuePair(new Pair<>("dur_percentage_self", 5), "The armor durability percentage below which the light will be triggerable if the target is SELF (like 15, 20, 50) (in some cases it may not apply)");
        configs.addKeyValuePair(new Pair<>("dur_percentage_allies", 10), "The armor durability percentage below which the light will be triggerable if the target is ALLIES (like 15, 20, 50) (in some cases it may not apply)");
        configs.addKeyValuePair(new Pair<>("dur_percentage_variant", 10), "The armor durability percentage below which the light will be triggerable if the target is VARIANT/Passive mobs for example (like 15, 20, 50) (in some cases it may not apply)");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("should_check_blocks", true), "Should the blocks near the player be checked for the light activation? Could impact on performance");
        configs.addKeyValuePair(new Pair<>("structure_griefing", true), "If set to false will prevent lights from spawning ANY KIND structures on activation (I'd suggest leaving it to true)");
        configs.addKeyValuePair(new Pair<>("non_fundamental_structure_griefing", true), "If set to false will prevent lights from spawning structures that are not fundamental for the light's effect. For example Earthen Light's structures will STILL SPAWN (I'd suggest leaving it to true)");
        //V2
        configs.addKeyValuePair(new Pair<>("replaceable_structures", true), "Should structures be replaced after a while by the old terrain? Setting this to true may impact performance!");
        configs.addKeyValuePair(new Pair<>("keep_essentials_structures", true), "Should structures essential to the effect of the light, such as Earthen light's pillars and ravines be kept if replaceable_structures is true? (aka the terrain won't regenerate)");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("command_target_feedback", true), "Should a message be sent the target of a command, such us when changing its innerlight?");
        configs.addKeyValuePair(new Pair<>("reset_on_join", false), "Should the InnerLight be completely resetted upon joining the server/world again? Useful after an update of the mod that added new Light Types");

        configs.addKeyValuePair(new Pair<>("auto_light_activation", false), "Allow players to auto activate their light if they want to");

        configs.addKeyValuePair(new Pair<>("light_locked_default", false), "Should the light activation be locked by default? (Unless you use a command players won't be able to use lights)");
        configs.addKeyValuePair(new Pair<>("unlock_with_luxintus", true), "Should eating a Luxintus berry unlocked the light?");
        configs.addKeyValuePair(new Pair<>("not_ally_then_enemy", false), "Should a player that is not an explicit ALLY be considered an ENEMY?");

        //V3
        configs.addKeyValuePair(new Pair<>("light_default_status", true), "When using world protector mods, should the light be activatable by default? If false, you'll need to create regions where the it's activatable through world protector mods' flags");

        configs.addKeyValuePair(new Pair<>("fall_trigger", 25), "How many blocks should be passed before a fall is considered very high? 10 blocks added per level of Feather Falling");

        //V3
        configs.addKeyValuePair(new Pair<>("trigger_block_radius", 3), "The radius of blocks to check when searching for blocks to fulfill light trigger conditions. Ex: checking fire blocks for triggering blazing light. WARNING! Could lead to a lot of lag at high values!");
    }

    public static void reloadConfig(){
        registerConfigs();
        LOGGER.info("All " + configs.getConfigsList().size() + " have been reloaded properly");

    }

    private static void assignConfigs() {
        LUXINTUS_BYPASS_COOLDOWN = !CONFIG.getOrDefault("luxintus_bypass_cooldown", true);
        LUXCOGNITA_BYPASS_COOLDOWN = !CONFIG.getOrDefault("luxcognita_bypass_cooldown", true);
        LUXMUTUA_BYPASS_COOLDOWN = !CONFIG.getOrDefault("luxmutua_bypass_cooldown", false);

        VERSION = CONFIG.getOrDefault("version", ver);

        AREA_OF_SEARCH_FOR_ENTITIES = CONFIG.getOrDefault("area_of_search_for_entities", 6);
        COOLDOWN_MULTIPLIER = CONFIG.getOrDefault("cooldown_multiplier", 1.0);
        DURATION_MULTIPLIER = CONFIG.getOrDefault("duration_multiplier", 1.3);

        ADJUST_FOR_LOW_DURATION = CONFIG.getOrDefault("adjust_for_low_duration", false);
        ADJUST_DUR_AMOUNT = CONFIG.getOrDefault("adjust_dur_amount", 5);
        ADJUST_DUR_THRESHOLD = CONFIG.getOrDefault("adjust_dur_threshold", 4);

        PLAYER_GLOWS = CONFIG.getOrDefault("player_glows", true);

        CHECK_SURROUNDED = CONFIG.getOrDefault("check_surrounded", true);
        SURROUNDED_AMOUNT = CONFIG.getOrDefault("surrounded_amount", 5);
        SURROUNDED_ALLIES_MULTIPLIER = CONFIG.getOrDefault("surrounded_allies_multiplier", 2.0);
        SURROUNDED_DISTANCE = CONFIG.getOrDefault("surrounded_distance", 5);
        CHECK_SURROUNDING_MOBS_HEALTH = CONFIG.getOrDefault("check_surrounding_mobs_health", false);
        SURROUNDING_HEALTH_THRESHOLD = CONFIG.getOrDefault("surrounding_health_threshold", 15);

        HP_PERCENTAGE_SELF = CONFIG.getOrDefault("hp_percentage_self", 25);
        HP_PERCENTAGE_ALLIES = CONFIG.getOrDefault("hp_percentage_allies", 50);
        HP_PERCENTAGE_VARIANT = CONFIG.getOrDefault("hp_percentage_variant", 50);
        HP_PERCENTAGE_INCREMENT = CONFIG.getOrDefault("hp_percentage_increment", 20);

        CHECK_ARMOR_DURABILITY = CONFIG.getOrDefault("check_armor_durability", false);
        DUR_PERCENTAGE_SELF = CONFIG.getOrDefault("dur_percentage_self", 5);
        DUR_PERCENTAGE_ALLIES = CONFIG.getOrDefault("dur_percentage_allies", 10);
        DUR_PERCENTAGE_VARIANT = CONFIG.getOrDefault("dur_percentage_variant", 10);
        ALWAYS_AFFECT_ALLIES = CONFIG.getOrDefault("always_affect_allies", false);

        SHOULD_CHECK_BLOCKS = CONFIG.getOrDefault("should_check_blocks", true);
        STRUCTURE_GRIEFING = CONFIG.getOrDefault("structure_griefing", true);
        NON_FUNDAMENTAL_STRUCTURE_GRIEFING = CONFIG.getOrDefault("non_fundamental_structure_griefing", true);

        TARGET_FEEDBACK = CONFIG.getOrDefault("command_target_feedback", true);

        RESET_ON_JOIN = CONFIG.getOrDefault("reset_on_join", false);

        AUTO_LIGHT_ACTIVATION = Config.CONFIG.getOrDefault("auto_light_activation", false);

        MULTIPLY_DURATION_LIMIT = Config.CONFIG.getOrDefault("multiply_duration_limit", true);
        LIGHT_LOCKED_DEFAULT = Config.CONFIG.getOrDefault("light_locked_default", false);

        MIN_ALLIES_LOW = Config.CONFIG.getOrDefault("min_allies_low", 1);
        DIV_SELF = Config.CONFIG.getOrDefault("div_self", 2.5);
        NOT_ALLY_THEN_ENEMY = Config.CONFIG.getOrDefault("not_ally_then_enemy", false);
        FALL_TRIGGER = Config.CONFIG.getOrDefault("fall_trigger", 25);

        //Config version 2
        REPLACEABLE_STRUCTURES = CONFIG.getOrDefault("replaceable_structures", true);
        KEEP_ESSENTIALS_STRUCTURES = CONFIG.getOrDefault("keep_essentials_structures", true);

        //Config version 3
        LIGHT_DEFAULT_STATUS = CONFIG.getOrDefault("light_default_status", true);
        TRIGGER_BLOCK_RADIUS = Config.CONFIG.getOrDefault("trigger_block_radius", 3);
    }
}

