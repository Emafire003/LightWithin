package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LightEffects {
    public static final StatusEffect LIGHT_FATIGUE = registerStatusEffect("light_fatigue",
            new LightFatigueEffect());

    private static StatusEffect registerStatusEffect(String name, StatusEffect effect){

        return Registry.register(Registry.STATUS_EFFECT, new Identifier(LightWithin.MOD_ID, name),effect);
    }

    public static void registerModEffects(){
        System.out.println("Registering ModStatusEffects for mod:" + LightWithin.MOD_ID);
    }
}
