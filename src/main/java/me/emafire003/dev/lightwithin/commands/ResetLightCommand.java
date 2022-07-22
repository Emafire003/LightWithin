package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.events.LightTriggeringAndEvents;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ResetLightCommand implements LightCommand{

    //Temporary, will remove once all lights have been implemented
    private List<InnerLightType> currently_usable_lights = Arrays.asList(InnerLightType.HEAL, InnerLightType.DEFENCE, InnerLightType.STRENGTH, InnerLightType.BLAZING, InnerLightType.FROST);
    private boolean confirming = false;
    private int tickCounter = 0;

    private int reset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
        ServerCommandSource source = context.getSource();
        if(!confirming){
            resetConfirm(context);
            return 0;
        }

        try{
            for(ServerPlayerEntity target : targets){
                LightComponent component = LightWithin.LIGHT_COMPONENT.get(target);
                component.clear();
                LightTriggeringAndEvents.createUniqueLight(target);
                source.sendFeedback(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight of §d" + target.getName().getString() + "§e has been resetted to its original values!" ).formatted(Formatting.YELLOW)), true);
            }
            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(Text.literal("Error: " + e.toString()),false);
            return 0;
        }

    }

    private int resetConfirm(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        source.sendFeedback(Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("§ePlease type §a/light reset <player/s> confirm §eto §c§lreset §etheir InnerLight")), false);
        confirming = true;
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if(confirming){
                tickCounter++;
            }
            if(tickCounter > 20*10){
                confirming = false;
                tickCounter = 0;
            }
        });
        return  1;
    }



    public LiteralCommandNode<ServerCommandSource> getNode() {
        return CommandManager
                .literal("reset")
                .then(
                        CommandManager.argument("player", EntityArgumentType.players())
                                .executes(this::resetConfirm)
                )
                .then(
                        CommandManager.argument("player", EntityArgumentType.players()).then(
                                        CommandManager.literal("confirm")
                                                .executes(this::reset)
                                )
                )
                .build();
    }
}
