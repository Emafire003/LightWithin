package me.emafire003.dev.lightwithin.commands;

import me.emafire003.dev.coloredglowlib.command.SetGlowingColor;
import me.emafire003.dev.coloredglowlib.command.SetTypeGlowingColor;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import static me.emafire003.dev.coloredglowlib.ColoredGlowLib.LOGGER;

public class LWCommandRegister {
    public static void registerCommands() {
        LOGGER.info("Registering commands...");
        CommandRegistrationCallback.EVENT.register(SetDimension::register);
        LOGGER.info("Done!");
    }
}
