package me.emafire003.dev.lightwithin.config;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class BalanceConfig {
    public static SimpleConfig CONFIG;
    private static ConfigProvider configs;

    private static final int ver = 2;
    public static int VERSION;

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

    public static int EARTHEN_MAX_POWER;
    public static int EARTHEN_MAX_DURATION;
    public static int EARTHEN_MIN_POWER;
    public static int EARTHEN_MIN_DURATION;

    public static int WIND_MAX_POWER;
    public static int WIND_MAX_DURATION;
    public static int WIND_MIN_POWER;
    public static int WIND_MIN_DURATION;

    public static int AQUA_MAX_POWER;
    public static int AQUA_MAX_DURATION;
    public static int AQUA_MIN_POWER;
    public static int AQUA_MIN_DURATION;

    public static int FOREST_AURA_MAX_POWER;
    public static int FOREST_AURA_MAX_DURATION;
    public static int FOREST_AURA_MIN_POWER;
    public static int FOREST_AURA_MIN_DURATION;
    public static double FOREST_AURA_PUFF_ACTION_BLOCK_RANGE = 1.5;
    public static double FOREST_AURA_PUFF_MAX_SPAWN_DIST = 4.5;
    public static double FOREST_AURA_PUFF_MIN_SPAWN_DIST = 0.7;

    public static int THUNDER_AURA_MAX_POWER;
    public static int THUNDER_AURA_MAX_DURATION;
    public static int THUNDER_AURA_MIN_POWER;
    public static int THUNDER_AURA_MIN_DURATION;
    public static int THUNDER_AURA_VARIANT_STORM_MIN_SIZE = 15;
    public static int THUNDER_AURA_VARIANT_LIGHTNINGS_PER_LEVEL = 1;
    public static int THUNDER_AURA_ALL_LIGHTNINGS_PER_LEVEL = 1;


    private static final String config_name = "_balancing";

    public static void handleVersionChange(){
        int version_found = CONFIG.getOrDefault("version", ver);
        if(version_found != ver){
            LOGGER.warn("DIFFERENT CONFIG VERSION DETECTED, updating...");
            HashMap<String, String> config_old = CONFIG.getConfigCopy();
            try {
                CONFIG.delete();
                CONFIG = SimpleConfig.of(LightWithin.MOD_ID + config_name).provider(configs).request();
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

        CONFIG = SimpleConfig.of(LightWithin.MOD_ID + config_name).provider(configs).request();

        handleVersionChange();

        try{
            assignConfigs();
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.warn("ERROR! The config could not be read, generating a new one...");

            File source = LightWithin.PATH.resolve(LightWithin.MOD_ID +  config_name + ".yml").toFile();
            File target = LightWithin.PATH.resolve(LightWithin.MOD_ID + "_corruptedorold" +  config_name +".yml").toFile();
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
            CONFIG = SimpleConfig.of(LightWithin.MOD_ID +  config_name).provider(configs).request();
            assignConfigs();
            LOGGER.warn("Generated a new config file, make sure to configure it again!");
        }

        LOGGER.info("All " + configs.getConfigsList().size() + " have been set properly");
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("version", ver), "The version of the config. DO NOT CHANGE IT :D");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");
        configs.addKeyValuePair(new Pair<>("comment", "comment"), "This config file lets you modify how the max and min values of each light when it activates. Be very careful while changing these, they could be already multiplied by a setting in the main config file!");
        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("heal_max_power", 8), "The maximum power multiplier (aka level of the effect)");
        configs.addKeyValuePair(new Pair<>("heal_max_duration", 10), "The maximum duration of the effect");
        configs.addKeyValuePair(new Pair<>("heal_min_power", 1), "The minimum power multiplier (aka level of the effect)");
        configs.addKeyValuePair(new Pair<>("heal_min_duration", 4), "The minimum duration of the effect");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("defense_max_power", 8), "The maximum power multiplier (aka level of the effect)");
        configs.addKeyValuePair(new Pair<>("defense_max_duration", 10), "The maximum duration of the effect");
        configs.addKeyValuePair(new Pair<>("defense_min_power", 1), "The minimum power multiplier (aka level of the effect)");
        configs.addKeyValuePair(new Pair<>("defense_min_duration", 5), "The minimum duration of the effect");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("strength_max_power", 6), "The maximum power multiplier (aka level of the effect)");
        configs.addKeyValuePair(new Pair<>("strength_max_duration", 10), "The maximum duration of the effect");
        configs.addKeyValuePair(new Pair<>("strength_min_power", 1), "The minimum power multiplier (aka level of the effect)");
        configs.addKeyValuePair(new Pair<>("strength_min_duration", 5), "The minimum duration of the effect");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("blazing_default_damage", 2), "The default damage of the light when activates. It will be multiplied by the power multiplier, and also the target will be set on fire so be careful");
        configs.addKeyValuePair(new Pair<>("blazing_max_power", 10), "The maximum power multiplier (aka by how much the default damage gets multiplied)");
        configs.addKeyValuePair(new Pair<>("blazing_max_duration", 15), "The maximum duration of the fire");
        configs.addKeyValuePair(new Pair<>("blazing_min_power", 1), "The minimum power multiplier (aka by how much the default damage gets multiplied)");
        configs.addKeyValuePair(new Pair<>("blazing_min_duration", 5), "The minimum duration of the fire");
        configs.addKeyValuePair(new Pair<>("blazing_crit_multiplier", 1.5), "The damage multiplier for a critical hit (must be >= 1)");
        configs.addKeyValuePair(new Pair<>("blazing_crit_fire_multiplier", 2), "The multiplier of the duration in seconds, for which the target(s) will be set on fire (must be >= 1)");
        configs.addKeyValuePair(new Pair<>("blazing_all_damage_bonus", 3), "The damage bonus (2 = 1 hearth) when the target is ALL");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("frost_max_power", 10), "The maximum power multiplier (aka the AMOUNT of freeze damage inflicted upon activation. 2 = 1 hearth)");
        configs.addKeyValuePair(new Pair<>("frost_max_duration", 18), "The maximum duration of the frost effect");
        configs.addKeyValuePair(new Pair<>("frost_min_power", 1), "The minimum power multiplier (aka the AMOUNT of freeze damage inflicted upon activation. 2 = 1 hearth)");
        configs.addKeyValuePair(new Pair<>("frost_min_duration", 5), "The minimum duration of the frost effect");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("earthen_max_power", 10), "The maximum power multiplier (Used to determine the level of the structures spawned and the bonus damage done to enemies)");
        configs.addKeyValuePair(new Pair<>("earthen_min_power", 1), "The minimum power multiplier (see above)");
        configs.addKeyValuePair(new Pair<>("earthen_max_duration", 18), "The maximum duration of some effects that can be applied (like solid rock, mining fatigue)");
        configs.addKeyValuePair(new Pair<>("earthen_min_duration", 1), "The minimum duration (see above)");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("wind_max_power", 10), "The maximum power multiplier (Used to determine the power of the status effects and the distance of the dash)");
        configs.addKeyValuePair(new Pair<>("wind_min_power", 1), "The minimum power multiplier (see above)");
        configs.addKeyValuePair(new Pair<>("wind_max_duration", 18), "The maximum duration of some effects that can be applied (like speed, haste, slow falling)");
        configs.addKeyValuePair(new Pair<>("wind_min_duration", 1), "The minimum duration (see above)");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("aqua_max_power", 10), "The maximum power multiplier (Determines power of status effects, number of drowned spawned, if the cage will spawn a trident and lightning )");
        configs.addKeyValuePair(new Pair<>("aqua_min_power", 1), "The minimum power multiplier (see above)");
        configs.addKeyValuePair(new Pair<>("aqua_max_duration", 18), "The maximum duration of some effects that can be applied (like water slide, conduit, etc.)");
        configs.addKeyValuePair(new Pair<>("aqua_min_duration", 1), "The minimum duration (see above)");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("forest_aura_max_power", 10), "The maximum power multiplier (Determines power of status effects, number of glowing entities seen, their category, number of puffs spawned )");
        configs.addKeyValuePair(new Pair<>("forest_aura_min_power", 1), "The minimum power multiplier (see above)");
        configs.addKeyValuePair(new Pair<>("forest_aura_max_duration", 18), "The maximum duration of some effects that can be applied (the duration of the effects, the amount of time in which the puffs are spawned)");
        configs.addKeyValuePair(new Pair<>("forest_aura_min_duration", 1), "The minimum duration (see above)");
        configs.addKeyValuePair(new Pair<>("forest_aura_puff_action_block_range", 1.5), "How far puffs affect other entities. Aka how close an entity needs to be in order to obtain the effect of the puff");
        configs.addKeyValuePair(new Pair<>("forest_aura_puff_max_spawn_distance", 4.5), "How far from the caster can puffs spawn, expressed in blocks");
        configs.addKeyValuePair(new Pair<>("forest_aura_puff_min_spawn_distance", 0.7), "How close to the caster can puffs spawn, expressed in blocks");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("thunder_aura_max_power", 10), "The maximum power multiplier (Determines power of status effects, how many lightnings are summonable and how many spawn in a storm )");
        configs.addKeyValuePair(new Pair<>("thunder_aura_min_power", 1), "The minimum power multiplier (see above)");
        configs.addKeyValuePair(new Pair<>("thunder_aura_max_duration", 18), "The maximum duration of some effects that can be applied (like thunder aura and storm aura etc.)");
        configs.addKeyValuePair(new Pair<>("thunder_aura_min_duration", 1), "The minimum duration (see above)");
        configs.addKeyValuePair(new Pair<>("thunder_aura_variant_storm_min_size", 15), "The minimum radius that the superstorm (the area in which lightnings spawn) spans across. Bear in mind that it will be increased by one block per level of power multiplier");
        configs.addKeyValuePair(new Pair<>("thunder_aura_variant_lightnings_per_level", 1), "The number of lightnings that will be spawned in the superstorm in each second per every power multiplier level");
        configs.addKeyValuePair(new Pair<>("thunder_aura_all_lightnings_per_level", 1), "The max number of lightnings that a player can spawn per every power multiplier level");
    }

    public static void reloadConfig(){
        registerConfigs();
        LOGGER.info("All " + configs.getConfigsList().size() + " have been reloaded properly");

    }

    private static void assignConfigs() {
        VERSION = CONFIG.getOrDefault("version", ver);
        HEAL_MAX_POWER = CONFIG.getOrDefault("heal_max_power", 8);
        HEAL_MAX_DURATION = CONFIG.getOrDefault("heal_max_duration", 10);
        HEAL_MIN_POWER = CONFIG.getOrDefault("heal_min_power", 1);
        HEAL_MIN_DURATION = CONFIG.getOrDefault("heal_min_duration", 4);

        DEFENSE_MAX_POWER = CONFIG.getOrDefault("defense_max_power", 8);
        DEFENSE_MAX_DURATION = CONFIG.getOrDefault("defense_max_duration", 10);
        DEFENSE_MIN_POWER = CONFIG.getOrDefault("defense_min_power", 1);
        DEFENSE_MIN_DURATION = CONFIG.getOrDefault("defense_min_duration", 5);

        STRENGTH_MAX_POWER = CONFIG.getOrDefault("strength_max_power", 6);
        STRENGTH_MAX_DURATION = CONFIG.getOrDefault("strength_max_duration", 10);
        STRENGTH_MIN_POWER = CONFIG.getOrDefault("strength_min_power", 1);
        STRENGTH_MIN_DURATION = CONFIG.getOrDefault("strength_min_duration", 5);

        BLAZING_MAX_POWER = CONFIG.getOrDefault("blazing_max_power", 10);
        BLAZING_MAX_DURATION = CONFIG.getOrDefault("blazing_max_duration", 15);
        BLAZING_MIN_POWER = CONFIG.getOrDefault("blazing_min_power", 1);
        BLAZING_MIN_DURATION = CONFIG.getOrDefault("blazing_min_duration", 5);
        BLAZING_CRIT_MULTIPLIER = CONFIG.getOrDefault("blazing_crit_multiplier", 1.5);
        BLAZING_CRIT_FIRE_MULTIPLIER = CONFIG.getOrDefault("blazing_crit_fire_multiplier", 2);
        BLAZING_ALL_DAMAGE_BONUS = CONFIG.getOrDefault("blazing_all_damage_bonus", 3);
        BLAZING_DEFAULT_DAMAGE = CONFIG.getOrDefault("blazing_default_damage", 2);

        FROST_MAX_POWER = CONFIG.getOrDefault("frost_max_power", 10);
        FROST_MAX_DURATION = CONFIG.getOrDefault("frost_max_duration", 18);
        FROST_MIN_POWER = CONFIG.getOrDefault("frost_min_power", 1);
        FROST_MIN_DURATION = CONFIG.getOrDefault("frost_min_duration", 5);

        EARTHEN_MAX_POWER = CONFIG.getOrDefault("earthen_max_power", 10);
        EARTHEN_MAX_DURATION = CONFIG.getOrDefault("earthen_max_duration", 18);
        EARTHEN_MIN_POWER = CONFIG.getOrDefault("earthen_min_power", 1);
        EARTHEN_MIN_DURATION = CONFIG.getOrDefault("earthen_min_duration", 1);

        WIND_MAX_POWER = CONFIG.getOrDefault("wind_max_power", 10);
        WIND_MAX_DURATION = CONFIG.getOrDefault("wind_max_duration", 18);
        WIND_MIN_POWER = CONFIG.getOrDefault("wind_min_power", 1);
        WIND_MIN_DURATION = CONFIG.getOrDefault("wind_min_duration", 1);

        AQUA_MAX_POWER = CONFIG.getOrDefault("aqua_max_power", 10);
        AQUA_MAX_DURATION = CONFIG.getOrDefault("aqua_max_duration", 18);
        AQUA_MIN_POWER = CONFIG.getOrDefault("aqua_min_power", 1);
        AQUA_MIN_DURATION = CONFIG.getOrDefault("aqua_min_duration", 1);

        FOREST_AURA_MAX_POWER = CONFIG.getOrDefault("forest_aura_max_power", 10);
        FOREST_AURA_MAX_DURATION = CONFIG.getOrDefault("forest_aura_max_duration", 18);
        FOREST_AURA_MIN_POWER = CONFIG.getOrDefault("forest_aura_min_power", 1);
        FOREST_AURA_MIN_DURATION = CONFIG.getOrDefault("forest_aura_min_duration", 1);
        FOREST_AURA_PUFF_ACTION_BLOCK_RANGE = CONFIG.getOrDefault("forest_aura_puff_action_block_range", 1.5);
        FOREST_AURA_PUFF_MAX_SPAWN_DIST = CONFIG.getOrDefault("forest_aura_puff_max_spawn_dist", 4.5);
        FOREST_AURA_PUFF_MIN_SPAWN_DIST = CONFIG.getOrDefault("forest_aura_puff_min_spawn_dist", 0.7);

        THUNDER_AURA_MAX_POWER = CONFIG.getOrDefault("thunder_aura_max_power", 10);
        THUNDER_AURA_MAX_DURATION = CONFIG.getOrDefault("thunder_aura_max_duration", 18);
        THUNDER_AURA_MIN_POWER = CONFIG.getOrDefault("thunder_aura_min_power", 1);
        THUNDER_AURA_MIN_DURATION = CONFIG.getOrDefault("thunder_aura_min_duration", 1);
        THUNDER_AURA_VARIANT_STORM_MIN_SIZE = CONFIG.getOrDefault("thunder_aura_variant_storm_min_size", 15);
        THUNDER_AURA_VARIANT_LIGHTNINGS_PER_LEVEL = CONFIG.getOrDefault("thunder_aura_variant_lightnings_per_level", 1);
        THUNDER_AURA_ALL_LIGHTNINGS_PER_LEVEL = CONFIG.getOrDefault("thunder_aura_variant_lightnings_per_level", 1);

    }
}

