package me.emafire003.dev.lightwithin.particles;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.particles.coloredpuff.ColoredPuffParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class LightParticles {
    public static final SimpleParticleType HEALLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType DEFENSELIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType STRENGTHLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType BLAZINGLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType FROSTLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType EARTHENLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType WINDLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType AQUALIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType FOREST_AURA_LIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType THUNDER_AURA_LIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType FROGLIGHT_PARTICLE = FabricParticleTypes.simple();

    public static final DefaultParticleType LIGHTNING_PARTICLE = FabricParticleTypes.simple();

    public static final SimpleParticleType LIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType SHINE_PARTICLE = FabricParticleTypes.simple();
    public static final ParticleType<ColoredPuffParticleEffect> COLORED_PUFF_PARTICLE = FabricParticleTypes.complex(ColoredPuffParticleEffect.FACTORY);

    public static void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("heal_light_particle"),
                HEALLIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("defense_light_particle"),
                DEFENSELIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("strength_light_particle"),
                STRENGTHLIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("blazing_light_particle"),
                BLAZINGLIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("frost_light_particle"),
                FROSTLIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("earthen_light_particle"),
                EARTHENLIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("wind_light_particle"),
                WINDLIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("aqua_light_particle"),
                AQUALIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("frog_light_particle"),
        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("forest_aura_light_particle"),
                FOREST_AURA_LIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("thunder_aura_light_particle"),
                THUNDER_AURA_LIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("frog_light_particle"),
                FROGLIGHT_PARTICLE);

        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("light_particle"),
                LIGHT_PARTICLE);

        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("shine_particle"),
                SHINE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier( "colored_puff_particle"),
                COLORED_PUFF_PARTICLE);

        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("lightning_particle"),
                LIGHTNING_PARTICLE);
    }
}
