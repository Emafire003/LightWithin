package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.DialogueProgressState;
import me.emafire003.dev.lightwithin.commands.arguments.LightTargetArgument;
import me.emafire003.dev.lightwithin.commands.arguments.LightTypeArgument;
import me.emafire003.dev.lightwithin.commands.arguments.LuxdialogueStateArgument;
import me.emafire003.dev.lightwithin.compat.permissions.PermissionsChecker;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SetLightCommand implements LightCommand{

    private int changeType(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        InnerLight type = LightTypeArgument.getType(context, "light_type");
        ServerCommandSource source = context.getSource();

        try{
            for(ServerPlayerEntity target : targets){

                if(!LightWithin.INNERLIGHT_REGISTRY.containsId(type.getLightId())){
                    source.sendError(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA)
                            .append(Text.literal("Error! The light you have specified does not exists or is not yet available!" ).formatted(Formatting.RED)));
                    return 0;
                }
                LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
                component.setType(type);
                TargetType target_type = component.getTargets();

                List<TargetType> possible = type.getPossibleTargetTypes();

                if(!possible.contains(target_type)){
                    int r = target.getRandom().nextBetween(0, possible.size()-1);
                    component.setTargets(possible.get(r));
                }

                if(Config.TARGET_FEEDBACK){
                    target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Your InnerLight type has been changed, your new type is: " ).formatted(Formatting.YELLOW)
                            .append(Text.literal(type.toString()).formatted(Formatting.GREEN))));
                }


                if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                    source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight type of §d" + target.getName().getString() + "§e has been changed to: " ).formatted(Formatting.YELLOW)
                            .append(Text.literal(type.toString()).formatted(Formatting.GREEN))), true);
                }
            }
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback( () -> Text.literal("Error: " + e),false);
            return 0;
        }

    }

    private int changeTarget(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        TargetType type = LightTargetArgument.getTarget(context, "light_target");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            InnerLight light = component.getType();
            List<TargetType> possible = light.getPossibleTargetTypes();

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
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The target type of §d" + target.getName().getString() + "§e has been changed to: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(type.toString()).formatted(Formatting.GREEN))), true);
            }
        }
        return 1;
    }

    private int changePower(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int power = IntegerArgumentType.getInteger(context, "new_power");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            component.setPowerMultiplier(power);

            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The power of your InnerLight has been changed to: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(power)).formatted(Formatting.GREEN))));
            }

            if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The power of §d" + target.getName().getString() + "§e has been changed to: " ).formatted(Formatting.YELLOW)
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
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The duration of §d" + target.getName().getString() + "§e has been changed to: " ).formatted(Formatting.YELLOW)
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
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The max cooldown of §d" + target.getName().getString() + "§e has been changed to: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(cooldown)).formatted(Formatting.GREEN))), true);
            }

        }
        return 1;
    }

    private int changeLightCharges(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int lightCharges = IntegerArgumentType.getInteger(context, "new_light_charges");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            component.setMaxLightStack(lightCharges);

            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of max light charges of your InnerLight has been changed to: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(lightCharges)).formatted(Formatting.GREEN))));
            }

            if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of max light charges of §d" + target.getName().getString() + "§e has been changed to: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(lightCharges)).formatted(Formatting.GREEN))), true);
            }

        }
        return 1;
    }

    private int changeLocked(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        boolean lock = BoolArgumentType.getBool(context, "lock");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            component.setIsLocked(lock);

            if(Config.TARGET_FEEDBACK){
                if(lock){
                    target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Your light was: "  ).formatted(Formatting.YELLOW)
                            .append(Text.literal("Locked!").formatted(Formatting.RED))));
                }else{
                    target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Your light was: " ).formatted(Formatting.YELLOW)
                            .append(Text.literal("Unlocked!").formatted(Formatting.GREEN))));
                    }
            }

            if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                if(lock){
                    source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The light of §d" + target.getName().getString() + "§e has been " ).formatted(Formatting.YELLOW)
                            .append(Text.literal("Unlocked!").formatted(Formatting.GREEN))), true);
                }else{
                    source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The light of §d" + target.getName().getString() + "§e has been " ).formatted(Formatting.YELLOW)
                            .append(Text.literal("Locked!").formatted(Formatting.RED))), true);
                }
            }

        }
        return 1;
    }

    private int changeTriggeredNaturally(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        boolean triggeredNaturally = BoolArgumentType.getBool(context, "triggeredNaturally");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            component.setTriggeredNaturally(triggeredNaturally);

            if(Config.TARGET_FEEDBACK){
                if(triggeredNaturally){
                    target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Your light was set as "  ).formatted(Formatting.YELLOW)
                            .append(Text.literal("Naturally triggered!").formatted(Formatting.GREEN))));
                }else{
                    target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Your light was set as " ).formatted(Formatting.YELLOW)
                            .append(Text.literal("not yet naturally triggered!").formatted(Formatting.RED))));
                }
            }

            if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                if(triggeredNaturally){
                    source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The light of §d" + target.getName().getString() + "§e has been set to " ).formatted(Formatting.YELLOW)
                            .append(Text.literal("Naturally triggered!").formatted(Formatting.GREEN))), true);
                }else{
                    source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The light of §d" + target.getName().getString() + "§e has been set to " ).formatted(Formatting.YELLOW)
                            .append(Text.literal("Not yet naturally triggered!").formatted(Formatting.RED))), true);
                }
            }

        }
        return 1;
    }

    //TODO update wiki with this thingies (and permissions and such)
    private int addLuxDialogueState(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        DialogueProgressState state = LuxdialogueStateArgument.getState(context, "dialogueState");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            component.addDialogueProgressState(state);

            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("A new progress state has been added to your LuxCognita dialogue: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(state)).formatted(Formatting.GREEN))));
            }

            if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The LuxDialogue of §d" + target.getName().getString() + "§e has been updated, adding: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(state)).formatted(Formatting.GREEN))), true);
            }

        }
        return 1;
    }

    private int removeLuxDialogueState(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        DialogueProgressState state = LuxdialogueStateArgument.getState(context, "dialogueState");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            component.removeDialogueProgressState(state);

            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("A progress state has been removed from your LuxCognita dialogue: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(state)).formatted(Formatting.RED))));
            }

            if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The LuxDialogue of §d" + target.getName().getString() + "§e has been updated, removing: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(state)).formatted(Formatting.RED))), true);
            }

        }
        return 1;
    }

    public LiteralCommandNode<ServerCommandSource> getNode() {
        return CommandManager
                .literal("set")
                .requires(PermissionsChecker.hasPerms("lightwithin.commands.set", 2))
                .then(
                        CommandManager
                                .literal("type")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .then(
                                                        CommandManager.argument("light_type", LightTypeArgument.lightType())
                                                                .suggests(LightCommand.Suggests.allLightTypes())
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
                                                                .suggests(LightCommand.Suggests.targetTypes())
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
                                                        CommandManager.argument("new_power", IntegerArgumentType.integer(1, LightWithin.getMaxPowerCommands()))
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
                                                        CommandManager.argument("new_duration", IntegerArgumentType.integer(1))
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
                                                        CommandManager.argument("new_cooldown", IntegerArgumentType.integer(1, 120))
                                                                .executes(this::changeCooldown)
                                                )

                                )
                )
                .then(
                        CommandManager
                                .literal("max_charges")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .then(
                                                        CommandManager.argument("new_light_charges", IntegerArgumentType.integer(0))
                                                                .executes(this::changeLightCharges)
                                                )

                                )
                )
                .then(
                        CommandManager
                                .literal("locked")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .then(
                                                        CommandManager.argument("lock", BoolArgumentType.bool())
                                                                .executes(this::changeLocked)
                                                )

                                )
                )
                .then(
                        CommandManager
                                .literal("hasTriggeredNaturally")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .then(
                                                        CommandManager.argument("triggeredNaturally", BoolArgumentType.bool())
                                                                .executes(this::changeTriggeredNaturally)
                                                )

                                )
                )
                .then(
                        CommandManager
                                .literal("dialogueProgress")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .then(
                                                        CommandManager.literal("add")
                                                                .then(
                                                                        CommandManager.argument("dialogueState", LuxdialogueStateArgument.state())
                                                                                .suggests(LightCommand.Suggests.dialogueState())
                                                                                .executes(this::addLuxDialogueState)
                                                        )
                                                )
                                                .then(
                                                        CommandManager.literal("remove")
                                                                .then(
                                                                        CommandManager.argument("dialogueState", LuxdialogueStateArgument.state())
                                                                                .suggests(LightCommand.Suggests.dialogueState())
                                                                                .executes(this::removeLuxDialogueState)
                                                                )
                                                )

                                )
                )
                .build();
    }
}
