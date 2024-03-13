package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.permissions.PermissionsChecker;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.events.LightCreationAndEvent;
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
                Pair<InnerLightType, TargetType> newone = LightCreationAndEvent.determineTypeAndTarget(UUID.randomUUID().toString().toLowerCase().split("-"), LightCreationAndEvent.TYPE_BIT,LightCreationAndEvent.TARGET_BIT);

                while(current.getFirst().equals(newone.getFirst())){
                    newone = LightCreationAndEvent.determineTypeAndTarget(UUID.randomUUID().toString().toLowerCase().split("-"), LightCreationAndEvent.TYPE_BIT,LightCreationAndEvent.TARGET_BIT);
                }

                InnerLightType type = newone.getFirst();
                TargetType targets_new = newone.getSecond();
                component.setType(newone.getFirst());
                component.setTargets(newone.getSecond());

                if(Config.TARGET_FEEDBACK){
                    target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The target type, and the type of your InnerLight have been changed, your new ones are: " ).formatted(Formatting.YELLOW)
                            .append(Text.literal(type.toString()).formatted(Formatting.GREEN)).append(" and ").formatted(Formatting.YELLOW).append(Text.literal(targets_new.toString()).formatted(Formatting.GREEN))));
                }
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§eThe new light type and target type for " + target.getName().getString() + " are: §a" + type + " §eand §a" + targets_new)), false);

            }
        }catch (Exception e){
            e.printStackTrace();
            source.sendError(Text.literal(LightWithin.PREFIX_MSG).append(Text.literal("There was an error while rerolling. Check the logs for more information")));
        }

        return  1;
    }


    private int rerollTarget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();
        try {
            for(ServerPlayerEntity target : targets){
                LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);

                TargetType current = component.getTargets();
                TargetType newone = LightCreationAndEvent.determineTarget(UUID.randomUUID().toString().toLowerCase().split("-"), 3, LightWithin.POSSIBLE_TARGETS.get(component.getType()));

                while(current.equals(newone)){
                    newone = LightCreationAndEvent.determineTarget(UUID.randomUUID().toString().toLowerCase().split("-"), 3, LightWithin.POSSIBLE_TARGETS.get(component.getType()));
                }


                TargetType targets_new = newone;
                component.setTargets(newone);

                if(Config.TARGET_FEEDBACK){
                    target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The target type of your InnerLight has been changed, your new one is: " ).formatted(Formatting.YELLOW)
                            .append(Text.literal(targets_new.toString()).formatted(Formatting.GREEN))));
                }
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§eThe new target type for " + target.getName().getString() + " is: §a" + targets_new)), false);

            }
        }catch (Exception e){
            e.printStackTrace();
            source.sendError(Text.literal(LightWithin.PREFIX_MSG).append(Text.literal("There was an error while rerolling. Check the logs for more information")));
        }

        return  1;
    }

    private int rerollType(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();
        try {
            for(ServerPlayerEntity target : targets){
                LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);

                Pair<InnerLightType, TargetType> current = new Pair<>(component.getType(), component.getTargets());
                Pair<InnerLightType, TargetType> newone = LightCreationAndEvent.determineTypeAndTarget(UUID.randomUUID().toString().toLowerCase().split("-"), LightCreationAndEvent.TYPE_BIT,LightCreationAndEvent.TARGET_BIT);

                while(current.getFirst().equals(newone.getFirst())){
                    newone = LightCreationAndEvent.determineTypeAndTarget(UUID.randomUUID().toString().toLowerCase().split("-"), LightCreationAndEvent.TYPE_BIT,LightCreationAndEvent.TARGET_BIT);
                }

                InnerLightType type = newone.getFirst();
                component.setType(newone.getFirst());
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§eThe new light type for " + target.getName().getString() + " is: §a" + type )), false);
                if(Config.TARGET_FEEDBACK){
                    target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The type of your InnerLight has been changed, your new one is: " ).formatted(Formatting.YELLOW)
                            .append(Text.literal(type.toString()).formatted(Formatting.GREEN))));
                }

                if(!LightWithin.POSSIBLE_TARGETS.get(type).contains(current.getSecond())){
                    TargetType targets_new = newone.getSecond();
                    component.setTargets(newone.getSecond());
                    if(Config.TARGET_FEEDBACK){
                        target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The target type of your InnerLight has been changed, your new one is: " ).formatted(Formatting.YELLOW)
                                .append(Text.literal(targets_new.toString()).formatted(Formatting.GREEN))));
                    }
                    source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§eThe new target type for " + target.getName().getString() + " is: §a" + targets_new )), false);
                }




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

            int power = LightCreationAndEvent.determinePower(UUID.randomUUID().toString().toLowerCase().split("-"), LightCreationAndEvent.POWER_BIT);
            component.setPowerMultiplier(power);

            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The power multiplier of your InnerLight has been changed, your new one is: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(power)).formatted(Formatting.GREEN))));
            }
            source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§eThe new power multiplier of " + target.getName().getString() + " is: §a" + component.getPowerMultiplier())), false);

        }
        return  1;
    }

    private int rerollDuration(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);

            int duration = LightCreationAndEvent.determineDuration(UUID.randomUUID().toString().toLowerCase().split("-"), LightCreationAndEvent.DURATION_BIT);
            component.setDuration(duration);

            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The duration of your InnerLight has been changed, your new one is: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(duration)).formatted(Formatting.GREEN))));
            }
            source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§eThe new duration of " + target.getName().getString() + " is: §a" + component.getDuration())), false);

        }
        return  1;
    }

    private int rerollCooldown(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);

            int cooldown = LightCreationAndEvent.determineCooldown(UUID.randomUUID().toString().toLowerCase().split("-"), LightCreationAndEvent.COOLDOWN_BIT);
            component.setMaxCooldown(cooldown);

            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The max cooldown of your InnerLight has been changed, your new one is: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(cooldown)).formatted(Formatting.GREEN))));
            }
            source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§eThe new max cooldown of " + target.getName().getString() + " is: §a" + component.getMaxCooldown())), false);

        }
        return  1;
    }

    private int rerollLightCharges(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);

            int lightCharges = LightCreationAndEvent.determineMaxLightStack(UUID.randomUUID().toString().toLowerCase().split("-"), LightCreationAndEvent.COOLDOWN_BIT);
            component.setMaxLightStack(lightCharges);

            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of max light charges of your InnerLight has been changed, your new one is: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(lightCharges)).formatted(Formatting.GREEN))));
            }
            source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§eThe new number of max light charges of " + target.getName().getString() + " is: §a" + lightCharges)), false);

        }
        return  1;
    }

    private int rerollAll(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        rerollCooldown(context);
        rerollDuration(context);
        rerollPower(context);
        rerollTypeTarget(context);
        rerollLightCharges(context);
        return  1;
    }



    public LiteralCommandNode<ServerCommandSource> getNode() {
        return CommandManager
                .literal("reroll")
                .requires(PermissionsChecker.hasPerms("lightwithin.commands.reroll", 2))
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
                                CommandManager.literal("type")
                                        .executes(this::rerollType)
                        )

                ).then(
                        CommandManager.argument("player", EntityArgumentType.players()).then(
                                CommandManager.literal("target")
                                        .executes(this::rerollTarget)
                        )

                )
                .then(
                        CommandManager.argument("player", EntityArgumentType.players()).then(
                                CommandManager.literal("cooldown")
                                        .executes(this::rerollCooldown)
                        )
                )
                .then(
                        CommandManager.argument("player", EntityArgumentType.players()).then(
                                CommandManager.literal("lightCharges")
                                        .executes(this::rerollLightCharges)
                        )
                )
                .build();
    }
}
