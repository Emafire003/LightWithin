package me.emafire003.dev.lightwithin.sounds;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class LightSounds {

    public static SoundEvent HEAL_LIGHT = registerSoundEvent("heal_light");
    public static SoundEvent LIGHT_READY = registerSoundEvent("light_ready");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(LightWithin.MOD_ID, name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }


    public static void registerSounds() {
        LOGGER.info("Registering sound effects...");
    }

}
