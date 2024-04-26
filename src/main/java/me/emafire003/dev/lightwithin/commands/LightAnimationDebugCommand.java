package me.emafire003.dev.lightwithin.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.permissions.PermissionsChecker;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.networking.PlayRenderEffectPacketS2C;
import me.emafire003.dev.lightwithin.util.RenderEffect;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;

public class LightAnimationDebugCommand implements LightCommand{

    private int playEffect(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgumentType.getEntities(context, "target");
        ServerCommandSource source = context.getSource();

        try{
            for(Entity target : targets){
                if(target instanceof ServerPlayerEntity){
                    ServerPlayNetworking.send((ServerPlayerEntity) target, PlayRenderEffectPacketS2C.ID, new PlayRenderEffectPacketS2C(RenderEffect.LIGHT_RAYS));
                }

                source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("The InnerLight of §d" + target.getName().getString() + "§e has been triggered!" ).formatted(Formatting.YELLOW)), true);
                if(Config.TARGET_FEEDBACK){
                    source.sendFeedback( () -> Text.literal(LightWithin.PREFIX_MSG).formatted(Formatting.AQUA).append(Text.literal("Your InnerLight was activated by force!" ).formatted(Formatting.YELLOW)), true);
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
                .literal("debug")
                .requires(PermissionsChecker.hasPerms("lightwithin.commands.debug", 2))
                .then(
                        CommandManager.literal("shader_effect").then(
                                CommandManager.argument("target", EntityArgumentType.entities())
                                        .executes(this::playEffect)
                        )

                )
                .build();
    }
}
