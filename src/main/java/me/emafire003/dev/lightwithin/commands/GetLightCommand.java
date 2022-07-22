package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GetLightCommand implements LightCommand{

    private int getType(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            InnerLightType type = LightWithin.LIGHT_COMPONENT.get(target).getType();
            source.sendFeedback(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight type of §d" + target.getName().getString() + "§e is: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(type.toString()).formatted(Formatting.GREEN))), true);
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(Text.literal("Error: " + e.toString()),false);
            return 0;
        }

    }

    private int getTarget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            TargetType type = LightWithin.LIGHT_COMPONENT.get(target).getTargets();
            source.sendFeedback(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The TargetType of §d" + target.getName().getString() + "§e is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal(type.toString()).formatted(Formatting.GREEN))), true);
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(Text.literal("Error: " + e.toString()),false);
            return 0;
        }

    }


    public LiteralCommandNode<ServerCommandSource> getNode() {
        return CommandManager
                .literal("get")
                .then(
                        CommandManager
                                .literal("type")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(this::getType)

                                )
                )
                .then(
                        CommandManager
                                .literal("target")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(this::getTarget)


                                )
                )
                .build();
    }
}
