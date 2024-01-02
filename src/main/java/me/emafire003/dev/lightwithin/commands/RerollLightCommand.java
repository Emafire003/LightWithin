package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.events.LightTriggeringAndEvents;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.UUID;

public class RerollLightCommand implements LightCommand{


    private int rerollTypeTarget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();
        try {
            for(ServerPlayerEntity target : targets){
                LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);

                Pair<InnerLightType, TargetType> current = new Pair<>(component.getType(), component.getTargets());
                Pair<InnerLightType, TargetType> newone = LightTriggeringAndEvents.determineTypeAndTarget(UUID.randomUUID().toString().toLowerCase().split("-"), 1,3);

                while(current.getFirst().equals(newone.getFirst())){
                    newone = LightTriggeringAndEvents.determineTypeAndTarget(UUID.randomUUID().toString().toLowerCase().split("-"), 1,3);
                }

                InnerLightType type = newone.getFirst();
                TargetType targets_new = newone.getSecond();
                component.setType(newone.getFirst());
                component.setTargets(newone.getSecond());

                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§eThe new light type and target type for " + target.getName().getString() + " are: §a" + type + " §eand §a" + targets_new)), false);

            }
        }catch (Exception e){
            e.printStackTrace();
            source.sendError(Text.literal(LightWithin.PREFIX_MSG).append(Text.literal("There was an error while rerolling. Check the logs for more information")));
        }

        return  1;
    }

    private int rerollPower(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);

            component.setPowerMultiplier(LightTriggeringAndEvents.determinePower(UUID.randomUUID().toString().toLowerCase().split("-"), 4));

            source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§eThe new power multiplier of " + target.getName().getString() + " is: §a" + component.getPowerMultiplier())), false);

        }
        return  1;
    }

    private int rerollDuration(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);

            component.setDuration(LightTriggeringAndEvents.determineDuration(UUID.randomUUID().toString().toLowerCase().split("-"), 2));

            source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§eThe new duration of " + target.getName().getString() + " is: §a" + component.getDuration())), false);

        }
        return  1;
    }

    private int rerollCooldown(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);

            component.setMaxCooldown(LightTriggeringAndEvents.determineCooldown(UUID.randomUUID().toString().toLowerCase().split("-"), 0));

            source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§eThe new max cooldown of " + target.getName().getString() + " is: §a" + component.getMaxCooldown())), false);

        }
        return  1;
    }

    private int rerollAll(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        rerollCooldown(context);
        rerollDuration(context);
        rerollPower(context);
        rerollTypeTarget(context);
        return  1;
    }



    public LiteralCommandNode<ServerCommandSource> getNode() {
        return CommandManager
                .literal("reroll")
                .then(
                        CommandManager.argument("player", EntityArgumentType.players()).then(
                                CommandManager.literal("all")
                                        .executes(this::rerollAll)
                        )

                )
                .then(
                        CommandManager.argument("player", EntityArgumentType.players()).then(
                                CommandManager.literal("power")
                                        .executes(this::rerollPower)
                        )
                                
                )
                .then(
                        CommandManager.argument("player", EntityArgumentType.players()).then(
                                CommandManager.literal("duration")
                                        .executes(this::rerollDuration)
                        )

                )
                .then(
                        CommandManager.argument("player", EntityArgumentType.players()).then(
                                CommandManager.literal("typeTarget")
                                        .executes(this::rerollTypeTarget)
                        )

                )
                .then(
                        CommandManager.argument("player", EntityArgumentType.players()).then(
                                CommandManager.literal("cooldown")
                                        .executes(this::rerollCooldown)
                        )

                )
                .build();
    }
}
