package me.emafire003.dev.lightwithin.config;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class TriggerConfig {
    public static SimpleConfig CONFIG;
    private static ConfigProvider defaultConfigs;

    private static final int ver = 2;
    public static int VERSION;

    public static int TRIGGER_THRESHOLD;

    //Heal light
    //Self
    public static int HEAL_SELF_VERY_LOW_HEALTH;
    public static int HEAL_SELF_LOW_HEALTH;
    public static int HEAL_SELF_SURROUNDED;
    public static int HEAL_SELF_ARMOR_DURABILITY;
    public static int HEAL_SELF_POISONED;

    //Allies
    public static int HEAL_ALLIES_ALLY_LOW_HEALTH;
    public static int HEAL_ALLIES_VERY_LOW_HEALTH;
    public static int HEAL_ALLIES_SURROUNDED;
    public static int HEAL_ALLIES_ALLY_ARMOR_DURABILITY;
    public static int HEAL_ALLIES_ALLY_POISONED;

    //Variant
    public static int HEAL_VARIANT_PASSIVE_LOW_HEALTH;
    public static int HEAL_VARIANT_VERY_LOW_HEALTH;
    public static int HEAL_VARIANT_ALLY_LOW_HEALTH;
    public static int HEAL_VARIANT_HARMFUL_EFFECT;
    public static int HEAL_VARIANT_OTHER_HARMFUL_EFFECT;

    //Defence light
    //Self
    public static int DEF_SELF_VERY_LOW_HEALTH;
    public static int DEF_SELF_LOW_HEALTH;
    public static int DEF_SELF_SURROUNDED;
    public static int DEF_SELF_ARMOR_DURABILITY;

    //Allies
    public static int DEF_ALLIES_ALLY_LOW_HEALTH;
    public static int DEF_ALLIES_VERY_LOW_HEALTH;
    public static int DEF_ALLIES_SURROUNDED;
    public static int DEF_ALLIES_ALLY_ARMOR_DURABILITY;

    //Variant
    public static int DEF_VARIANT_PASSIVE_LOW_HEALTH;

    //Strength light
    //Self-Variant
    public static int STR_SELF_VARIANT_VERY_LOW_HEALTH;
    public static int STR_SELF_VARIANT_LOW_HEALTH;
    public static int STR_SELF_VARIANT_SURROUNDED;
    public static int STR_SELF_VARIANT_ARMOR_DURABILITY;

    //Allies
    public static int STR_ALLIES_ALLY_LOW_HEALTH;
    public static int STR_ALLIES_VERY_LOW_HEALTH;
    public static int STR_ALLIES_SURROUNDED;
    public static int STR_ALLIES_ALLY_ARMOR_DURABILITY;

    //Blazing light
    //All
    public static int BLAZING_ALL_VERY_LOW_HEALTH;
    public static int BLAZING_ALL_LOW_HEALTH;
    public static int BLAZING_ALL_SURROUNDED;
    public static int BLAZING_ALL_ARMOR_DURABILITY;
    public static int BLAZING_ALL_CONDITIONS;
    public static int BLAZING_ALL_ONFIRE;

    //Enemies
    public static int BLAZING_ENEMIES_VERY_LOW_HEALTH;
    public static int BLAZING_ENEMIES_SURROUNDED;
    public static int BLAZING_ENEMIES_ALLY_ARMOR_DURABILITY;
    public static int BLAZING_ENEMIES_ARMOR_DURABILITY;
    public static int BLAZING_ENEMIES_CONDITIONS;
    public static int BLAZING_ENEMIES_ONFIRE;

    public static List<String> BLAZING_TRIGGER_BLOCKS;
    public static List<String> BLAZING_TRIGGER_ITEMS;
    private static final List<String> fire_items = CheckUtils.toItemStringList(Arrays.asList(Items.TORCH, Items.FIRE_CHARGE, Items.FLINT_AND_STEEL, Items.CAMPFIRE, Items.SOUL_CAMPFIRE, Items.SOUL_TORCH, Items.LAVA_BUCKET));
    private static final List<String> fire_blocks = CheckUtils.toBlockStringList(Arrays.asList(Blocks.LAVA, Blocks.MAGMA_BLOCK, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.WALL_TORCH, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE));

    //Frost light
    //All
    public static int FROST_ALL_VERY_LOW_HEALTH;
    public static int FROST_ALL_LOW_HEALTH;
    public static int FROST_ALL_SURROUNDED;
    public static int FROST_ALL_ARMOR_DURABILITY;
    public static int FROST_ALL_CONDITIONS;
    public static int FROST_ALL_FREEZING;

    //Enemies
    public static int FROST_ENEMIES_VERY_LOW_HEALTH;
    public static int FROST_ENEMIES_ALLY_LOW_HEALTH;
    public static int FROST_ENEMIES_SURROUNDED;
    public static int FROST_ENEMIES_ALLY_ARMOR_DURABILITY;
    public static int FROST_ENEMIES_ARMOR_DURABILITY;
    public static int FROST_ENEMIES_CONDITIONS;
    public static int FROST_ENEMIES_FREEZING;

    //SELF
    public static int FROST_SELF_VERY_LOW_HEALTH;
    public static int FROST_SELF_LOW_HEALTH;
    public static int FROST_SELF_ARMOR_DURABILITY;
    public static int FROST_SELF_CONDITIONS;
    public static int FROST_SELF_FREEZING;

    //Allies
    public static int FROST_ALLIES_ALLY_LOW_HEALTH;
    public static int FROST_ALLIES_VERY_LOW_HEALTH;
    public static int FROST_ALLIES_SURROUNDED;
    public static int FROST_ALLIES_ALLY_ARMOR_DURABILITY;
    public static int FROST_ALLIES_CONDITIONS;
    public static int FROST_ALLIES_FREEZING;
    public static int FROST_ALLIES_ALLY_FREEZING;

    public static List<String> FROST_TRIGGER_ITEMS;
    public static List<String> FROST_TRIGGER_BLOCKS;
    private static final List<String> ice_items = CheckUtils.toItemStringList(Arrays.asList(Items.ICE, Items.PACKED_ICE, Items.BLUE_ICE, Items.SNOW, Items.SNOW_BLOCK, Items.SNOWBALL, Items.POWDER_SNOW_BUCKET));
    private static final List<String> ice_blocks = CheckUtils.toBlockStringList(Arrays.asList(Blocks.POWDER_SNOW, Blocks.SNOW, Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW_CAULDRON));


    //Earthen light
    //Variant
    public static int EARTHEN_VARIANT_ALLY_LOW_HEALTH;
    public static int EARTHEN_VARIANT_VERY_LOW_HEALTH;
    public static int EARTHEN_VARIANT_SURROUNDED;
    public static int EARTHEN_VARIANT_CONDITIONS;

    //Enemies
    public static int EARTHEN_ENEMIES_ALLY_LOW_HEALTH;
    public static int EARTHEN_ENEMIES_VERY_LOW_HEALTH;
    public static int EARTHEN_ENEMIES_SURROUNDED;
    public static int EARTHEN_ENEMIES_CONDITIONS;

    //SELF
    public static int EARTHEN_SELF_VERY_LOW_HEALTH;
    public static int EARTHEN_SELF_LOW_HEALTH;
    public static int EARTHEN_SELF_SURROUNDED;
    public static int EARTHEN_SELF_CONDITIONS;

    //Allies
    public static int EARTHEN_ALLIES_ALLY_LOW_HEALTH;
    public static int EARTHEN_ALLIES_VERY_LOW_HEALTH;
    public static int EARTHEN_ALLIES_SURROUNDED;
    public static int EARTHEN_ALLIES_CONDITIONS;

    //Wind light
    //All
    public static int WIND_ALL_VERY_LOW_HEALTH;
    public static int WIND_ALL_SURROUNDED;
    public static int WIND_ALL_FALLING;
    public static int WIND_ALL_FALLING_HIGH;
    public static int WIND_ALL_CONDITIONS;

    //SELF
    public static int WIND_SELF_VERY_LOW_HEALTH;
    public static int WIND_SELF_LOW_HEALTH;
    public static int WIND_SELF_SURROUNDED;
    public static int WIND_SELF_FALLING;
    public static int WIND_SELF_FALLING_HIGH;
    public static int WIND_SELF_CONDITIONS;

    //V2
    public static List<String> WIND_TRIGGER_BLOCKS;
    private static final List<String> air_blocks = CheckUtils.toBlockStringList(List.of(Blocks.AIR));

    //Allies
    public static int WIND_ALLIES_ALLY_LOW_HEALTH;
    public static int WIND_ALLIES_VERY_LOW_HEALTH;
    public static int WIND_ALLIES_SURROUNDED;
    public static int WIND_ALLIES_FALLING_HIGH;
    public static int WIND_ALLIES_ALLY_FALLING;
    public static int WIND_ALLIES_ALLY_FALLING_HIGH;
    public static int WIND_ALLIES_CONDITIONS;

    //Aqua light
    //All
    public static int AQUA_ALL_VERY_LOW_HEALTH;
    public static int AQUA_ALL_SURROUNDED;
    public static int AQUA_ALL_ALLY_ARMOR_DURABILITY;
    public static int AQUA_ALL_CONDITIONS;
    public static int AQUA_ALL_DROWNING;

    //Enemies
    public static int AQUA_ENEMIES_VERY_LOW_HEALTH;
    public static int AQUA_ENEMIES_LOW_HEALTH;
    public static int AQUA_ENEMIES_SURROUNDED;
    public static int AQUA_ENEMIES_ALLY_ARMOR_DURABILITY;
    public static int AQUA_ENEMIES_CONDITIONS;
    public static int AQUA_ENEMIES_DROWNING;

    //SELF
    public static int AQUA_SELF_VERY_LOW_HEALTH;
    public static int AQUA_SELF_LOW_HEALTH;
    public static int AQUA_SELF_SURROUNDED;
    public static int AQUA_SELF_ARMOR_DURABILITY;
    public static int AQUA_SELF_CONDITIONS;
    public static int AQUA_SELF_DROWNING;

    //Allies
    public static int AQUA_ALLIES_ALLY_LOW_HEALTH;
    public static int AQUA_ALLIES_VERY_LOW_HEALTH;
    public static int AQUA_ALLIES_SURROUNDED;
    public static int AQUA_ALLIES_ALLY_ARMOR_DURABILITY;
    public static int AQUA_ALLIES_CONDITIONS;
    public static int AQUA_ALLIES_DROWNING;
    public static int AQUA_ALLIES_ALLY_DROWNING;
    public static List<String> AQUA_TRIGGER_ITEMS;
    public static List<String> AQUA_TRIGGER_BLOCKS;
    private static final List<String> aqua_items = CheckUtils.toItemStringList(Arrays.asList(Items.WATER_BUCKET, Items.GLASS_BOTTLE, Items.HEART_OF_THE_SEA, Items.NAUTILUS_SHELL, Items.CONDUIT));
    private static final List<String> aqua_blocks = CheckUtils.toBlockStringList(Arrays.asList(Blocks.WATER, Blocks.WATER_CAULDRON, Blocks.CONDUIT, Blocks.WET_SPONGE));



    private static final String config_name = "_trigger_balance";

    public static void handleVersionChange(){
        int version_found = CONFIG.getOrDefault("version", ver);
        if(version_found != ver){
            LOGGER.warn("DIFFERENT CONFIG VERSION DETECTED, updating...");
            HashMap<String, String> config_old = CONFIG.getConfigCopy();
            try {
                CONFIG.delete();
                CONFIG = SimpleConfig.of(LightWithin.MOD_ID + config_name).provider(defaultConfigs).request();
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
        defaultConfigs = new ConfigProvider();
        createDefaultConfigs();

        CONFIG = SimpleConfig.of(LightWithin.MOD_ID + config_name).provider(defaultConfigs).request();

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
            CONFIG = SimpleConfig.of(LightWithin.MOD_ID +  config_name).provider(defaultConfigs).request();
            assignConfigs();
            LOGGER.warn("Generated a new config file, make sure to configure it again!");
        }

        LOGGER.info("All " + defaultConfigs.getConfigsList().size() + " have been set properly");
    }

    private static final String caster_very_low_health = "The contribution to the trigger threshold when the caster is in danger and very low health";
    private static final String caster_low_health = "The contribution to the trigger threshold when the caster is in danger and low health";
    private static final String caster_surrounded = "The contribution to the trigger threshold when the caster is surrounded. (If enabled)";
    private static final String caster_low_armor = "The contribution to the trigger threshold when the caster has low durability armor. (If enabled)";
    private static final String caster_poisoned = "The contribution to the trigger threshold when the caster is poisoned";
    private static final String caster_harmful = "The contribution to the trigger threshold when the caster has an harmful status effect";

    private static final String allies_low_health = "The contribution to the trigger threshold when the caster's ALLIES are in danger and on low health";
    private static final String allies_low_armor = "The contribution to the trigger threshold when the caster's allies have low durability armor. (If enabled)";
    private static final String allies_poisoned = "The contribution to the trigger threshold when the caster's allies are poisoned";

    private static final String passive_low_health = "The contribution to the trigger threshold when passive mobs are in danger and on low health";
    private static final String other_harmful = "The contribution to the trigger threshold when another entity has an harmful status effect";

    private static final String light_conditions_1 = "The contribution to the trigger threshold when the ";
    private static final String light_conditions_2 = " light conditions are met";

    private static final String caster_falling = "The contribution to the trigger threshold made by the caster falling a few blocks";
    private static final String caster_falling_high = "The contribution to the trigger threshold made by the caster falling a lot of blocks";
    private static final String allies_falling = "The contribution to the trigger threshold made by the caster's allies falling a few blocks";
    private static final String allies_falling_high = "The contribution to the trigger threshold made by the caster's allies falling a lot of blocks";

    private static final String caster_burning = "The contribution to the trigger threshold made by the caster being on fire";
    private static final String caster_freezing = "The contribution to the trigger threshold made by the caster freezing";
    private static final String ally_freezing = "The contribution to the trigger threshold made by the caster's allies being freezing";
    private static final String caster_drowning = "The contribution to the trigger threshold made by the caster drowning";
    private static final String ally_drowning = "The contribution to the trigger threshold made by the caster's allies drowning";

    private static final String items_required = "A list of items for the fulfillment of the light conditions";
    private static final String blocks_required = "A list of blocks for the fulfillment of the light conditions";

    private static void createDefaultConfigs() {
        defaultConfigs.addKeyValuePair(new Pair<>("version", ver), "The version of the config. DO NOT CHANGE IT :D");

        defaultConfigs.addKeyValuePair(new Pair<>("trigger_threshold", 5), "The threshold to reach in order to activate a light");

        defaultConfigs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");
        defaultConfigs.addKeyValuePair(new Pair<>("comment", "comment"), "This config file lets you modify how the lights trigger. Be very careful if you really want to change this!");
        defaultConfigs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        //==========================Heal==========================
        //Self
        defaultConfigs.addKeyValuePair(new Pair<>("heal_self_very_low_health", 5), caster_very_low_health+" (excludes the low health)");
        defaultConfigs.addKeyValuePair(new Pair<>("heal_self_low_health", 2), caster_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("heal_self_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("heal_self_armor_durability", 1), caster_low_armor);
        defaultConfigs.addKeyValuePair(new Pair<>("heal_self_poisoned", 3), caster_poisoned);

        //Allies
        defaultConfigs.addKeyValuePair(new Pair<>("heal_allies_ally_low_health", 4), allies_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("heal_allies_very_low_health", 1), caster_very_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("heal_allies_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("heal_allies_ally_armor_durability", 1), allies_low_armor);
        defaultConfigs.addKeyValuePair(new Pair<>("heal_allies_ally_poisoned", 2), allies_poisoned);

        //Variant
        defaultConfigs.addKeyValuePair(new Pair<>("heal_variant_passive_low_health", 2), passive_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("heal_variant_very_low_health", 2), caster_very_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("heal_variant_ally_low_health", 2), allies_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("heal_variant_other_harmful_effect", 3), other_harmful + " (excludes the check for the caster)");
        defaultConfigs.addKeyValuePair(new Pair<>("heal_variant_harmful_effect", 3), caster_harmful);

        defaultConfigs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        //==========================Defence==========================
        //Self
        defaultConfigs.addKeyValuePair(new Pair<>("defence_self_very_low_health", 5), caster_very_low_health+" (excludes the low health)");
        defaultConfigs.addKeyValuePair(new Pair<>("defence_self_low_health", 3), caster_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("defence_self_surrounded", 2), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("defence_self_armor_durability", 2), caster_low_armor);

        //Allies
        defaultConfigs.addKeyValuePair(new Pair<>("defence_allies_ally_low_health", 4), allies_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("defence_allies_very_low_health", 1), caster_very_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("defence_allies_surrounded", 2), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("defence_allies_ally_armor_durability", 2), allies_low_armor);

        //Variant
        defaultConfigs.addKeyValuePair(new Pair<>("defence_variant_passive_low_health", 5), "WARNING: This is the only check, so leave it equal to the Trigger" +passive_low_health);

        defaultConfigs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        //==========================Strength==========================
        //Self
        defaultConfigs.addKeyValuePair(new Pair<>("strength_self_variant_very_low_health", 5), caster_very_low_health+" (excludes the low health)");
        defaultConfigs.addKeyValuePair(new Pair<>("strength_self_variant_low_health", 3), caster_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("strength_self_variant_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("strength_self_variant_armor_durability", 2), caster_low_armor);

        //Allies
        defaultConfigs.addKeyValuePair(new Pair<>("strength_allies_ally_low_health", 4), allies_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("strength_allies_very_low_health", 1), caster_very_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("strength_allies_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("strength_allies_ally_armor_durability", 2), allies_low_armor);

        defaultConfigs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        //==========================Blazing==========================
        defaultConfigs.addKeyValuePair(new Pair<>("blazing_trigger_items", fire_items), items_required);
        defaultConfigs.addKeyValuePair(new Pair<>("blazing_trigger_blocks", fire_blocks), blocks_required);
        //All
        defaultConfigs.addKeyValuePair(new Pair<>("blazing_all_very_low_health", 4), caster_very_low_health+" (excludes the low health)");
        defaultConfigs.addKeyValuePair(new Pair<>("blazing_all_low_health", 2), caster_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("blazing_all_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("blazing_all_armor_durability", 1), caster_low_armor);
        defaultConfigs.addKeyValuePair(new Pair<>("blazing_all_onfire", 1), caster_burning);
        defaultConfigs.addKeyValuePair(new Pair<>("blazing_all_conditions", 3), light_conditions_1 + "blazing" + light_conditions_2);

        //Enemies-Variant
        defaultConfigs.addKeyValuePair(new Pair<>("blazing_enemies_very_low_health", 2), caster_very_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("blazing_enemies_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("blazing_all_armor_durability", 1), caster_low_armor);
        defaultConfigs.addKeyValuePair(new Pair<>("blazing_enemies_ally_armor_durability", 2), allies_low_armor);
        defaultConfigs.addKeyValuePair(new Pair<>("blazing_enemies_onfire", 1), caster_burning);
        defaultConfigs.addKeyValuePair(new Pair<>("blazing_enemies_conditions", 3), light_conditions_1 + "blazing" + light_conditions_2);

        defaultConfigs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        //==========================Frost==========================
        defaultConfigs.addKeyValuePair(new Pair<>("frost_trigger_items", ice_items), items_required);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_trigger_blocks", ice_blocks), blocks_required);
        //All
        defaultConfigs.addKeyValuePair(new Pair<>("frost_all_very_low_health", 4), caster_very_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_all_low_health", 2), caster_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_all_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_all_armor_durability", 1), caster_low_armor);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_all_freezing", 1), caster_freezing);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_all_conditions", 3), light_conditions_1 + "frost" + light_conditions_2);

        //Enemies
        defaultConfigs.addKeyValuePair(new Pair<>("frost_enemies_ally_low_health", 3), allies_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_enemies_very_low_health", 1), caster_very_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_enemies_armor_durability", 1), caster_low_armor);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_enemies_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_enemies_ally_armor_durability", 2), allies_low_armor);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_enemies_freezing", 1), caster_freezing);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_enemies_conditions", 3), light_conditions_1 + "frost" + light_conditions_2);

        //Self
        defaultConfigs.addKeyValuePair(new Pair<>("frost_self_very_low_health", 4), caster_very_low_health+" (excludes the low health)");
        defaultConfigs.addKeyValuePair(new Pair<>("frost_self_low_health", 2), caster_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_self_armor_durability", 2), caster_low_armor);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_self_freezing", 1), caster_freezing);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_self_conditions", 3), light_conditions_1 + "frost" + light_conditions_2);

        //Allies
        defaultConfigs.addKeyValuePair(new Pair<>("frost_allies_ally_low_health", 4), allies_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_allies_very_low_health", 1), caster_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_allies_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_allies_ally_armor_durability", 1), allies_low_armor);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_allies_freezing", 1), caster_freezing);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_allies_ally_freezing", 1), ally_freezing);
        defaultConfigs.addKeyValuePair(new Pair<>("frost_allies_conditions", 3), light_conditions_1 + "frost" + light_conditions_2);

        defaultConfigs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        //==========================Earthen==========================
        //Variant
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_variant_very_low_health", 4), caster_very_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_variant_allies_low_healthy", 3), allies_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_variant_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_variant_conditions", 3), light_conditions_1 + "earthen" + light_conditions_2);

        //Enemies
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_enemies_ally_low_health", 3), allies_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_enemies_very_low_health", 4), caster_very_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_enemies_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_enemies_conditions", 3), light_conditions_1 + "earthen" + light_conditions_2);

        //Self
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_self_very_low_health", 3), caster_very_low_health+" (excludes the low health)");
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_self_low_health", 2), caster_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_self_surrounded", 2), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_self_conditions", 3), light_conditions_1 + "frost" + light_conditions_2);

        //Allies
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_allies_ally_low_health", 4), allies_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_allies_very_low_health", 1), caster_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_allies_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("earthen_allies_conditions", 3), light_conditions_1 + "frost" + light_conditions_2);

        defaultConfigs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        //==========================Wind==========================
        defaultConfigs.addKeyValuePair(new Pair<>("wind_trigger_blocks", air_blocks), blocks_required);
        //All
        defaultConfigs.addKeyValuePair(new Pair<>("wind_all_very_low_health", 4), caster_very_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("wind_all_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("wind_all_falling", 1), caster_falling);
        defaultConfigs.addKeyValuePair(new Pair<>("wind_all_falling_high", 3), caster_falling_high);
        defaultConfigs.addKeyValuePair(new Pair<>("wind_all_conditions", 3), light_conditions_1 + "wind" + light_conditions_2);

        //Self
        defaultConfigs.addKeyValuePair(new Pair<>("wind_self_very_low_health", 4), caster_very_low_health+" (excludes the low health)");
        defaultConfigs.addKeyValuePair(new Pair<>("wind_self_low_health", 2), caster_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("wind_self_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("wind_self_falling", 1), caster_falling);
        defaultConfigs.addKeyValuePair(new Pair<>("wind_self_falling_high", 3), caster_falling_high);
        defaultConfigs.addKeyValuePair(new Pair<>("wind_self_conditions", 3), light_conditions_1 + "wind" + light_conditions_2);

        //Allies
        defaultConfigs.addKeyValuePair(new Pair<>("wind_allies_ally_low_health", 4), allies_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("wind_allies_very_low_health", 1), caster_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("wind_allies_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("wind_allies_ally_falling", 2), allies_falling);
        defaultConfigs.addKeyValuePair(new Pair<>("wind_allies_ally_falling_high", 4), allies_falling_high);
        defaultConfigs.addKeyValuePair(new Pair<>("wind_allies_falling", 2), caster_falling);
        defaultConfigs.addKeyValuePair(new Pair<>("wind_allies_conditions", 3), light_conditions_1 + "wind" + light_conditions_2);

        defaultConfigs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        //==========================Aqua==========================
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_trigger_items", aqua_items), items_required);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_trigger_blocks", aqua_blocks), blocks_required+ ". Waterlogged blocks will also be counted automatically");
        //All
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_all_very_low_health", 4), caster_very_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_all_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_all_ally_armor_durability", 1), allies_low_armor);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_all_drowning", 1), caster_drowning);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_all_conditions", 3), light_conditions_1 + "aqua" + light_conditions_2);

        //Enemies
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_enemies_very_low_health", 3), caster_very_low_health + " (excludes low health)");
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_enemies_low_health", 2), caster_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_enemies_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_enemies_ally_armor_durability", 1), allies_low_armor);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_enemies_drowning", 1), caster_drowning);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_enemies_conditions", 3), light_conditions_1 + "aqua" + light_conditions_2);

        //Self
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_self_very_low_health", 4), caster_very_low_health+" (excludes the low health)");
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_self_low_health", 2), caster_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_self_armor_durability", 1), caster_low_armor);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_self_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_self_drowning", 1), caster_drowning);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_self_conditions", 3), light_conditions_1 + "aqua" + light_conditions_2);

        //Allies
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_allies_ally_low_health", 4), allies_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_allies_very_low_health", 1), caster_low_health);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_allies_surrounded", 1), caster_surrounded);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_allies_ally_armor_durability", 1), allies_low_armor);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_allies_drowning", 1), caster_drowning);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_allies_ally_drowning", 1), ally_drowning);
        defaultConfigs.addKeyValuePair(new Pair<>("aqua_allies_conditions", 3), light_conditions_1 + "aqua" + light_conditions_2);

    }

    public static void reloadConfig(){
        registerConfigs();
        LOGGER.info("All " + defaultConfigs.getConfigsList().size() + " have been reloaded properly");

    }

    private static void assignConfigs() {
        VERSION = CONFIG.getOrDefault("version", ver);

        TRIGGER_THRESHOLD = CONFIG.getOrDefault("trigger_threshold", 5);

        //========================Heal========================
        //Self
        HEAL_SELF_VERY_LOW_HEALTH = CONFIG.getOrDefault("heal_self_very_low_health", 5);
        HEAL_SELF_LOW_HEALTH = CONFIG.getOrDefault("heal_self_low_health", 2);
        HEAL_SELF_SURROUNDED = CONFIG.getOrDefault("heal_self_surrounded", 1);
        HEAL_SELF_ARMOR_DURABILITY = CONFIG.getOrDefault("heal_self_armor_durability", 1);
        HEAL_SELF_POISONED = CONFIG.getOrDefault("heal_self_poisoned", 3);

        //Allies
        HEAL_ALLIES_ALLY_LOW_HEALTH = CONFIG.getOrDefault("heal_allies_ally_low_health", 4);
        HEAL_ALLIES_VERY_LOW_HEALTH = CONFIG.getOrDefault("heal_allies_very_low_health", 1);
        HEAL_ALLIES_SURROUNDED = CONFIG.getOrDefault("heal_allies_surrounded", 1);
        HEAL_ALLIES_ALLY_ARMOR_DURABILITY = CONFIG.getOrDefault("heal_allies_ally_armor_durability", 1);
        HEAL_ALLIES_ALLY_POISONED = CONFIG.getOrDefault("heal_allies_ally_poisoned", 2);

        //Variant
        HEAL_VARIANT_PASSIVE_LOW_HEALTH = CONFIG.getOrDefault("heal_variant_passive_low_health", 2);
        HEAL_VARIANT_VERY_LOW_HEALTH = CONFIG.getOrDefault("heal_variant_very_low_health", 2);
        HEAL_VARIANT_ALLY_LOW_HEALTH = CONFIG.getOrDefault("heal_variant_ally_low_health", 2);
        HEAL_VARIANT_HARMFUL_EFFECT = CONFIG.getOrDefault("heal_variant_other_harmful_effect", 3);
        HEAL_VARIANT_OTHER_HARMFUL_EFFECT = CONFIG.getOrDefault("heal_variant_harmful_effect", 3);

        //========================Defence========================
        //Self
        DEF_SELF_VERY_LOW_HEALTH = CONFIG.getOrDefault("defence_self_very_low_health", 5);
        DEF_SELF_LOW_HEALTH = CONFIG.getOrDefault("defence_self_low_health", 2);
        DEF_SELF_SURROUNDED = CONFIG.getOrDefault("defence_self_surrounded", 2);
        DEF_SELF_ARMOR_DURABILITY = CONFIG.getOrDefault("defence_self_armor_durability", 2);

        //Allies
        DEF_ALLIES_ALLY_LOW_HEALTH = CONFIG.getOrDefault("defence_allies_ally_low_health", 4);
        DEF_ALLIES_VERY_LOW_HEALTH = CONFIG.getOrDefault("defence_allies_very_low_health", 1);
        DEF_ALLIES_SURROUNDED = CONFIG.getOrDefault("defence_allies_surrounded", 2);
        DEF_ALLIES_ALLY_ARMOR_DURABILITY = CONFIG.getOrDefault("defence_allies_ally_armor_durability", 2);

        //Variant
        DEF_VARIANT_PASSIVE_LOW_HEALTH = CONFIG.getOrDefault("defence_variant_passive_low_health", 5);

        //========================Strength========================
        //Self-Variant
        STR_SELF_VARIANT_VERY_LOW_HEALTH = CONFIG.getOrDefault("strength_self_variant_very_low_health", 5);
        STR_SELF_VARIANT_LOW_HEALTH = CONFIG.getOrDefault("strength_self_variant_low_health", 2);
        STR_SELF_VARIANT_SURROUNDED = CONFIG.getOrDefault("strength_self_variant_surrounded", 2);
        STR_SELF_VARIANT_ARMOR_DURABILITY = CONFIG.getOrDefault("strength_self_variant_armor_durability", 2);

        //Allies
        STR_ALLIES_ALLY_LOW_HEALTH = CONFIG.getOrDefault("strength_allies_ally_low_health", 4);
        STR_ALLIES_VERY_LOW_HEALTH = CONFIG.getOrDefault("strength_allies_very_low_health", 1);
        STR_ALLIES_SURROUNDED = CONFIG.getOrDefault("strength_allies_surrounded", 2);
        STR_ALLIES_ALLY_ARMOR_DURABILITY = CONFIG.getOrDefault("strength_allies_ally_armor_durability", 2);


        //========================Blazing========================
        //All
        BLAZING_ALL_VERY_LOW_HEALTH = CONFIG.getOrDefault("blazing_all_very_low_health", 4);
        BLAZING_ALL_LOW_HEALTH = CONFIG.getOrDefault("blazing_all_low_health", 2);
        BLAZING_ALL_SURROUNDED = CONFIG.getOrDefault("blazing_all_surrounded", 1);
        BLAZING_ALL_ONFIRE = CONFIG.getOrDefault("blazing_all_onfire", 1);
        BLAZING_ALL_ARMOR_DURABILITY = CONFIG.getOrDefault("blazing_all_armor_durability", 1);
        BLAZING_ALL_CONDITIONS = CONFIG.getOrDefault("blazing_all_conditions", 3);

        //Enemies
        BLAZING_ENEMIES_VERY_LOW_HEALTH = CONFIG.getOrDefault("blazing_enemies_very_low_health", 2);
        BLAZING_ENEMIES_SURROUNDED = CONFIG.getOrDefault("blazing_enemies_surrounded", 1);
        BLAZING_ENEMIES_ONFIRE = CONFIG.getOrDefault("blazing_enemies_onfire", 1);
        BLAZING_ENEMIES_ALLY_ARMOR_DURABILITY = CONFIG.getOrDefault("blazing_all_armor_durability", 1);
        BLAZING_ENEMIES_ARMOR_DURABILITY = CONFIG.getOrDefault("blazing_enemies_ally_armor_durability", 2);
        BLAZING_ENEMIES_CONDITIONS = CONFIG.getOrDefault("blazing_enemies_conditions", 2);

        //V2
        BLAZING_TRIGGER_ITEMS = CONFIG.getOrDefault("blazing_trigger_items", fire_items);
        BLAZING_TRIGGER_BLOCKS = CONFIG.getOrDefault("blazing_trigger_blocks", fire_blocks);

        //========================Frost========================
        //All
        FROST_ALL_VERY_LOW_HEALTH = CONFIG.getOrDefault("frost_all_very_low_health", 4);
        FROST_ALL_LOW_HEALTH = CONFIG.getOrDefault("frost_all_low_health", 2);
        FROST_ALL_SURROUNDED = CONFIG.getOrDefault("frost_all_surrounded", 1);
        FROST_ALL_FREEZING = CONFIG.getOrDefault("frost_all_freezing", 1);
        FROST_ALL_ARMOR_DURABILITY = CONFIG.getOrDefault("frost_all_armor_durability", 1);
        FROST_ALL_CONDITIONS = CONFIG.getOrDefault("frost_all_conditions", 3);

        //Enemies
        FROST_ENEMIES_ALLY_LOW_HEALTH = CONFIG.getOrDefault("frost_enemies_ally_low_health", 3);
        FROST_ENEMIES_VERY_LOW_HEALTH = CONFIG.getOrDefault("frost_enemies_very_low_health", 2);
        FROST_ENEMIES_SURROUNDED = CONFIG.getOrDefault("frost_enemies_surrounded", 1);
        FROST_ENEMIES_FREEZING = CONFIG.getOrDefault("frost_enemies_freezing", 1);
        FROST_ENEMIES_ALLY_ARMOR_DURABILITY = CONFIG.getOrDefault("frost_enemies_ally_armor_durability", 1);
        FROST_ENEMIES_ARMOR_DURABILITY = CONFIG.getOrDefault("frost_enemies_armor_durability", 1);
        FROST_ENEMIES_CONDITIONS = CONFIG.getOrDefault("frost_enemies_conditions", 2);

        //Self
        FROST_SELF_VERY_LOW_HEALTH = CONFIG.getOrDefault("frost_self_very_low_health", 4);
        FROST_SELF_LOW_HEALTH = CONFIG.getOrDefault("frost_self_low_health", 2);
        FROST_SELF_FREEZING = CONFIG.getOrDefault("frost_self_freezing", 1);
        FROST_SELF_ARMOR_DURABILITY = CONFIG.getOrDefault("frost_self_armor_durability", 1);
        FROST_SELF_CONDITIONS = CONFIG.getOrDefault("frost_self_conditions", 3);

        //Allies
        FROST_ALLIES_ALLY_LOW_HEALTH = CONFIG.getOrDefault("frost_allies_ally_low_health", 4);
        FROST_ALLIES_VERY_LOW_HEALTH = CONFIG.getOrDefault("frost_allies_very_low_health", 1);
        FROST_ALLIES_SURROUNDED = CONFIG.getOrDefault("frost_allies_surrounded", 1);
        FROST_ALLIES_FREEZING = CONFIG.getOrDefault("frost_allies_freezing", 1);
        FROST_ALLIES_ALLY_FREEZING = CONFIG.getOrDefault("frost_allies_ally_freezing", 1);
        FROST_ALLIES_ALLY_ARMOR_DURABILITY = CONFIG.getOrDefault("frost_allies_ally_armor_durability", 1);
        FROST_ALLIES_CONDITIONS = CONFIG.getOrDefault("frost_allies_conditions", 3);

        //V2
        FROST_TRIGGER_ITEMS = CONFIG.getOrDefault("frost_trigger_items", ice_items);
        FROST_TRIGGER_BLOCKS = CONFIG.getOrDefault("frost_trigger_blocks", ice_blocks);

        //==========================Earthen==========================
        //Variant
        EARTHEN_VARIANT_ALLY_LOW_HEALTH = CONFIG.getOrDefault("earthen_variant_allies_low_healthy", 3);
        EARTHEN_VARIANT_VERY_LOW_HEALTH = CONFIG.getOrDefault("earthen_variant_very_low_health", 4);
        EARTHEN_VARIANT_SURROUNDED = CONFIG.getOrDefault("earthen_variant_surrounded", 1);
        EARTHEN_VARIANT_CONDITIONS = CONFIG.getOrDefault("earthen_variant_conditions", 3);

        //Enemies
        EARTHEN_ENEMIES_ALLY_LOW_HEALTH = CONFIG.getOrDefault("earthen_enemies_ally_low_health", 3);
        EARTHEN_ENEMIES_VERY_LOW_HEALTH = CONFIG.getOrDefault("earthen_enemies_very_low_health", 4);
        EARTHEN_ENEMIES_SURROUNDED = CONFIG.getOrDefault("earthen_enemies_surrounded", 1);
        EARTHEN_ENEMIES_CONDITIONS = CONFIG.getOrDefault("earthen_enemies_conditions", 3);

        //Self
        EARTHEN_SELF_VERY_LOW_HEALTH = CONFIG.getOrDefault("earthen_self_very_low_health", 3);
        EARTHEN_SELF_LOW_HEALTH = CONFIG.getOrDefault("earthen_self_low_health", 2);
        EARTHEN_SELF_SURROUNDED = CONFIG.getOrDefault("earthen_self_surrounded", 2);
        EARTHEN_SELF_CONDITIONS = CONFIG.getOrDefault("earthen_self_conditions", 3);

        //Allies
        EARTHEN_ALLIES_ALLY_LOW_HEALTH = CONFIG.getOrDefault("earthen_allies_ally_low_health", 4);
        EARTHEN_ALLIES_VERY_LOW_HEALTH = CONFIG.getOrDefault("earthen_allies_very_low_health", 1);
        EARTHEN_ALLIES_SURROUNDED = CONFIG.getOrDefault("earthen_allies_surrounded", 1);
        EARTHEN_ALLIES_CONDITIONS = CONFIG.getOrDefault("earthen_allies_conditions", 3);

        //==========================Wind==========================
        //All
        WIND_ALL_VERY_LOW_HEALTH = CONFIG.getOrDefault("wind_all_very_low_health", 4);
        WIND_ALL_SURROUNDED = CONFIG.getOrDefault("wind_all_surrounded", 1);
        WIND_ALL_FALLING = CONFIG.getOrDefault("wind_all_falling", 1);
        WIND_ALL_FALLING_HIGH = CONFIG.getOrDefault("wind_all_falling_high", 3);
        WIND_ALL_CONDITIONS = CONFIG.getOrDefault("wind_all_conditions", 3);

        //SELF
        WIND_SELF_VERY_LOW_HEALTH = CONFIG.getOrDefault("wind_self_very_low_health", 4);
        WIND_SELF_LOW_HEALTH = CONFIG.getOrDefault("wind_self_low_health", 2);
        WIND_SELF_SURROUNDED = CONFIG.getOrDefault("wind_self_surrounded", 1);
        WIND_SELF_FALLING = CONFIG.getOrDefault("wind_self_falling", 1);
        WIND_SELF_FALLING_HIGH = CONFIG.getOrDefault("wind_self_falling_high", 3);
        WIND_SELF_CONDITIONS = CONFIG.getOrDefault("wind_self_conditions", 3);

        //Allies
        WIND_ALLIES_ALLY_LOW_HEALTH = CONFIG.getOrDefault("wind_allies_ally_low_health", 4);
        WIND_ALLIES_VERY_LOW_HEALTH = CONFIG.getOrDefault("wind_allies_very_low_health", 1);
        WIND_ALLIES_SURROUNDED = CONFIG.getOrDefault("wind_allies_surrounded", 1);
        WIND_ALLIES_FALLING_HIGH = CONFIG.getOrDefault("wind_allies_ally_falling", 2);
        WIND_ALLIES_ALLY_FALLING_HIGH = CONFIG.getOrDefault("wind_allies_ally_falling_high", 4);
        WIND_ALLIES_ALLY_FALLING = CONFIG.getOrDefault("wind_allies_falling", 2);
        WIND_ALLIES_CONDITIONS = CONFIG.getOrDefault("wind_allies_conditions", 3);

        //V2
        WIND_TRIGGER_BLOCKS = CONFIG.getOrDefault("wind_trigger_blocks", air_blocks);

        //==========================Aqua==========================
        //All
        AQUA_ALL_VERY_LOW_HEALTH = CONFIG.getOrDefault("aqua_all_very_low_health", 4);
        AQUA_ALL_SURROUNDED = CONFIG.getOrDefault("aqua_all_surrounded", 1);
        AQUA_ALL_DROWNING = CONFIG.getOrDefault("aqua_all_drowning", 1);
        AQUA_ALL_ALLY_ARMOR_DURABILITY = CONFIG.getOrDefault("aqua_all_ally_armor_durability", 1);
        AQUA_ALL_CONDITIONS = CONFIG.getOrDefault("aqua_all_conditions", 3);

        //Enemies
        AQUA_ENEMIES_VERY_LOW_HEALTH = CONFIG.getOrDefault("aqua_enemies_very_low_health", 3);
        AQUA_ENEMIES_LOW_HEALTH = CONFIG.getOrDefault("aqua_enemies_low_health", 2);
        AQUA_ENEMIES_SURROUNDED = CONFIG.getOrDefault("aqua_enemies_surrounded", 1);
        AQUA_ENEMIES_DROWNING = CONFIG.getOrDefault("aqua_enemies_drowning", 1);
        AQUA_ENEMIES_ALLY_ARMOR_DURABILITY = CONFIG.getOrDefault("aqua_enemies_ally_armor_durability", 1);
        AQUA_ENEMIES_CONDITIONS = CONFIG.getOrDefault("aqua_enemies_conditions", 3);

        //SELF
        AQUA_SELF_VERY_LOW_HEALTH = CONFIG.getOrDefault("aqua_self_very_low_health", 4);
        AQUA_SELF_LOW_HEALTH = CONFIG.getOrDefault("aqua_self_low_health", 2);
        AQUA_SELF_SURROUNDED = CONFIG.getOrDefault("aqua_self_armor_durability", 1);
        AQUA_SELF_ARMOR_DURABILITY = CONFIG.getOrDefault("aqua_self_surrounded", 1);
        AQUA_SELF_DROWNING = CONFIG.getOrDefault("aqua_self_drowning", 1);
        AQUA_SELF_CONDITIONS = CONFIG.getOrDefault("aqua_self_conditions", 3);

        //Allies
        AQUA_ALLIES_ALLY_LOW_HEALTH = CONFIG.getOrDefault("aqua_allies_ally_low_health", 4);
        AQUA_ALLIES_VERY_LOW_HEALTH = CONFIG.getOrDefault("aqua_allies_very_low_health", 1);
        AQUA_ALLIES_SURROUNDED = CONFIG.getOrDefault("aqua_allies_surrounded", 1);
        AQUA_ALLIES_DROWNING = CONFIG.getOrDefault("aqua_allies_drowning", 1);
        AQUA_ALLIES_ALLY_DROWNING = CONFIG.getOrDefault("aqua_allies_ally_drowning", 1);
        AQUA_ALLIES_ALLY_ARMOR_DURABILITY = CONFIG.getOrDefault("aqua_allies_ally_armor_durability", 1);
        AQUA_ALLIES_CONDITIONS = CONFIG.getOrDefault("aqua_allies_conditions", 3);

        //V2
        AQUA_TRIGGER_ITEMS = CONFIG.getOrDefault("aqua_trigger_items", aqua_items);
        AQUA_TRIGGER_BLOCKS = CONFIG.getOrDefault("aqua_trigger_blocks", aqua_blocks);

    }
}

