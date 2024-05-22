package me.emafire003.dev.lightwithin.particles;

import me.emafire003.dev.lightwithin.LightWithin;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LightParticles {
    public static final SimpleParticleType HEALLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType DEFENSELIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType STRENGTHLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType BLAZINGLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType FROSTLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType EARTHENLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType WINDLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType AQUALIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType FROGLIGHT_PARTICLE = FabricParticleTypes.simple();

    public static final SimpleParticleType LIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType SHINE_PARTICLE = FabricParticleTypes.simple();


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
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "frog_light_particle"),
                FROGLIGHT_PARTICLE);

        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "light_particle"),
                LIGHT_PARTICLE);

        Registry.register(Registries.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "shine_particle"),
                SHINE_PARTICLE);
    }
}
