package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.emafire003.dev.lightwithin.client.EventHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class SetDimension {

    @SuppressWarnings("all")
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean b) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder) CommandManager.literal("setdimensions").requires((source) -> {
            return source.hasPermissionLevel(2);
        })).then(CommandManager.argument("x",  IntegerArgumentType.integer(1)).then(((RequiredArgumentBuilder)CommandManager.argument("y",  IntegerArgumentType.integer(1)).executes((context) -> {
            return execute((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger(context, "x"), IntegerArgumentType.getInteger(context, "y"));
        })))));
    }
    private static int execute(ServerCommandSource source, int x, int y) throws CommandSyntaxException {
        EventHandler.x = x;
        EventHandler.y = y;
        return 1;
    }
}
