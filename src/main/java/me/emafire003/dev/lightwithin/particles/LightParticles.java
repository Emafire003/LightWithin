package me.emafire003.dev.lightwithin.particles;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.lights.NoneLight;
import me.emafire003.dev.lightwithin.particles.coloredpuff.ColoredPuffParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class LightParticles {

    public static final HashMap<Identifier, SimpleParticleType> TYPES_PARTICLES = new HashMap<>();

    private static void registerLightTypeParticle(InnerLight type){
        TYPES_PARTICLES.put(type.getLightId(),
                Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier(type.getLightId().getPath()+"_light_particle"), FabricParticleTypes.simple()));

        LightWithin.LOGGER.info("Registering particle: " + TYPES_PARTICLES);
    }

    public static final SimpleParticleType LIGHTNING_PARTICLE = FabricParticleTypes.simple();

    public static final SimpleParticleType LIGHT_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType SHINE_PARTICLE = FabricParticleTypes.simple();
    public static final ParticleType<ColoredPuffParticleEffect> COLORED_PUFF_PARTICLE = FabricParticleTypes.complex(ColoredPuffParticleEffect.CODEC, ColoredPuffParticleEffect.PACKET_CODEC);


    /** MUST BE CALLED AFTER REGISTERING THE LIGHTS! */
    public static void registerParticles() {

        LightWithin.INNERLIGHT_REGISTRY.forEach( type -> {
            if(type instanceof NoneLight){
                return;
            }
            registerLightTypeParticle(type);
        });

        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("light_particle"),
                LIGHT_PARTICLE);

        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("shine_particle"),
                SHINE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("colored_puff_particle"),
                COLORED_PUFF_PARTICLE);

        Registry.register(Registries.PARTICLE_TYPE, LightWithin.getIdentifier("lightning_particle"),
                LIGHTNING_PARTICLE);
    }
}
