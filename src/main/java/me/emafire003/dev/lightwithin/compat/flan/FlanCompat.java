package me.emafire003.dev.lightwithin.compat.flan;

import io.github.flemmli97.flan.Flan;
import io.github.flemmli97.flan.api.ClaimHandler;
import me.emafire003.dev.lightwithin.config.Config;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class FlanCompat {

    public static Identifier ACTIVATE_LIGHT = Identifier.of(Flan.MODID, "activate_innerlight");
    public static Identifier LIGHT_GRIEFING = Identifier.of(Flan.MODID, "innerlight_griefing");


    public static boolean canActivateHere(ServerPlayerEntity player, BlockPos pos){
        try{
            //It works unless you own that claim etc
            return ClaimHandler.canInteract(player, pos, ACTIVATE_LIGHT);
        }catch (NullPointerException e){
            //It means there is no chunk
            return Config.LIGHT_DEFAULT_STATUS;
        }
    }

    public static boolean canActivateHereGriefing(ServerPlayerEntity player, BlockPos pos){
        try{
            player.sendMessage(Text.literal("Yes the problem is flan. Checking GRIEFING LIGHT: " + ClaimHandler.canInteract(player, pos, LIGHT_GRIEFING)));

            //It works unless you own that claim etc

            return ClaimHandler.canInteract(player, pos, LIGHT_GRIEFING);
        }catch (NullPointerException e){
            //It means there is no chunk
            return Config.LIGHT_DEFAULT_STATUS;
        }
    }

    public static void registerFlan(){

    }

}
