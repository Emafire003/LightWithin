package me.emafire003.dev.lightwithin.compat.yacl;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import me.emafire003.dev.lightwithin.config.ClientConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


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

        // The xy positions of the icons
        options.add(
                Option.<Integer>createBuilder() // boolean is the type of option we'll be making
                        .name(Text.translatable("config.lightwithin.light_active_icon_x"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.light_active_icon_x.tooltip")))
                        .binding(
                                ClientConfig.light_icon_default_position, // the default value
                                () -> ClientConfig.LIGHT_ACTIVE_ICON_X, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.LIGHT_ACTIVE_ICON_X = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, MinecraftClient.getInstance().getWindow().getScaledWidth())
                                .step(1))
                        .build()
        );

        options.add(
                Option.<Integer>createBuilder() // boolean is the type of option we'll be making
                        .name(Text.translatable("config.lightwithin.light_active_icon_y"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.light_active_icon_y.tooltip")))
                        .binding(
                                ClientConfig.light_icon_default_position, // the default value
                                () -> ClientConfig.LIGHT_ACTIVE_ICON_Y, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.LIGHT_ACTIVE_ICON_Y = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, MinecraftClient.getInstance().getWindow().getScaledHeight())
                                .step(1))
                        .build()
        );

        options.add(
                Option.<Integer>createBuilder() // boolean is the type of option we'll be making
                        .name(Text.translatable("config.lightwithin.light_charge_icon_x"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.light_charge_icon_x.tooltip")))
                        .binding(
                                ClientConfig.light_icon_default_position, // the default value
                                () -> ClientConfig.LIGHT_CHARGE_ICON_X, // a field to get the current value from
                                newVal -> {
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
                Option.<Integer>createBuilder() // boolean is the type of option we'll be making
                        .name(Text.translatable("config.lightwithin.light_charge_icon_y"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.light_charge_icon_y.tooltip")))
                        .binding(
                                ClientConfig.light_icon_default_position, // the default value
                                () -> ClientConfig.LIGHT_CHARGE_ICON_Y, // a field to get the current value from
                                newVal -> {
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
                Option.<Double>createBuilder() // boolean is the type of option we'll be making
                        .name(Text.translatable("config.lightwithin.light_active_scale_factor"))
                        .description(OptionDescription.of(Text.translatable("config.lightwithin.light_active_scale_factor.tooltip")))
                        .binding(
                                ClientConfig.light_icon_default_scale, // the default value
                                () -> ClientConfig.LIGHT_ACTIVE_SCALE_FACTOR, // a field to get the current value from
                                newVal -> {
                                    ClientConfig.LIGHT_ACTIVE_SCALE_FACTOR = newVal;
                                    ClientConfig.saveToFile();
                                }
                        )
                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                .range((double) 0, 10.0)
                                .step(0.1))
                        .build()
        );

        options.add(
                Option.<Double>createBuilder() // boolean is the type of option we'll be making
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
                Option.<Boolean>createBuilder() // boolean is the type of option we'll be making
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
                Option.<Boolean>createBuilder() // boolean is the type of option we'll be making
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
                Option.<Boolean>createBuilder() // boolean is the type of option we'll be making
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

        return options;
    }


}
