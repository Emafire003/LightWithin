package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.permissions.PermissionsChecker;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

public class GetLightCommand implements LightCommand{

    private int getType(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            InnerLightType type = LightWithin.LIGHT_COMPONENT.get(target).getType();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight type of §d" + target.getName().getString() + "§e is: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(type.toString()).formatted(Formatting.GREEN))), true);
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(() -> Text.literal("Error: " + e.toString()),false);
            return 0;
        }

    }

    private int getTarget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            TargetType type = LightWithin.LIGHT_COMPONENT.get(target).getTargets();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The TargetType of §d" + target.getName().getString() + "§e is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal(type.toString()).formatted(Formatting.GREEN))), true);
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(() -> Text.literal("Error: " + e.toString()),false);
            return 0;
        }

    }

    private int getPower(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            double power = LightWithin.LIGHT_COMPONENT.get(target).getPowerMultiplier();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The power multiplier of §d" + target.getName().getString() + "§e is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal("§a"+power))), true);
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(() -> Text.literal("Error: " + e.toString()),false);
            return 0;
        }
    }

    private int getDuration(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            int duration = LightWithin.LIGHT_COMPONENT.get(target).getDuration();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The duration of §d" + target.getName().getString() + "§e is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal("§a"+duration))), true);
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(() -> Text.literal("Error: " + e.toString()),false);
            return 0;
        }
    }

    private int getMaxCooldown(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            int cooldown = LightWithin.LIGHT_COMPONENT.get(target).getMaxCooldown();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The max cooldown of §d" + target.getName().getString() + "§e is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal("§a"+cooldown))), true);
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(() -> Text.literal("Error: " + e.toString()),false);
            return 0;
        }
    }

    private int getLocked(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            boolean locked = LightWithin.LIGHT_COMPONENT.get(target).getLocked();
            if(!locked){
                source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The light for §d" + target.getName().getString() + "§e is: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal("§aUnlocked"))), true);
            }else{
                source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The light for §d" + target.getName().getString() + "§e is: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal("§cLocked"))), true);
            }
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(() -> Text.literal("Error: " + e.toString()),false);
            return 0;
        }
    }

    private int getCooldown(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            Map<StatusEffect, StatusEffectInstance> effect_map = target.getActiveStatusEffects();
            if(effect_map.containsKey(LightEffects.LIGHT_FATIGUE)){
                int cooldown = effect_map.get(LightEffects.LIGHT_FATIGUE).getDuration()/20;
                source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The current cooldown of §d" + target.getName().getString() + "§e is: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal("§a"+cooldown))), true);
            }else{
                source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§d " + target.getName().getString() + "§eis not in cooldown" ).formatted(Formatting.YELLOW)), true);
            }

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(() -> Text.literal("Error: " + e.toString()),false);
            return 0;
        }
    }

    private int getAll(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            InnerLightType type = LightWithin.LIGHT_COMPONENT.get(target).getType();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight type of §d" + target.getName().getString() + "§e is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal(type.toString()).formatted(Formatting.GREEN))), true);
            TargetType target_type = LightWithin.LIGHT_COMPONENT.get(target).getTargets();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The TargetType is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal(target_type.toString()).formatted(Formatting.GREEN))), true);
            double power = LightWithin.LIGHT_COMPONENT.get(target).getPowerMultiplier();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The power multiplier is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal("§a"+power))), true);
            int duration = LightWithin.LIGHT_COMPONENT.get(target).getDuration();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The duration is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal("§a"+duration))), true);
            int mcooldown = LightWithin.LIGHT_COMPONENT.get(target).getMaxCooldown();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The max cooldown is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal("§a"+mcooldown))), true);
            boolean locked = LightWithin.LIGHT_COMPONENT.get(target).getLocked();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Locked: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal("§a"+locked))), true);

            Map<StatusEffect, StatusEffectInstance> effect_map = target.getActiveStatusEffects();
            if(effect_map.containsKey(LightEffects.LIGHT_FATIGUE)){
                int cooldown = effect_map.get(LightEffects.LIGHT_FATIGUE).getDuration()/20;
                source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The current cooldown is: ").formatted(Formatting.YELLOW).append(Text.literal("§a"+cooldown))), true);
            }else{
                source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§d" + target.getName().getString() + "§e is not in cooldown" ).formatted(Formatting.YELLOW)), true);
            }

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(() -> Text.literal("Error: " + e.toString()),false);
            return 0;
        }
    }

    private int getLights(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();

        try{
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The light-types available at this moment are: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal(SetLightCommand.currently_usable_lights.toString()).formatted(Formatting.GREEN))), true);
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(() -> Text.literal("Error: " + e.toString()),false);
            return 0;
        }
    }

    public LiteralCommandNode<ServerCommandSource> getNode() {
        return CommandManager
                .literal("get")
                .requires(PermissionsChecker.hasPerms("lightwithin.commands.get", 2))
                .then(
                        CommandManager
                                .literal("all")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(this::getAll)

                                )
                )
                .then(
                        CommandManager
                                .literal("lights").executes(this::getLights)
                )
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
                .then(
                        CommandManager
                                .literal("power")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(this::getPower)


                                )
                )
                .then(
                        CommandManager
                                .literal("duration")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(this::getDuration)


                                )
                )
                .then(
                        CommandManager
                                .literal("cooldown")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(this::getCooldown)


                                )
                )
                .then(
                        CommandManager
                                .literal("maxcooldown")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(this::getMaxCooldown)


                                )
                )
                .then(
                        CommandManager
                                .literal("locked")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(this::getLocked)


                                )
                )
                .build();
    }
}
