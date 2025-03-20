package me.emafire003.dev.lightwithin.compat.flashback;

import com.moulberry.flashback.Flashback;
import net.fabricmc.loader.api.FabricLoader;

public class FlashbackCompat {

    public static boolean isInReplayMode(){
        if(!FabricLoader.getInstance().isModLoaded("flashback")){
            return false;
        }
        return Flashback.isInReplay();
    }
}
