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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.sendReadyPacket;

public class ReadyLightCommand implements LightCommand{

    /// TargetUUID <delay, tickCounter>
    private static final HashMap<UUID, Pair<Integer, Integer>> targetDelayCounterMap = new HashMap<>();

    public static void createScheduler(){
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            HashSet<UUID> keyCopies = new HashSet<>(targetDelayCounterMap.keySet());
            keyCopies.forEach(uuid -> {
                Pair<Integer, Integer> delay_counter = targetDelayCounterMap.get(uuid);
                PlayerEntity p1 = server.getPlayerManager().getPlayer(uuid);
                assert p1 != null;
                if (delay_counter.getRight() > delay_counter.getLeft()){
                    ServerPlayerEntity p = server.getPlayerManager().getPlayer(uuid);
                    if(p==null){
                        targetDelayCounterMap.remove(uuid);
                        return;
                    }
                    if(p.hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
                        p.removeStatusEffect(LightEffects.LIGHT_ACTIVE);
                    }
                    if(p.hasStatusEffect(LightEffects.LIGHT_FATIGUE)){
                        p.removeStatusEffect(LightEffects.LIGHT_FATIGUE);
                    }
                    sendReadyPacket(p, true);
                    targetDelayCounterMap.remove(uuid);

                    server.getCommandSource().sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight of §d" + p.getName().getString() + "§e has been readied!" ).formatted(Formatting.YELLOW)), true);
                    if(Config.TARGET_FEEDBACK){
                        p.sendMessage(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Your InnerLight was made ready by force!" ).formatted(Formatting.YELLOW)));
                    }
                    return;
                }
                delay_counter.setRight(delay_counter.getRight()+1);
                targetDelayCounterMap.put(uuid, delay_counter);
            });

        });
    }
    private int readyDelay(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        int delay = IntegerArgumentType.getInteger(context, "delay");
        ServerCommandSource source = context.getSource();

        try{
            for(ServerPlayerEntity target : targets){
                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight of §d" + target.getName().getString() + "§e will be activated in §d" + delay + " seconds!" ).formatted(Formatting.YELLOW)), true);
                targetDelayCounterMap.put(target.getUuid(), new Pair<>(delay*20, 0));
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
