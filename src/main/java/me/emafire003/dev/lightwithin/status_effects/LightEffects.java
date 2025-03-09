package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LightEffects {
    public static final StatusEffect LIGHT_FATIGUE = registerStatusEffect("light_fatigue",
            new LightFatigueEffect());
    public static final StatusEffect LIGHT_ACTIVE = registerStatusEffect("light_active",
            new LightActiveEffect());
    public static final StatusEffect FROST = registerStatusEffect("frost",
            new FrostEffect().addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "4d5ca720-40c5-492f-b74a-f31771f2dacc", -1.0f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final StatusEffect FREEZE_RESISTANCE = registerStatusEffect("freeze_resistance",
            new FreezeResistanceEffect());
    public static final StatusEffect STURDY_ROCK = registerStatusEffect("sturdy_rock",
            new SturdyRockEffect().addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, "70d5f061-467c-430a-a9b6-48475f961db9", 0.15f, EntityAttributeModifier.Operation.ADDITION));
            //.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "70d5f061-467c-430a-a9b6-48475f961db9", 0.5f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final StatusEffect WATER_SLIDE = registerStatusEffect("water_slide",
                    new WaterSlideEffect());
    public static final StatusEffect WATER_CASCADE = registerStatusEffect("water_cascade",
            new WaterCascadeEffect());
    public static final StatusEffect FOREST_AURA = registerStatusEffect("forest_aura",
            new ForestAuraEffect());
    public static final StatusEffect INTOXICATION = registerStatusEffect("intoxication",
            new IntoxicationEffect());
    public static final StatusEffect THUNDER_AURA = registerStatusEffect("thunder_aura",
            new ThunderAuraEffect());
    public static final StatusEffect STORM_AURA = registerStatusEffect("storm_aura",
            new StormAuraEffect());

    private static StatusEffect registerStatusEffect(String name, StatusEffect effect){
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(LightWithin.MOD_ID, name),effect);
    }

    public static void registerModEffects(){
        LightWithin.LOGGER.info("Registering status effects for " + LightWithin.MOD_ID);
    }
}
