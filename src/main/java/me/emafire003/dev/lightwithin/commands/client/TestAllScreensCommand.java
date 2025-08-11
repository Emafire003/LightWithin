
package me.emafire003.dev.lightwithin.commands.client;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.screens.LuxdialogueScreens;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestAllScreensCommand implements ClientLightCommand {

    private int testScreen(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        try{

            List<String> brokens = new ArrayList<>();
            LuxdialogueScreens.LUXDIALOGUE_SCREENS.forEach((s, luxcognitaScreenV2) -> {
                    try {
                        MinecraftClient.getInstance().setScreen(luxcognitaScreenV2);
                        context.getSource().sendFeedback(Text.literal(s + " ok").formatted(Formatting.GREEN));
                    }catch (Exception e){
                        brokens.add(s);
                        context.getSource().sendFeedback(Text.literal(s + " has problems!!!").formatted(Formatting.RED));
                    }
            }
            );
            if(!brokens.isEmpty()){
                context.getSource().sendFeedback(Text.literal("§bA list of broken screens: "));
                brokens.forEach( br -> context.getSource().sendFeedback(Text.literal(br).formatted(Formatting.RED)));
            }else{
                context.getSource().sendFeedback(Text.literal("All screens good").formatted(Formatting.DARK_GREEN));
            }
            return 1;
        }catch (Exception e){
            context.getSource().sendError(Text.literal(LightWithin.PREFIX_MSG).append("§cThere has been an error while displaying screens"));
            e.printStackTrace();
            return 0;
        }

    }


    public LiteralCommandNode<FabricClientCommandSource> getNode() {
        return ClientCommandManager
                .literal("test_screens")
                .executes(this::testScreen)
                .build();
    }
}
