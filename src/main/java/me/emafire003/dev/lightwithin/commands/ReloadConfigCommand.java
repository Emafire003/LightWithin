package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.permissions.PermissionsChecker;
import me.emafire003.dev.lightwithin.config.Config;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadConfigCommand implements LightCommand{


    private int reloadConfig(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            Config.reloadConfig();
            context.getSource().sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).append("§aConfig successfully reloaded!"), false);
            return 1;
        }catch (Exception e){
            context.getSource().sendError(Text.literal(LightWithin.PREFIX_MSG).append("§cThere has been an error while reloading the config, check the logs"));
            e.printStackTrace();
            return 0;
        }

    }




    public LiteralCommandNode<ServerCommandSource> getNode() {
        return CommandManager
                .literal("reload")
                .requires(PermissionsChecker.hasPerms("lightwithin.commands.reload", 2))
                .executes(this::reloadConfig)
                .build();
    }
}
