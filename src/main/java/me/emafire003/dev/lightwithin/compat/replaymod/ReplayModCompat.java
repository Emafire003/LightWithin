package me.emafire003.dev.lightwithin.compat.replaymod;

import com.replaymod.replay.ReplayHandler;
import com.replaymod.replay.ReplayModReplay;
import net.fabricmc.loader.api.FabricLoader;

public class ReplayModCompat {

    public static boolean isInReplayMode(){
        if(!FabricLoader.getInstance().isModLoaded("replaymod")){
            return false;
        }
        ReplayHandler handler = ReplayModReplay.instance.getReplayHandler();
        return handler != null;
    }
}
