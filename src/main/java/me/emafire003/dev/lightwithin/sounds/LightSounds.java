package me.emafire003.dev.lightwithin.sounds;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class LightSounds {

    public static SoundEvent HEAL_LIGHT = registerSoundEvent("heal_light");
    public static SoundEvent LIGHT_READY = registerSoundEvent("light_ready");
    public static SoundEvent LIGHT_CHARGED = registerSoundEvent("light_charged");
    public static SoundEvent LIGHT_ERROR = registerSoundEvent("light_error");
    public static SoundEvent LUXCOGNITA_DISPLAY = registerSoundEvent("luxcognita_display");
    public static SoundEvent LIGHT_CRIT = registerSoundEvent("light_crit");
    public static SoundEvent DEFENSE_LIGHT = registerSoundEvent("defense_light");
    public static SoundEvent STRENGTH_LIGHT = registerSoundEvent("strength_light");
    public static SoundEvent BLAZING_LIGHT = registerSoundEvent("blazing_light");
    public static SoundEvent FROST_LIGHT = registerSoundEvent("frost_light");
    public static SoundEvent EARTHEN_LIGHT = registerSoundEvent("earthen_light");
    public static SoundEvent WIND_LIGHT = registerSoundEvent("wind_light");
    public static SoundEvent AQUA_LIGHT = registerSoundEvent("aqua_light");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(LightWithin.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }


    public static void registerSounds() {
        LOGGER.info("Registering sound effects...");
    }

}
