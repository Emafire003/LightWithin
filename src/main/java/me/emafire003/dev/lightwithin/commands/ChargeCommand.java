package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.permissions.PermissionsChecker;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Objects;

public class ChargeCommand implements LightCommand{

    private int addLightCharges(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int light_charges_to_add = IntegerArgumentType.getInteger(context, "charges");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            int total_charges = light_charges_to_add+component.getCurrentLightCharges();
            if(total_charges > component.getMaxLightStack()){
                if(Config.TARGET_FEEDBACK){
                    target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Cannot add this many light charges, the max number of allowed light charges is: " ).formatted(Formatting.RED)
                            .append(Text.literal(String.valueOf(component.getMaxLightStack())).formatted(Formatting.GREEN))));
                    LightParticlesUtil.spawnChargedParticles(target);
                    target.playSound(LightSounds.LIGHT_CHARGED, 1f, 1f);
                }
                if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                    source.sendError(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of light charges of " + target.getName().getString() + "§e could not be changes, since it would exceed the max light stacking which is: " ).formatted(Formatting.RED)
                            .append(Text.literal(String.valueOf(component.getMaxLightStack())).formatted(Formatting.GREEN))));
                }
                return 0;
            }else{
                component.setLightCharges(total_charges);
                if(Config.TARGET_FEEDBACK){
                    target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of your light charges is now: " ).formatted(Formatting.YELLOW)
                            .append(Text.literal(String.valueOf(total_charges)).formatted(Formatting.GREEN))));
                }

                if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                    source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of light charges of " + target.getName().getString() + "§e has been changed to: " ).formatted(Formatting.YELLOW)
                            .append(Text.literal(String.valueOf(total_charges)).formatted(Formatting.GREEN))), true);
                }
            }

        }
        return 1;
    }

    private int removeLightCharges(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int light_charges_to_remove = IntegerArgumentType.getInteger(context, "charges");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            int total_charges = component.getCurrentLightCharges()-light_charges_to_remove;
            if(total_charges < 0){
                if(Config.TARGET_FEEDBACK){
                    target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Cannot remove this many light charges, it would go into the negatives! The current amount of charge is: " ).formatted(Formatting.RED)
                            .append(Text.literal(String.valueOf(component.getCurrentLightCharges())).formatted(Formatting.GREEN))));
                }
                if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                    source.sendError(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of light charges of " + target.getName().getString() + "§e could not be changed, since it would go into the negatives! The current number of charges is: " ).formatted(Formatting.RED)
                            .append(Text.literal(String.valueOf(component.getCurrentLightCharges())).formatted(Formatting.GREEN))));
                }
                return 0;
            }else{
                component.setLightCharges(total_charges);
                if(Config.TARGET_FEEDBACK){
                    target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of your light charges is now: " ).formatted(Formatting.YELLOW)
                            .append(Text.literal(String.valueOf(total_charges)).formatted(Formatting.GREEN))));
                }

                if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                    source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of light charges of " + target.getName().getString() + "§e has been changed to: " ).formatted(Formatting.YELLOW)
                            .append(Text.literal(String.valueOf(total_charges)).formatted(Formatting.GREEN))), true);
                }
            }

        }
        return 1;
    }

    private int fillLightCharges(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            component.setLightCharges(component.getMaxLightStack());

            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of your light charges is now: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(component.getMaxLightStack())).formatted(Formatting.GREEN))));
                LightParticlesUtil.spawnChargedParticles(target);
                target.playSound(LightSounds.LIGHT_CHARGED, 1f, 1f);
            }

            if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of light charges of " + target.getName().getString() + "§e has been changed to: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(component.getMaxLightStack())).formatted(Formatting.GREEN))), true);
            }

        }
        return 1;
    }

    private int emptyLightCharges(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            LightWithin.LIGHT_COMPONENT.get(target).setLightCharges(0);
            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of your light charges is now: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(0)).formatted(Formatting.GREEN))));
            }

            if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of light charges of " + target.getName().getString() + "§e has been changed to: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(0)).formatted(Formatting.GREEN))), true);
            }

        }
        return 1;
    }

    private int getLightCharges(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();

        for(ServerPlayerEntity target : targets){
            int charges = LightWithin.LIGHT_COMPONENT.get(target).getCurrentLightCharges();
            if(Config.TARGET_FEEDBACK){
                target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of your light charges is: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(charges)).formatted(Formatting.GREEN))));
            }

            if(!Objects.requireNonNull(source.getPlayer()).equals(target) || !Config.TARGET_FEEDBACK){
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The number of light charges of " + target.getName().getString() + "§e is: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal(String.valueOf(charges)).formatted(Formatting.GREEN))), true);
            }

        }
        return 1;
    }


    public LiteralCommandNode<ServerCommandSource> getNode() {
        return CommandManager
                .literal("charge")
                .requires(PermissionsChecker.hasPerms("lightwithin.commands.charge", 2))
                .then(
                        CommandManager
                                .literal("add")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .then(
                                                        CommandManager.argument("charges", IntegerArgumentType.integer(1))
                                                                .executes(this::addLightCharges)
                                                )

                                )
                ).then(
                        CommandManager
                                .literal("remove")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .then(
                                                        CommandManager.argument("charges", IntegerArgumentType.integer(1))
                                                                .executes(this::removeLightCharges)
                                                )

                                )
                )
                .then(
                        CommandManager
                                .literal("fill")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .executes(this::fillLightCharges)
                                )
                ).then(
                        CommandManager
                                .literal("empty")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .executes(this::emptyLightCharges)
                                )
                )
                .then(
                        CommandManager
                                .literal("get")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.players())
                                                .executes(this::getLightCharges)
                                )
                )
                .build();
    }
}
