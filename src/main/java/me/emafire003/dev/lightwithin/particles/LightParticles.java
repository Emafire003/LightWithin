package me.emafire003.dev.lightwithin.particles;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.particles.coloredpuff.ColoredPuffParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LightParticles {
    public static final DefaultParticleType HEALLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType DEFENSELIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType STRENGTHLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType BLAZINGLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType FROSTLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType EARTHENLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType WINDLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType AQUALIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType FOREST_AURA_LIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType THUNDER_AURA_LIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType FROGLIGHT_PARTICLE = FabricParticleTypes.simple();

    public static final DefaultParticleType LIGHTNING_PARTICLE = FabricParticleTypes.simple();

    public static final DefaultParticleType LIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType SHINE_PARTICLE = FabricParticleTypes.simple();
    public static final ParticleType<ColoredPuffParticleEffect> COLORED_PUFF_PARTICLE = FabricParticleTypes.complex(ColoredPuffParticleEffect.FACTORY);

    public static void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "heal_light_particle"),
                HEALLIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "defense_light_particle"),
                DEFENSELIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "strength_light_particle"),
                STRENGTHLIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "blazing_light_particle"),
                BLAZINGLIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "frost_light_particle"),
                FROSTLIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "earthen_light_particle"),
                EARTHENLIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "wind_light_particle"),
                WINDLIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "aqua_light_particle"),
                AQUALIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "forest_aura_light_particle"),
                FOREST_AURA_LIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "thunder_aura_light_particle"),
                THUNDER_AURA_LIGHT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "frog_light_particle"),
                FROGLIGHT_PARTICLE);

        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "light_particle"),
                LIGHT_PARTICLE);

        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "shine_particle"),
                SHINE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "colored_puff_particle"),
                COLORED_PUFF_PARTICLE);

        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "lightning_particle"),
                LIGHTNING_PARTICLE);
    }
}
