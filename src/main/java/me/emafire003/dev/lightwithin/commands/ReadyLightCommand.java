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

import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.sendReadyPacket;

public class ReadyLightCommand implements LightCommand{

    private int tickCounter = 0;
    private boolean stopped = false;

    private int readyDelay(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
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
                        sendReadyPacket(target, true);
                        source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight of §d" + target.getName().getString() + "§e has been readied!" ).formatted(Formatting.YELLOW)), true);
                        if(Config.TARGET_FEEDBACK){
                            target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Your InnerLight was made ready by force!" ).formatted(Formatting.YELLOW)));
                        }
                        stopped = true;
                        tickCounter = 0;
                    }

                });
            }

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback( () -> Text.literal("Error: " + e),false);
            return 0;
        }
    }

    private int ready(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
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
                sendReadyPacket(target, true);
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight of §d" + target.getName().getString() + "§e has been readied!" ).formatted(Formatting.YELLOW)), true);
                if(Config.TARGET_FEEDBACK){
                    target.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Your InnerLight was made ready by force!" ).formatted(Formatting.YELLOW)));
                }
            }
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback( () -> Text.literal("Error: " + e),false);
            return 0;
        }

    }


    public LiteralCommandNode<ServerCommandSource> getNode() {
        return CommandManager
                .literal("ready")
                .requires(PermissionsChecker.hasPerms("lightwithin.commands.ready", 2))
                .then(
                        CommandManager.argument("player", EntityArgumentType.players())
                                .executes(this::ready)
                )
                .then(
                        CommandManager.argument("player", EntityArgumentType.players())
                                        .then(
                                                CommandManager.argument("delay", IntegerArgumentType.integer())
                                                        .executes(this::readyDelay)
                                        )
                )
                .build();
    }
}
