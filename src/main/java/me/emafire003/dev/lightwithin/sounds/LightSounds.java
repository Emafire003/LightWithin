package me.emafire003.dev.lightwithin.sounds;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.FrogLight;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.lights.NoneLight;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.HashMap;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class LightSounds {

    public static HashMap<Identifier, SoundEvent> TYPES_SOUNDS = new HashMap<>();

    private static void registerLightTypeSound(InnerLight type){
        TYPES_SOUNDS.put(type.getLightId(), registerSoundEvent(type.getLightId().getPath()+"_light"));
    }

    //TODO see https://docs.fabricmc.net/develop/sounds/dynamic-sounds and maybe add a static noise while the thunder aura effect is active
    public static SoundEvent LIGHT_READY = registerSoundEvent("light_ready");
    public static SoundEvent LIGHT_CHARGED = registerSoundEvent("light_charged");
    public static SoundEvent LIGHT_ERROR = registerSoundEvent("light_error");
    public static SoundEvent LUXCOGNITA_DISPLAY = registerSoundEvent("luxcognita_display");
    public static SoundEvent LIGHT_CRIT = registerSoundEvent("light_crit");
    public static SoundEvent FOREST_AURA_PUFF = registerSoundEvent("forest_aura_puff");
    public static SoundEvent THUNDER_AURA_ZAP = registerSoundEvent("thunder_aura_zap");
    public static SoundEvent LUXCOGNITA_DAMAGE_BLOCK = registerSoundEvent("luxcognita_damage_block");
    public static SoundEvent LUXCOGNITA_DREAM_BGM = registerSoundEvent("luxcognita_dream");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(LightWithin.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }


    /** MUST BE CALLED AFTER REGISTERING THE LIGHTS
     * */
    public static void registerSounds() {
        LOGGER.debug("Registering sound effects...");
        LightWithin.INNERLIGHT_REGISTRY.forEach( innerLight -> {
            if(innerLight instanceof FrogLight || innerLight instanceof NoneLight){
                return;
            }
            registerLightTypeSound(innerLight);
        });
    }

}
