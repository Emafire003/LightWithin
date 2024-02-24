package me.emafire003.dev.lightwithin.compat.flan;

import io.github.flemmli97.flan.api.ClaimHandler;
import io.github.flemmli97.flan.api.permission.ClaimPermission;
import io.github.flemmli97.flan.api.permission.PermissionRegistry;
import me.emafire003.dev.lightwithin.config.Config;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class FlanCompat {

    public static ClaimPermission ACTIVATE_LIGHT = PermissionRegistry.register(new ClaimPermission("ACTIVATE_LIGHT", () -> new ItemStack(Items.LIGHT),true,
            "Permission to activate an InnerLight"));
    public static ClaimPermission LIGHT_GRIEFING = PermissionRegistry.register(new ClaimPermission("LIGHT_GRIEFING", () -> new ItemStack(Items.REDSTONE_LAMP),true,
            "Permission to modify the terrain during a light activation"));

    public static boolean canActivateHere(ServerPlayerEntity player, BlockPos pos){
        try{
            //It works unless you own that claim etc
            boolean can = ClaimHandler.canInteract(player, pos, ACTIVATE_LIGHT);

            return can;
        }catch (NullPointerException e){
            //It means there is no chunk
            return Config.LIGHT_DEFAULT_STATUS;
        }
    }

    public static boolean canActivateHereGriefing(ServerPlayerEntity player, BlockPos pos){
        return ClaimHandler.canInteract(player, pos, LIGHT_GRIEFING);
    }

    public static void registerFlan(){

    }

}
