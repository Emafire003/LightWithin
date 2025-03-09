package me.emafire003.dev.lightwithin.sounds;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class LightSounds {

    //TODO see https://docs.fabricmc.net/develop/sounds/dynamic-sounds and maybe add a static noise while the thunder aura effect is active
    public static SoundEvent HEAL_LIGHT = registerSoundEvent("heal_light");
    public static SoundEvent LIGHT_READY = registerSoundEvent("light_ready");
    public static SoundEvent LIGHT_CHARGED = registerSoundEvent("light_charged");
    public static SoundEvent LIGHT_ERROR = registerSoundEvent("light_error");
    public static SoundEvent LUXCOGNITA_DISPLAY = registerSoundEvent("luxcognita_display");
    public static SoundEvent LIGHT_CRIT = registerSoundEvent("light_crit");
    public static SoundEvent FOREST_AURA_PUFF = registerSoundEvent("forest_aura_puff");
    public static SoundEvent THUNDER_AURA_ZAP = registerSoundEvent("thunder_aura_zap");
    public static SoundEvent DEFENSE_LIGHT = registerSoundEvent("defense_light");
    public static SoundEvent STRENGTH_LIGHT = registerSoundEvent("strength_light");
    public static SoundEvent BLAZING_LIGHT = registerSoundEvent("blazing_light");
    public static SoundEvent FROST_LIGHT = registerSoundEvent("frost_light");
    public static SoundEvent EARTHEN_LIGHT = registerSoundEvent("earthen_light");
    public static SoundEvent WIND_LIGHT = registerSoundEvent("wind_light");
    public static SoundEvent AQUA_LIGHT = registerSoundEvent("aqua_light");
    public static SoundEvent FOREST_AURA_LIGHT = registerSoundEvent("forest_aura_light");
    public static SoundEvent THUNDER_AURA_LIGHT = registerSoundEvent("thunder_aura_light");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = LightWithin.getIdentifier(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }


    public static void registerSounds() {
        LOGGER.info("Registering sound effects...");
    }

}
