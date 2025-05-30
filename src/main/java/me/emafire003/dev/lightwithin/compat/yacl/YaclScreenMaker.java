package me.emafire003.dev.lightwithin.compat.yacl;

import com.mojang.datafixers.util.Pair;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import me.emafire003.dev.lightwithin.config.ClientConfig;
import me.emafire003.dev.lightwithin.util.ScreenPositionsPresets;
import me.emafire003.dev.lightwithin.util.ScreenUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class YaclScreenMaker {

    public static Screen getScreen(Screen parent){

        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("config.lightwithin.name"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("config.lightwithin.category_name"))
                        .tooltip(Text.translatable("config.lightwithin.description"))
                        .options(createOptions())
                        .build())
                .build().generateScreen(parent);
    }


    public static @NotNull Collection<? extends Option<?>> createOptions(){
        List<Option<?>> options = new ArrayList<>();
        AtomicBoolean updatedFromActivePreset = new AtomicBoolean(false);
        AtomicBoolean updatedFromChargePreset = new AtomicBoolean(false);

        // The xy positions of the icons
        options.add(
                Option.<ScreenPositionsPresets>createBuilder()
                        .name(Text.translatable("config.lightwithin.light_ready_icon_preset"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.light_ready_icon_preset.tooltip")))
                        .binding(
                                ScreenPositionsPresets.TOP_LEFT, // the default value
                                () -> ScreenPositionsPresets.valueOf(ClientConfig.LIGHT_READY_PRESET), // a field to get the current value from
                                newVal -> {
                                    Pair<Integer, Integer> xy = ScreenUtils.getXYIcons(newVal, ClientConfig.LIGHT_READY_SCALE_FACTOR);
                                    ClientConfig.LIGHT_READY_ICON_X = xy.getFirst();
                                    ClientConfig.LIGHT_READY_ICON_Y = xy.getSecond();
                                    ClientConfig.LIGHT_READY_PRESET = newVal.name();
                                    ClientConfig.saveToFile();
                                    updatedFromActivePreset.set(true);
                                }
                        )
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ScreenPositionsPresets.class))
                        .build()
        );

        // The xy positions of the icons
        options.add(
                Option.<ScreenPositionsPresets>createBuilder()
                        .name(Text.translatable("config.lightwithin.light_charge_icon_preset"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.light_charge_icon_preset.tooltip")))
                        .binding(
                                ScreenPositionsPresets.TOP_LEFT, // the default value
                                () -> ScreenPositionsPresets.valueOf(ClientConfig.LIGHT_CHARGE_PRESET), // a field to get the current value from
                                newVal -> {
                                    Pair<Integer, Integer> xy = ScreenUtils.getXYIcons(newVal, ClientConfig.LIGHT_CHARGE_SCALE_FACTOR);
                                    ClientConfig.LIGHT_CHARGE_ICON_X = xy.getFirst();
                                    ClientConfig.LIGHT_CHARGE_ICON_Y = xy.getSecond();
                                    ClientConfig.LIGHT_CHARGE_PRESET = newVal.name();
                                    ClientConfig.saveToFile();
                                    updatedFromChargePreset.set(true);
                                }
                        )
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ScreenPositionsPresets.class))
                        .build()
        );

        options.add(
                Option.<Integer>createBuilder()
                        .name(Text.translatable("config.lightwithin.light_ready_icon_x"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.light_ready_icon_x.tooltip")))
                        .binding(
                                ClientConfig.light_icon_default_position, // the default value
                                () -> ClientConfig.LIGHT_READY_ICON_X, // a field to get the current value from
                                newVal -> {
                                    if(updatedFromActivePreset.get()){
                                        return;
                                    }
                                    ClientConfig.LIGHT_READY_ICON_X = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, MinecraftClient.getInstance().getWindow().getScaledWidth())
                                .step(1))
                        .build()
        );

        options.add(
                Option.<Integer>createBuilder()
                        .name(Text.translatable("config.lightwithin.light_ready_icon_y"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.light_ready_icon_y.tooltip")))
                        .binding(
                                ClientConfig.light_icon_default_position, // the default value
                                () -> ClientConfig.LIGHT_READY_ICON_Y, // a field to get the current value from
                                newVal -> {
                                    if(updatedFromActivePreset.get()){
                                        return;
                                    }
                                    ClientConfig.LIGHT_READY_ICON_Y = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, MinecraftClient.getInstance().getWindow().getScaledHeight())
                                .step(1))
                        .build()
        );

        options.add(
                Option.<Integer>createBuilder()
                        .name(Text.translatable("config.lightwithin.light_charge_icon_x"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.light_charge_icon_x.tooltip")))
                        .binding(
                                ClientConfig.light_icon_default_position, // the default value
                                () -> ClientConfig.LIGHT_CHARGE_ICON_X, // a field to get the current value from
                                newVal -> {
                                    if(updatedFromChargePreset.get()){
                                        return;
                                    }
                                    ClientConfig.LIGHT_CHARGE_ICON_X = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, MinecraftClient.getInstance().getWindow().getScaledWidth())
                                .step(1))
                        .build()
        );

        options.add(
                Option.<Integer>createBuilder()
                        .name(Text.translatable("config.lightwithin.light_charge_icon_y"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.light_charge_icon_y.tooltip")))
                        .binding(
                                ClientConfig.light_icon_default_position, // the default value
                                () -> ClientConfig.LIGHT_CHARGE_ICON_Y, // a field to get the current value from
                                newVal -> {
                                    if(updatedFromChargePreset.get()){
                                        return;
                                    }
                                    ClientConfig.LIGHT_CHARGE_ICON_Y = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, MinecraftClient.getInstance().getWindow().getScaledHeight())
                                .step(1))
                        .build()
        );

        options.add(
                Option.<Double>createBuilder()
                        .name(Text.translatable("config.lightwithin.light_ready_scale_factor"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.light_ready_scale_factor.tooltip")))
                        .binding(
                                ClientConfig.light_icon_default_scale, // the default value
                                () -> ClientConfig.LIGHT_READY_SCALE_FACTOR, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.LIGHT_READY_SCALE_FACTOR = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                .range((double) 0, 10.0)
                                .step(0.1))
                        .build()
        );

        options.add(
                Option.<Double>createBuilder()
                        .name(Text.translatable("config.lightwithin.light_charge_scale_factor"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.light_charge_scale_factor.tooltip")))
                        .binding(
                                ClientConfig.light_icon_default_scale, // the default value
                                () -> ClientConfig.LIGHT_CHARGE_SCALE_FACTOR, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.LIGHT_CHARGE_SCALE_FACTOR = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                .range((double) 0, 10.0)
                                .step(0.1))
                        .build()
        );

        options.add(
                Option.<Double>createBuilder()
                        .name(Text.translatable("config.lightwithin.runes_scale_factor"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.runes_scale_factor.tooltip")))
                        .binding(
                                ClientConfig.runes_default_scale, // the default value
                                () -> ClientConfig.RUNES_SCALE_FACTOR, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.RUNES_SCALE_FACTOR = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                .range(0.1, 5.0)
                                .step(0.1))
                        .build()
        );

        options.add(
                Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.lightwithin.hide_light_charge_icon"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.hide_light_charge_icon.tooltip")))
                        .binding(
                                ClientConfig.HIDE_LIGHT_CHARGE_ICON_default, // the default value
                                () -> ClientConfig.HIDE_LIGHT_CHARGE_ICON, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.HIDE_LIGHT_CHARGE_ICON = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build()
        );

        options.add(
                Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.lightwithin.show_charged_player_glow"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.show_charged_player_glow.tooltip")))
                        .binding(
                                ClientConfig.SHOW_CHARGED_PLAYER_GLOW_default, // the default value
                                () -> ClientConfig.SHOW_CHARGED_PLAYER_GLOW, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.SHOW_CHARGED_PLAYER_GLOW = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build()
        );


        options.add(
                Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.lightwithin.showrunes"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.showrunes.tooltip")))
                        .binding(
                                ClientConfig.SHOW_RUNES_default, // the default value
                                () -> ClientConfig.SHOW_RUNES, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.SHOW_RUNES = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build()
        );

        options.add(
                Option.<Integer>createBuilder()
                        .name(Text.translatable("config.lightwithin.showrunes_for"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.showrunes_for.tooltip")))
                        .binding(
                                ClientConfig.SHOW_RUNES_FOR_default, // the default value
                                () -> ClientConfig.SHOW_RUNES_FOR, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.SHOW_RUNES_FOR = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> IntegerFieldControllerBuilder.create(opt)
                                .range(0, 60)
                        )
                        .build()
        );

        options.add(
                Option.<Double>createBuilder()
                        .name(Text.translatable("config.lightwithin.ingredient_and_item_scale"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.ingredient_and_item_scale.tooltip")))
                        .binding(
                                ClientConfig.ingredient_target_scale_default, // the default value
                                () -> ClientConfig.INGREDIENT_TARGET_SCALE, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.INGREDIENT_TARGET_SCALE = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                .range((double) 0, 50.0)
                                .step(0.1))
                        .build()
        );

        options.add(
                Option.<Integer>createBuilder()
                        .name(Text.translatable("config.lightwithin.show_ingredient_target_for"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.show_ingredient_target_for.tooltip")))
                        .binding(
                                ClientConfig.show_ingredient_target_for_default, // the default value
                                () -> ClientConfig.SHOW_INGREDIENT_TARGET_FOR, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.SHOW_INGREDIENT_TARGET_FOR = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> IntegerFieldControllerBuilder.create(opt)
                                .range(0, 60)
                        )
                        .build()
        );

        options.add(
                Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.lightwithin.auto_light_activation"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.auto_light_activation.tooltip")))
                        .binding(
                                ClientConfig.AUTO_LIGHT_ACTIVATION_default, // the default value
                                () -> ClientConfig.AUTO_LIGHT_ACTIVATION, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.AUTO_LIGHT_ACTIVATION = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build()
        );

        options.add(
                Option.<Color>createBuilder()
                        .name(Text.translatable("config.lightwithin.forestaura_enemy_color"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.forestaura_enemy_color.tooltip")))
                        .binding(
                                Color.decode("#"+ClientConfig.FORESTAURA_ENEMY_COLOR_default), // the default value
                                () -> Color.decode("#"+ClientConfig.FORESTAURA_ENEMY_COLOR), // a field to get the current value from
                                newVal -> {
                                    //Alternatively: String.format("%06x", 0xFFFFFF & newVal.getRGB())
                                    ClientConfig.FORESTAURA_ENEMY_COLOR = Integer.toHexString(newVal.getRGB()).substring(2);
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false))
                        .build()
        );

        options.add(
                Option.<Color>createBuilder()
                        .name(Text.translatable("config.lightwithin.forestaura_ally_color"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.forestaura_ally_color.tooltip")))
                        .binding(
                                Color.decode("#"+ClientConfig.FORESTAURA_ALLY_COLOR_default), // the default value
                                () -> Color.decode("#"+ClientConfig.FORESTAURA_ALLY_COLOR), // a field to get the current value from
                                newVal -> {
                                    //Alternatively: String.format("%06x", 0xFFFFFF & newVal.getRGB())
                                    ClientConfig.FORESTAURA_ALLY_COLOR = Integer.toHexString(newVal.getRGB()).substring(2);
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false))
                        .build()
        );
        options.add(
                Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.lightwithin.forestaura_intoxication_shader"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.forestaura_intoxication_shader.tooltip")))
                        .binding(
                                ClientConfig.FORESTAURA_INTOXICATION_SHADER_default, // the default value
                                () -> ClientConfig.FORESTAURA_INTOXICATION_SHADER, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.FORESTAURA_INTOXICATION_SHADER = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build()
        );
        options.add(
                Option.<Boolean>createBuilder()
                        .name(Text.translatable("config.lightwithin.intoxication_shader_warning"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.intoxication_shader_warning.tooltip")))
                        .binding(
                                ClientConfig.INTOXICATION_SHADER_WARNING_default, // the default value
                                () -> ClientConfig.INTOXICATION_SHADER_WARNING, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.INTOXICATION_SHADER_WARNING = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build()
        );
        options.add(
                Option.<Float>createBuilder()
                        .name(Text.translatable("config.lightwithin.luxdialogue.text_scale"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.luxdialogue.text_scale.tooltip")))
                        .binding(
                                ClientConfig.LUXDIALOGYE_TEXT_SCALE_default, // the default value
                                () -> ClientConfig.LUXDIALOGUE_TEXT_SCALE, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.LUXDIALOGUE_TEXT_SCALE = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                .range(0.0f, 2.0f)
                                .step(0.05f))
                        .build()
        );
        options.add(
                Option.<Integer>createBuilder()
                        .name(Text.translatable("config.lightwithin.luxdialogue.close_after"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.luxdialogue.close_after.tooltip")))
                        .binding(
                                ClientConfig.CLOSE_LUXDIALOGUE_SCREEN_AFTER_default, // the default value
                                () -> ClientConfig.CLOSE_LUXDIALOGUE_SCREEN_AFTER, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.CLOSE_LUXDIALOGUE_SCREEN_AFTER = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> IntegerFieldControllerBuilder.create(opt)
                                .min(5)
                        )
                        .build()
        );
        options.add(
                Option.<Float>createBuilder()
                        .name(Text.translatable("config.lightwithin.luxdialogue.bgm_volume"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.luxdialogue.bgm_volume.tooltip")))
                        .binding(
                                ClientConfig.LUXCOGNITA_DREAM_BGM_VOLUME_default, // the default value
                                () -> ClientConfig.LUXCOGNITA_DREAM_BGM_VOLUME, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.LUXCOGNITA_DREAM_BGM_VOLUME = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                .range(0.0f, 1.0f)
                                .step(0.01f))
                        .build()
        );
        options.add(
                Option.<Integer>createBuilder()
                        .name(Text.translatable("config.lightwithin.overlay_text_duration"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.overlay_text_duratio.tooltip")))
                        .binding(
                                ClientConfig.OVERLAY_TEXT_DURATION_default, // the default value
                                () -> ClientConfig.OVERLAY_TEXT_DURATION, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.OVERLAY_TEXT_DURATION = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> IntegerFieldControllerBuilder.create(opt)
                                .range(0, 60)
                        )
                        .build()
        );
        updatedFromActivePreset.set(false);
        return options;
    }


}
