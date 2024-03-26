package me.emafire003.dev.lightwithin.commands.client;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;


//Based on Factions' code https://github.com/ickerio/factions (MIT license)
public interface ClientLightCommand {
    LiteralCommandNode<FabricClientCommandSource> getNode();


}
