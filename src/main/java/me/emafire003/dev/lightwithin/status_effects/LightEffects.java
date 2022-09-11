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
    public static final StatusEffect FREEZE_RESISTANCE = registerStatusEffect("freeze_resistance",
            new FreezeResistanceEffect());
    public static final StatusEffect SOLID_ROCK = registerStatusEffect("solid_rock",
            new SolidRockEffect().addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, "70d5f061-467c-430a-a9b6-48475f961db9", 0.15f, EntityAttributeModifier.Operation.ADDITION));
            //.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "70d5f061-467c-430a-a9b6-48475f961db9", 0.5f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));

    private static StatusEffect registerStatusEffect(String name, StatusEffect effect){
        return Registry.register(Registry.STATUS_EFFECT, new Identifier(LightWithin.MOD_ID, name),effect);
    }

    public static void registerModEffects(){
        LightWithin.LOGGER.info("Registering status effects for " + LightWithin.MOD_ID);
    }
}
