package me.emafire003.dev.lightwithin.compat.yawp;

import de.z0rdak.yawp.core.flag.RegionFlag;
import de.z0rdak.yawp.handler.flags.FlagCheckEvent;
import de.z0rdak.yawp.handler.flags.HandlerUtil;
import de.z0rdak.yawp.managers.data.region.DimensionRegionCache;
import de.z0rdak.yawp.managers.data.region.RegionDataManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class YawpCompat {

    public static boolean canActivateHere(ServerPlayerEntity player, BlockPos pos){
        DimensionRegionCache dimCache = RegionDataManager.get().cacheFor(player.getServerWorld().getRegistryKey());
        FlagCheckEvent flagCheckEvent = HandlerUtil.checkTargetEvent(pos, RegionFlag.NO_PVP, dimCache.getDimensionalRegion());
        return !flagCheckEvent.isDenied();

    }

    public static boolean canActivateHereGriefing(ServerPlayerEntity player, BlockPos pos){
        DimensionRegionCache dimCache = RegionDataManager.get().cacheFor(player.getServerWorld().getRegistryKey());
        FlagCheckEvent flagCheckEvent = HandlerUtil.checkTargetEvent(pos, RegionFlag.BREAK_BLOCKS, dimCache.getDimensionalRegion());
        return !flagCheckEvent.isDenied();
    }

}
