package me.emafire003.dev.lightwithin.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class ClientLightCommands {

    //Based on Factions' code https://github.com/ickerio/factions
    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        LiteralCommandNode<FabricClientCommandSource> lightcommands = ClientCommandManager
                .literal("light_client")
                .build();

        LiteralCommandNode<FabricClientCommandSource> alias = ClientCommandManager
                .literal("lightwithin_client")
                .build();

        dispatcher.getRoot().addChild(lightcommands);
        dispatcher.getRoot().addChild(alias);

        ClientLightCommand[] commands = new ClientLightCommand[] {
                new ReloadClientConfigCommand(),
                new TestAllScreensCommand()
        };

        for (ClientLightCommand command : commands) {
            lightcommands.addChild(command.getNode());
            alias.addChild(command.getNode());
        }
    }

}
