package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.commands.arguments.LightTargetArgument;
import me.emafire003.dev.lightwithin.commands.arguments.LightTypeArgument;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SetLightCommand implements LightCommand{

    //Temporary, will remove once all lights have been implemented
    private List<InnerLightType> currently_usable_lights = Arrays.asList(InnerLightType.HEAL, InnerLightType.DEFENCE, InnerLightType.STRENGTH, InnerLightType.BLAZING, InnerLightType.FROST);

    private int changeType(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        InnerLightType type = LightTypeArgument.getType(context, "light_type");
        ServerCommandSource source = context.getSource();

        try{
            for(ServerPlayerEntity target : targets){
                if(!currently_usable_lights.contains(type)){
                    source.sendError(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA)
                            .append(Text.literal("Error! The light you have specified does not exists or is not yet available!" ).formatted(Formatting.RED)));
                    return 0;
                }
                LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
                component.setType(type);
                TargetType target_type = component.getTargets();

                List<TargetType> possible = LightWithin.possible_targets.get(type);
                possible.contains(target_type);

                if(!possible.contains(target_type)){
                    //TODO will need to rework this based on the light possibile targets and such
                    int r = target.getRandom().nextBetween(0, possible.size()-1);
                    component.setTargets(possible.get(r));
                }

                if(Config.TARGET_FEEDBACK){
                    target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Your InnerLight type has been changed, your new type is: " ).formatted(Formatting.YELLOW)
                            .append(Text.literal(type.toString()).formatted(Formatting.GREEN))));
                }


                if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                    source.sendFeedback(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight type of ??d" + target.getName().getString() + "??e has been changed to: " ).formatted(Formatting.YELLOW)
                            .append(Text.literal(type.toString()).formatted(Formatting.GREEN))), true);
                }
            }
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(Text.literal("Error: " + e.toString()),false);
            return 0;
        }



    }

    private int changeTarget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        TargetType type = LightTargetArgument.getTarget(context, "light_target");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            InnerLightType light = component.getType();
            List<TargetType> possible = LightWithin.possible_targets.get(light);

            if(!possible.contains(type)){
                source.sendError(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA)
                        .append(Text.literal("Error! The target-type you have specified does not exists or is not available for the target's InnerLight!" ).formatted(Formatting.RED)));
                return 0;
            }
            component.setTargets(type);

            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The target of your InnerLight has been changed, your new TargetType is: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(type.toString()).formatted(Formatting.GREEN))));
            }

            if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                source.sendFeedback(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The target type of ??d" + target.getName().getString() + "??e has been changed to: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(type.toString()).formatted(Formatting.GREEN))), true);
            }
        }
        return 1;
    }

    private int changePower(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        double power = DoubleArgumentType.getDouble(context, "new_power");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            component.setPowerMultiplier(power);

            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The power of your InnerLight has been changed to: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(power)).formatted(Formatting.GREEN))));
            }

            if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                source.sendFeedback(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The power of ??d" + target.getName().getString() + "??e has been changed to: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(power)).formatted(Formatting.GREEN))), true);
            }

        }
        return 1;
    }

    private int changeDuration(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int duration = IntegerArgumentType.getInteger(context, "new_duration");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            component.setDuration(duration);

            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The duration of your InnerLight has been changed to: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(duration)).formatted(Formatting.GREEN))));
            }

            if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                source.sendFeedback(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The duration of ??d" + target.getName().getString() + "??e has been changed to: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(duration)).formatted(Formatting.GREEN))), true);
            }

        }
        return 1;
    }

    private int changeCooldown(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int cooldown = IntegerArgumentType.getInteger(context, "new_cooldown");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            component.setMaxCooldown(cooldown);

            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The max cooldown of your InnerLight has been changed to: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(cooldown)).formatted(Formatting.GREEN))));
            }

            if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                source.sendFeedback(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The max cooldown of ??d" + target.getName().getString() + "??e has been changed to: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(cooldown)).formatted(Formatting.GREEN))), true);
            }

        }
        return 1;
    }


    public LiteralCommandNode<ServerCommandSource> getNode() {
        return CommandManager
                .literal("set")
                .then(
                        CommandManager
                                .literal("type")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .then(
                                                        CommandManager.argument("light_type", LightTypeArgument.lightType())
                                                                .executes(this::changeType)
                                                )

                                )
                )
                .then(
                        CommandManager
                                .literal("target")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .then(
                                                        CommandManager.argument("light_target", LightTargetArgument.lightTarget())
                                                                .executes(this::changeTarget)
                                                )

                                )
                )
                .then(
                        CommandManager
                                .literal("power")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .then(
                                                        CommandManager.argument("new_power", DoubleArgumentType.doubleArg(0, 9))
                                                                .executes(this::changePower)
                                                )

                                )
                )
                .then(
                        CommandManager
                                .literal("duration")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .then(
                                                        CommandManager.argument("new_duration", IntegerArgumentType.integer(0, 9))
                                                                .executes(this::changeDuration)
                                                )

                                )
                )
                .then(
                        CommandManager
                                .literal("cooldown")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .then(
                                                        CommandManager.argument("new_cooldown", IntegerArgumentType.integer(0, 100))
                                                                .executes(this::changeCooldown)
                                                )

                                )
                )
                .build();
    }
}
