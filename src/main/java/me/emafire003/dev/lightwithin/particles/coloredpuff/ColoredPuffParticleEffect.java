package me.emafire003.dev.lightwithin.particles.coloredpuff;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import me.emafire003.dev.lightwithin.particles.LightParticles;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3f;

public class ColoredPuffParticleEffect extends AbstractDustParticleEffect {

    /*public static final Vector3f SCULK_BLUE = Vec3d.unpackRgb(3790560).toVector3f();
    public static final ColoredPuffParticleEffect DEFAULT = new ColoredPuffParticleEffect(SCULK_BLUE, DustParticleEffect.RED, 1.0F);
   */
    
    public static final MapCodec<ColoredPuffParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codecs.VECTOR_3F.fieldOf("from_color").forGetter(effect -> effect.fromColor),
                            Codecs.VECTOR_3F.fieldOf("to_color").forGetter(effect -> effect.toColor),
                            SCALE_CODEC.fieldOf("scale").forGetter(AbstractDustParticleEffect::getScale)
                    )
                    .apply(instance, ColoredPuffParticleEffect::new)
    );

    public static final PacketCodec<RegistryByteBuf, ColoredPuffParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR3F,
            effect -> effect.fromColor,
            PacketCodecs.VECTOR3F,
            effect -> effect.toColor,
            PacketCodecs.FLOAT,
            AbstractDustParticleEffect::getScale,
            ColoredPuffParticleEffect::new
    );

    private final Vector3f fromColor;
    private final Vector3f toColor;

    public ColoredPuffParticleEffect(Vector3f fromColor, Vector3f toColor, float scale) {
        super(scale);
        this.fromColor = fromColor;
        this.toColor = toColor;
    }

    public Vector3f getFromColor() {
        return this.fromColor;
    }

    public Vector3f getToColor() {
        return this.toColor;
    }



    @Override
    public ParticleType<ColoredPuffParticleEffect> getType() {
        return LightParticles.COLORED_PUFF_PARTICLE;
    }
}
