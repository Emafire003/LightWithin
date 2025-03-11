package me.emafire003.dev.lightwithin.compat.yawp;

import de.z0rdak.yawp.api.FlagEvaluator;
import de.z0rdak.yawp.api.events.region.FlagCheckEvent;
import de.z0rdak.yawp.api.events.region.FlagCheckResult;
import de.z0rdak.yawp.core.flag.FlagState;
import de.z0rdak.yawp.core.flag.RegionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class YawpCompat {

    //TODO test for the new versio
    public static boolean canActivateHere(ServerPlayerEntity player, BlockPos pos){
        FlagCheckEvent flagCheckEvent = new FlagCheckEvent(pos, RegionFlag.NO_PVP, player.getWorld().getRegistryKey(), player);
        FlagCheckResult flagCheckResult = FlagEvaluator.evaluate(flagCheckEvent);
        FlagState flagState = flagCheckResult.getFlagState();
        return flagState.equals(FlagState.ALLOWED);

    }

    public static boolean canActivateHereGriefing(ServerPlayerEntity player, BlockPos pos){
        /*DimensionRegionCache dimCache = RegionDataManager.get().cacheFor(player.getServerWorld().getRegistryKey());
        FlagCheckEvent flagCheckEvent = HandlerUtil.checkTargetEvent(pos, RegionFlag.BREAK_BLOCKS, dimCache.getDimensionalRegion());
        return !flagCheckEvent.isDenied();*/
        FlagCheckEvent flagCheckEvent = new FlagCheckEvent(pos, RegionFlag.BREAK_BLOCKS, player.getWorld().getRegistryKey(), player);
        FlagCheckResult flagCheckResult = FlagEvaluator.evaluate(flagCheckEvent);
        FlagState flagState = flagCheckResult.getFlagState();
        return flagState.equals(FlagState.ALLOWED);
    }

}
