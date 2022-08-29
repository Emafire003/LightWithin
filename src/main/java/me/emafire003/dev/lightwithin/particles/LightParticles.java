package me.emafire003.dev.lightwithin.particles;

import me.emafire003.dev.lightwithin.LightWithin;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LightParticles {
    public static final DefaultParticleType HEALLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType DEFENSELIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType STRENGTHLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType BLAZINGLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType FROSTLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType EARTHENLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType WINDLIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType AQUALIGHT_PARTICLE = FabricParticleTypes.simple();

    public static final DefaultParticleType EARTH_PARTICLE = FabricParticleTypes.simple();


    public static void registerParticles() {
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "heal_light_particle"),
                HEALLIGHT_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "defense_light_particle"),
                DEFENSELIGHT_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "strength_light_particle"),
                STRENGTHLIGHT_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "blazing_light_particle"),
                BLAZINGLIGHT_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "frost_light_particle"),
                FROSTLIGHT_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "earthen_light_particle"),
                EARTHENLIGHT_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "wind_light_particle"),
                WINDLIGHT_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(LightWithin.MOD_ID, "aqua_light_particle"),
                AQUALIGHT_PARTICLE);
    }
}
