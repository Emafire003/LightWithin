package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.permissions.PermissionsChecker;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.events.LightCreationAndEvent;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

public class GetLightCommand implements LightCommand{

    private int getNaturallyTriggered(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            boolean b = LightWithin.LIGHT_COMPONENT.get(target).hasTriggeredNaturally();
            if(b){
                source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight of §d" + target.getName().getString() + "has ").formatted(Formatting.YELLOW)
                        .append(Text.literal("already triggered naturally!").formatted(Formatting.GREEN))), true);
            }else{
                source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight of §d" + target.getName().getString() + "has ").formatted(Formatting.YELLOW)
                        .append(Text.literal("not yet triggered naturally!").formatted(Formatting.RED))), true);
            }

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(() -> Text.literal("Error: " + e),false);
            return 0;
        }

    }

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
            source.sendFeedback(() -> Text.literal("Error: " + e),false);
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
            source.sendFeedback(() -> Text.literal("Error: " + e),false);
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
            source.sendFeedback(() -> Text.literal("Error: " + e),false);
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
            source.sendFeedback(() -> Text.literal("Error: " + e),false);
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
            source.sendFeedback(() -> Text.literal("Error: " + e),false);
            return 0;
        }
    }

    private int getMaxLightCharges(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            int maxLightStack = LightWithin.LIGHT_COMPONENT.get(target).getMaxLightStack();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The max number of light charges for §d" + target.getName().getString() + "§e is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal("§a"+maxLightStack))), true);
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(() -> Text.literal("Error: " + e),false);
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
            source.sendFeedback(() -> Text.literal("Error: " + e),false);
            return 0;
        }
    }

    private int getCooldown(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            Map<RegistryEntry<StatusEffect>, StatusEffectInstance> effect_map = target.getActiveStatusEffects();
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
            source.sendFeedback(() -> Text.literal("Error: " + e),false);
            return 0;
        }
    }

    private int getAll(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            String[] originalUUID = target.getUuid().toString().toLowerCase().split("-");
            Pair<InnerLightType, TargetType> original_type_target = LightCreationAndEvent.determineTypeAndTarget(originalUUID, LightCreationAndEvent.TYPE_BIT, LightCreationAndEvent.TARGET_BIT);

            LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
            InnerLightType type = component.getType();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight type of §d" + target.getName().getString() + "§e is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal(type.toString()).formatted(Formatting.GREEN)).append(Text.literal(", was: ").formatted(Formatting.YELLOW)).append(Text.literal(original_type_target.getFirst().toString()).formatted(Formatting.GREEN))), true);
            TargetType target_type = component.getTargets();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The TargetType is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal(target_type.toString()).formatted(Formatting.GREEN)).append(Text.literal(", was: ").formatted(Formatting.YELLOW)).append(Text.literal(original_type_target.getSecond().toString()).formatted(Formatting.GREEN))), true);
            double power = component.getPowerMultiplier();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The power multiplier is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal("§a"+power)).append(Text.literal(", was: ").formatted(Formatting.YELLOW)).append(Text.literal(String.valueOf(LightCreationAndEvent.determinePower(originalUUID, LightCreationAndEvent.POWER_BIT))).formatted(Formatting.GREEN))), true);
            int duration = component.getDuration();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The duration is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal("§a"+duration)).append(Text.literal(", was: ").formatted(Formatting.YELLOW)).append(Text.literal(String.valueOf(LightCreationAndEvent.determineDuration(originalUUID, LightCreationAndEvent.DURATION_BIT))).formatted(Formatting.GREEN))), true);
            int mcooldown = component.getMaxCooldown();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The max cooldown is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal("§a"+mcooldown)).append(Text.literal(", was: ").formatted(Formatting.YELLOW)).append(Text.literal(String.valueOf(LightCreationAndEvent.determineCooldown(originalUUID, LightCreationAndEvent.COOLDOWN_BIT))).formatted(Formatting.GREEN))), true);
            int maxLightStacks = component.getMaxLightStack();
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The max light charges number is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal("§a"+maxLightStacks)).append(Text.literal(", was: ").formatted(Formatting.YELLOW)).append(Text.literal(String.valueOf(LightCreationAndEvent.determineMaxLightCharges(originalUUID, LightCreationAndEvent.COOLDOWN_BIT))).formatted(Formatting.GREEN))), true);

            if(component.getLocked()){
                source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Locked: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal("true").formatted(Formatting.GREEN))), true);
            }else{
                source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Locked: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal("false").formatted(Formatting.RED))), true);
            }
            if(component.hasTriggeredNaturally()){
                source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Has triggered naturally: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal("true").formatted(Formatting.GREEN))), true);
            }else{
                source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Has triggered naturally: " ).formatted(Formatting.YELLOW)
                        .append(Text.literal("false").formatted(Formatting.RED))), true);
            }

            Map<RegistryEntry<StatusEffect>, StatusEffectInstance> effect_map = target.getActiveStatusEffects();
            if(effect_map.containsKey(LightEffects.LIGHT_FATIGUE)){
                int cooldown = effect_map.get(LightEffects.LIGHT_FATIGUE).getDuration()/20;
                source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The current cooldown is: ").formatted(Formatting.YELLOW).append(Text.literal("§a"+cooldown))), true);
            }else{
                source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§d" + target.getName().getString() + "§e is not in cooldown" ).formatted(Formatting.YELLOW)), true);
            }
            source.sendFeedback(() -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The current number of light charges is: " ).formatted(Formatting.YELLOW)
                    .append(Text.literal("§a"+component.getCurrentLightCharges()))), true);

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(() -> Text.literal("Error: " + e),false);
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
            source.sendFeedback(() -> Text.literal("Error: " + e),false);
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
                                .literal("max_light_charges")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(this::getMaxLightCharges)


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
                .then(
                        CommandManager
                                .literal("hasTriggeredNaturally")
                                .then(
                                        CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(this::getNaturallyTriggered)

                                )
                )
                .build();
    }
}
