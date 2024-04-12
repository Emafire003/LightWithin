package me.emafire003.dev.lightwithin.commands.client;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.config.ClientConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class ReloadClientConfigCommand implements ClientLightCommand {


    private int reloadConfig(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        try{
            ClientConfig.reloadConfig();
            context.getSource().sendFeedback(Text.literal(LightWithin.PREFIX_MSG).append("§aClient config successfully reloaded!"));
            return 1;
        }catch (Exception e){
            context.getSource().sendError(Text.literal(LightWithin.PREFIX_MSG).append("§cThere has been an error while reloading the config, check the logs"));
            e.printStackTrace();
            return 0;
        }

    }


    public LiteralCommandNode<FabricClientCommandSource> getNode() {
        return ClientCommandManager
                .literal("reload")
                .executes(this::reloadConfig)
                .build();
    }
}
