package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LightEffects {
    public static final StatusEffect LIGHT_FATIGUE = registerStatusEffect("light_fatigue",
            new LightFatigueEffect());
    public static final StatusEffect LIGHT_ACTIVE = registerStatusEffect("light_active",
            new LightActiveEffect());
    public static final StatusEffect FROST = registerStatusEffect("frost",
            new FrostEffect());

    private static StatusEffect registerStatusEffect(String name, StatusEffect effect){

        return Registry.register(Registry.STATUS_EFFECT, new Identifier(LightWithin.MOD_ID, name),effect);
    }

    public static void registerModEffects(){
        LightWithin.LOGGER.info("Registering status effects for " + LightWithin.MOD_ID);
    }
}
