package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.DialogueProgressState;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.lights.NoneLight;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.List;


//Based on Factions' code https://github.com/ickerio/factions (MIT license)
public interface LightCommand {
    LiteralCommandNode<ServerCommandSource> getNode();

    interface Suggests {

        static SuggestionProvider<ServerCommandSource> targetTypes() {
            return (context, builder) -> {

                Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "player");
                for(ServerPlayerEntity player : targets){
                    InnerLight light = LightWithin.LIGHT_COMPONENT.get(player).getType();

                    List<TargetType> possible = light.getPossibleTargetTypes();

                    for(TargetType type : possible){
                        builder.suggest(type.toString());
                    }
                }


                return builder.buildFuture();
            };
        }

        static SuggestionProvider<ServerCommandSource> allLightTypes() {
            return (context, builder) -> {
                LightWithin.INNERLIGHT_REGISTRY.forEach( type-> {
                    if(type instanceof NoneLight){
                        return;
                    }
                    builder.suggest(type.toString());
                });


                return builder.buildFuture();
            };
        }

        static SuggestionProvider<ServerCommandSource> dialogueState() {
            return (context, builder) -> {
                for(DialogueProgressState state : DialogueProgressState.values()) {
                    builder.suggest(state.toString());
                }
                return builder.buildFuture();
            };
        }
    }

}
