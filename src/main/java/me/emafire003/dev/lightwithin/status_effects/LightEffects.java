package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class LightEffects {
    public static final RegistryEntry<StatusEffect> LIGHT_FATIGUE = registerStatusEffect("light_fatigue",
            new LightFatigueEffect());
    public static final RegistryEntry<StatusEffect> LIGHT_ACTIVE = registerStatusEffect("light_active",
            new LightActiveEffect());
    public static final RegistryEntry<StatusEffect> FROST = registerStatusEffect("frost",
            new FrostEffect().addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "4d5ca720-40c5-492f-b74a-f31771f2dacc", -1.0f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final RegistryEntry<StatusEffect> FREEZE_RESISTANCE = registerStatusEffect("freeze_resistance",
            new FreezeResistanceEffect());
    public static final RegistryEntry<StatusEffect> STURDY_ROCK = registerStatusEffect("sturdy_rock",
            new SturdyRockEffect().addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, "70d5f061-467c-430a-a9b6-48475f961db9", 0.15f, EntityAttributeModifier.Operation.ADD_VALUE));
            //.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "70d5f061-467c-430a-a9b6-48475f961db9", 0.5f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryEntry<StatusEffect> WATER_SLIDE = registerStatusEffect("water_slide",
                    new WaterSlideEffect());
    public static final RegistryEntry<StatusEffect> WATER_CASCADE = registerStatusEffect("water_cascade",
            new WaterCascadeEffect());
    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect effect){
        return Registry.registerReference(Registries.STATUS_EFFECT, new Identifier(LightWithin.MOD_ID, name), effect);
    }

    public static void registerModEffects(){
        LightWithin.LOGGER.info("Registering status effects for " + LightWithin.MOD_ID);
    }
}
