package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.permissions.PermissionsChecker;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;

public class ActivateLightCommand implements LightCommand{

    private int tickCounter = 0;
    private boolean stopped = false;

    private int activateDelay(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int delay = IntegerArgumentType.getInteger(context, "delay");
        ServerCommandSource source = context.getSource();

        try{
            for(ServerPlayerEntity target : targets){
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight of §d" + target.getName().getString() + "§e will be activated in §d" + delay + " seconds!" ).formatted(Formatting.YELLOW)), true);
                ServerTickEvents.END_SERVER_TICK.register(server -> {
                    if(!stopped){
                        tickCounter++;
                    }
                    if(tickCounter > 20*delay){
                        LightWithin.activateLight(target);
                        source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight of §d" + target.getName().getString() + "§e has been triggered!" ).formatted(Formatting.YELLOW)), true);
                        if(Config.TARGET_FEEDBACK){
                            target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Your InnerLight was activated by force!" ).formatted(Formatting.YELLOW)));
                        }
                        stopped = true;
                        tickCounter = 0;
                    }

                });
            }

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback( () -> Text.literal("Error: " + e.toString()),false);
            return 0;
        }
    }

    private int activate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();

        try{
            for(ServerPlayerEntity target : targets){
                if(target.hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
                    target.removeStatusEffect(LightEffects.LIGHT_ACTIVE);
                }
                if(target.hasStatusEffect(LightEffects.LIGHT_FATIGUE)){
                    target.removeStatusEffect(LightEffects.LIGHT_FATIGUE);
                }
                LightWithin.activateLight(target);
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight of §d" + target.getName().getString() + "§e has been triggered!" ).formatted(Formatting.YELLOW)), true);
                if(Config.TARGET_FEEDBACK){
                    source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Your InnerLight was activated by force!" ).formatted(Formatting.YELLOW)), true);
                }
            }
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback( () -> Text.literal("Error: " + e.toString()),false);
            return 0;
        }

    }


    public LiteralCommandNode<ServerCommandSource> getNode() {
        return CommandManager
                .literal("activate")
                .requires(PermissionsChecker.hasPerms("lightwithin.commands.activate", 2))
                .then(
                        CommandManager.argument("player", EntityArgumentType.players())
                                .executes(this::activate)
                )
                .then(
                        CommandManager.argument("player", EntityArgumentType.players())
                                        .then(
                                                CommandManager.argument("delay", IntegerArgumentType.integer())
                                                        .executes(this::activateDelay)
                                        )

                )
                .build();
    }
}
