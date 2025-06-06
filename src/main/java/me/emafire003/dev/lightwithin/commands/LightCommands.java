package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.commands.arguments.LightTargetArgument;
import me.emafire003.dev.lightwithin.commands.arguments.LightTypeArgument;
import me.emafire003.dev.lightwithin.commands.arguments.LuxdialogueStateArgument;
import me.emafire003.dev.lightwithin.compat.permissions.PermissionsChecker;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class LightCommands {

    public static void registerArguments(){
        ArgumentTypeRegistry.registerArgumentType(LightWithin.getIdentifier("light_type"), LightTypeArgument.class, ConstantArgumentSerializer.of(LightTypeArgument::lightType));
        ArgumentTypeRegistry.registerArgumentType(LightWithin.getIdentifier("light_target"), LightTargetArgument.class, ConstantArgumentSerializer.of(LightTargetArgument::lightTarget));
        ArgumentTypeRegistry.registerArgumentType(LightWithin.getIdentifier("lux_dialogue_state"), LuxdialogueStateArgument.class, ConstantArgumentSerializer.of(LuxdialogueStateArgument::state));

    }

    //Based on Factions' code https://github.com/ickerio/factions
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        LiteralCommandNode<ServerCommandSource> lightcommands = CommandManager
                .literal("light")
                .build();

        LiteralCommandNode<ServerCommandSource> alias = CommandManager
                .literal("lightwithin")
                .requires(PermissionsChecker.hasPerms("lightwithin.commands", 2))
                .build();

        dispatcher.getRoot().addChild(lightcommands);
        dispatcher.getRoot().addChild(alias);

        LightCommand[] commands = new LightCommand[] {
                new SetLightCommand(),
                new GetLightCommand(),
                new ResetLightCommand(),
                new ActivateLightCommand(),
                new RerollLightCommand(),
                new ReloadConfigCommand(),
                new ReadyLightCommand(),
                new ChargeCommand(),
                new SpoofLightCommand()
        };

        ActivateLightCommand.createScheduler();
        ReadyLightCommand.createScheduler();

        for (LightCommand command : commands) {
            lightcommands.addChild(command.getNode());
            alias.addChild(command.getNode());
        }
    }
}
