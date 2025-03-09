package me.emafire003.dev.lightwithin.config;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.RendererEventHandler;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class ClientConfig {
    public static SimpleConfig CONFIG;
    private static ConfigProvider configs;

    private static final int ver = 2;
    public static int VERSION;

    public static int LIGHT_READY_ICON_X = 10;
    public static int LIGHT_CHARGE_ICON_X = 10;
    public static int LIGHT_READY_ICON_Y = 10;
    public static int LIGHT_CHARGE_ICON_Y = 10;
    public static double LIGHT_CHARGE_SCALE_FACTOR = 1.0;
    public static double LIGHT_READY_SCALE_FACTOR = 1.0;
    public static double RUNES_SCALE_FACTOR = 1.0;
    public static String LIGHT_READY_PRESET = "TOP_LEFT";
    public static String LIGHT_CHARGE_PRESET = "TOP_LEFT";
    public static double INGREDIENT_TARGET_SCALE = 5.0;
    public static double ingredient_target_scale_default = 5.0;
    public static int SHOW_INGREDIENT_TARGET_FOR = 5;
    public static int show_ingredient_target_for_default = 5;
    public static boolean HIDE_LIGHT_CHARGE_ICON = false;
    public static final boolean HIDE_LIGHT_CHARGE_ICON_default = false;
    public static boolean SHOW_CHARGED_PLAYER_GLOW = true;
    public static final boolean SHOW_CHARGED_PLAYER_GLOW_default = true;
    public static boolean SHOW_RUNES = true;
    public static final boolean SHOW_RUNES_default = true;
    public static int SHOW_RUNES_FOR = 3;
    public static final int SHOW_RUNES_FOR_default = 3;
    public static boolean AUTO_LIGHT_ACTIVATION = false;
    public static boolean AUTO_LIGHT_ACTIVATION_default = false;
    public static String FORESTAURA_ENEMY_COLOR = "560d03";
    public static String FORESTAURA_ENEMY_COLOR_default = "560d03";
    public static String FORESTAURA_ALLY_COLOR = "2ee878";
    public static String FORESTAURA_ALLY_COLOR_default = "2ee878";
    public static boolean FORESTAURA_INTOXICATION_SHADER = true;
    public static boolean FORESTAURA_INTOXICATION_SHADER_default = true;
    public static boolean INTOXICATION_SHADER_WARNING = true;
    public static boolean INTOXICATION_SHADER_WARNING_default = true;

    public static final int light_icon_default_position = 10;
    public static final double light_icon_default_scale = 1.0;
    public static final double runes_default_scale = 0.5;

    private static final String config_name = "_client";

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


    public static void saveToFile(){
        try {
            CONFIG.set("light_ready_icon_x", LIGHT_READY_ICON_X);
            CONFIG.set("light_ready_icon_y", LIGHT_READY_ICON_Y);
            CONFIG.set("light_charge_icon_x", LIGHT_CHARGE_ICON_X);
            CONFIG.set("light_charge_icon_y", LIGHT_CHARGE_ICON_Y);
            CONFIG.set("light_ready_preset", LIGHT_READY_PRESET);
            CONFIG.set("light_charge_preset", LIGHT_CHARGE_PRESET);
            CONFIG.set("light_ready_scale_factor", LIGHT_READY_SCALE_FACTOR);
            CONFIG.set("light_charge_scale_factor", LIGHT_CHARGE_SCALE_FACTOR);
            CONFIG.set("hide_light_charge_icon", HIDE_LIGHT_CHARGE_ICON);
            CONFIG.set("runes_scale_factor", RUNES_SCALE_FACTOR);
            CONFIG.set("show_charged_player_glow", SHOW_CHARGED_PLAYER_GLOW);
            CONFIG.set("show_runes", SHOW_RUNES);
            CONFIG.set("show_runes_for", SHOW_RUNES_FOR);
            CONFIG.set("auto_light_activation", AUTO_LIGHT_ACTIVATION);
            CONFIG.set("ingredient_target_scale", INGREDIENT_TARGET_SCALE);
            CONFIG.set("show_ingredient_target_for", SHOW_INGREDIENT_TARGET_FOR);
            CONFIG.set("forestaura_enemy_color", FORESTAURA_ENEMY_COLOR);
            CONFIG.set("forestaura_ally_color", FORESTAURA_ALLY_COLOR);
            CONFIG.set("forestaura_intoxication_shader", FORESTAURA_INTOXICATION_SHADER);
            CONFIG.set("intoxication_shader_warning", INTOXICATION_SHADER_WARNING);
            CONFIG.update();
            RendererEventHandler.updateFromConfig();
        } catch (IOException e) {
            LOGGER.warn("Could not delete the config file before saving the new one!");
        }
        LOGGER.debug("Config saved to disk correctly");

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
        configs.addKeyValuePair(new Pair<>("comment", "comment"), "This config file lets you modify client rendering options, such as displaying the light ready icon in a different location or hiding it");
        try{
            int max_x = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int max_y = MinecraftClient.getInstance().getWindow().getScaledHeight();
            double scale_factor = MinecraftClient.getInstance().getWindow().getScaleFactor();
            configs.addKeyValuePair(new Pair<>("comment", "comment"), "The maximum x coordinate of your screen seems to be: " + max_x + ", while your y coordinate seems to be: " + max_y + " and your scale factor: " + scale_factor);
        }catch (Exception e){
            configs.addKeyValuePair(new Pair<>("comment", "comment"), "The maximum x and y coordinates of your screen could not be determined. Delete, than reload the config with the '/light_client reload' command to try again!");
        }

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("light_ready_icon_x", light_icon_default_position), "The x coordinate of the light ready icon position on your screen. A value of 0 corresponds to left side.");
        configs.addKeyValuePair(new Pair<>("light_ready_icon_y", light_icon_default_position), "The y coordinate of the light ready icon position on your screen. A value of 0 corresponds to top side.");
        configs.addKeyValuePair(new Pair<>("light_charge_icon_x", light_icon_default_position), "The x coordinate of the light charge icon position on your screen. A value of 0 corresponds to left side.");
        configs.addKeyValuePair(new Pair<>("light_charge_icon_y", light_icon_default_position), "The y coordinate of the light charge icon position on your screen. A value of 0 corresponds to top side.");

        configs.addKeyValuePair(new Pair<>("light_ready_scale_factor", light_icon_default_scale), "Make this number bigger to make the light ready icon bigger, make it smaller to have a smaller light icon!");
        configs.addKeyValuePair(new Pair<>("light_charge_scale_factor", light_icon_default_scale), "Make this number bigger to make the light charge icon bigger, make it smaller to have a smaller light icon!");
        configs.addKeyValuePair(new Pair<>("runes_scale_factor", runes_default_scale), "Make this number bigger to make the light runes bigger, make it smaller to have a smaller rune!");

        configs.addKeyValuePair(new Pair<>("light_ready_preset", "TOP_LEFT"), "The position of the light ready icon using presets, such as CENTER, TOP/BOTTOM LEFT/RIGHT etc. Can only be modified while using YACL.");
        configs.addKeyValuePair(new Pair<>("light_charge_preset", "TOP_LEFT"), "The position of the light charge icon using presets, such as CENTER, TOP/BOTTOM LEFT/RIGHT etc. Can only be modified while using YACL.");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("hide_light_charge_icon", HIDE_LIGHT_CHARGE_ICON_default), "Hide the light charges icon, but still displays the light ready one, or the error one if you do something that's not allowed");
        configs.addKeyValuePair(new Pair<>("show_charged_player_glow", SHOW_CHARGED_PLAYER_GLOW_default), "See players that have light charges ready glow like a GlowSquid");

        configs.addKeyValuePair(new Pair<>("show_runes", SHOW_RUNES_default), "Setting this value to false will disable runes from rendering even in first person");
        configs.addKeyValuePair(new Pair<>("show_runes_for", SHOW_RUNES_FOR_default), "How many seconds should the runes last on screen?");

        configs.addKeyValuePair(new Pair<>("ingredient_target_scale", ingredient_target_scale_default), "Scale of the item or target icons displayed after interacting with the Luxcognita berry");
        configs.addKeyValuePair(new Pair<>("show_ingredient_target_for", show_ingredient_target_for_default), "How many seconds should the item and target icons last on screen?");

        configs.addKeyValuePair(new Pair<>("auto_light_activation", AUTO_LIGHT_ACTIVATION_default), "Setting this value to true will activate your light as soon as it's ready. WARNING: it may be disabled by the server!");

        configs.addKeyValuePair(new Pair<>("forestaura_enemy_color", FORESTAURA_ENEMY_COLOR), "The color that will indicate enemies with the forest aura vision. You must install ColoredGlowLib to see the effect.");
        configs.addKeyValuePair(new Pair<>("forestaura_ally_color", FORESTAURA_ALLY_COLOR), "The color that will indicate allies with the forest aura vision. You must install ColoredGlowLib to see the effect.");
        configs.addKeyValuePair(new Pair<>("forestaura_intoxication_shader", FORESTAURA_INTOXICATION_SHADER), "Accessibility setting. Disable this if you don't want to see the Intoxicated effect shader (may give motion sickness)");
        configs.addKeyValuePair(new Pair<>("intoxication_shader_warning", INTOXICATION_SHADER_WARNING), "Accessibility setting. Turn this off to not see the warning message every time the intoxication effect is applied to you");

    }

    public static void reloadConfig(){
        registerConfigs();
        RendererEventHandler.updateFromConfig();
        LOGGER.info("All " + configs.getConfigsList().size() + " have been reloaded properly");

    }

    private static void assignConfigs() {
        VERSION = CONFIG.getOrDefault("version", ver);

        LIGHT_READY_ICON_X = CONFIG.getOrDefault("light_ready_icon_x", light_icon_default_position);
        LIGHT_READY_ICON_Y = CONFIG.getOrDefault("light_ready_icon_y", light_icon_default_position);
        LIGHT_CHARGE_ICON_X = CONFIG.getOrDefault("light_charge_icon_x", light_icon_default_position);
        LIGHT_CHARGE_ICON_Y = CONFIG.getOrDefault("light_charge_icon_y", light_icon_default_position);
        LIGHT_READY_PRESET = CONFIG.getOrDefault("light_ready_preset", "TOP_LEFT");
        LIGHT_CHARGE_PRESET = CONFIG.getOrDefault("light_charge_preset", "TOP_LEFT");
        LIGHT_READY_SCALE_FACTOR = CONFIG.getOrDefault("light_ready_scale_factor", light_icon_default_scale);
        LIGHT_CHARGE_SCALE_FACTOR = CONFIG.getOrDefault("light_charge_scale_factor", light_icon_default_scale);
        RUNES_SCALE_FACTOR = CONFIG.getOrDefault("runes_scale_factor", runes_default_scale);
        HIDE_LIGHT_CHARGE_ICON = CONFIG.getOrDefault("hide_light_charge_icon", HIDE_LIGHT_CHARGE_ICON_default);
        SHOW_CHARGED_PLAYER_GLOW = CONFIG.getOrDefault("show_charged_player_glow", SHOW_CHARGED_PLAYER_GLOW_default);
        SHOW_RUNES = CONFIG.getOrDefault("show_runes", SHOW_RUNES_default);
        SHOW_RUNES_FOR = CONFIG.getOrDefault("show_runes_for", SHOW_RUNES_FOR_default);
        INGREDIENT_TARGET_SCALE = CONFIG.getOrDefault("ingredient_target_scale", ingredient_target_scale_default);
        SHOW_INGREDIENT_TARGET_FOR = CONFIG.getOrDefault("show_ingredient_target_for", show_ingredient_target_for_default);
        AUTO_LIGHT_ACTIVATION = CONFIG.getOrDefault("auto_light_activation", AUTO_LIGHT_ACTIVATION_default);
        FORESTAURA_ENEMY_COLOR = CONFIG.getOrDefault("forestaura_enemy_color", FORESTAURA_ENEMY_COLOR_default);
        FORESTAURA_ALLY_COLOR = CONFIG.getOrDefault("forestaura_ally_color", FORESTAURA_ALLY_COLOR_default);
        FORESTAURA_INTOXICATION_SHADER = CONFIG.getOrDefault("forestaura_intoxication_shader", FORESTAURA_INTOXICATION_SHADER);
        INTOXICATION_SHADER_WARNING = CONFIG.getOrDefault("intoxication_shader", INTOXICATION_SHADER_WARNING);
    }
}

