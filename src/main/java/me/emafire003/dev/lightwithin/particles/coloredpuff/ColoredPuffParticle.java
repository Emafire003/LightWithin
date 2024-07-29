package me.emafire003.dev.lightwithin.particles.coloredpuff;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class ColoredPuffParticle extends AbstractDustParticle<ColoredPuffParticleEffect> {
    private final Vector3f startColor;
    private final Vector3f endColor;

    protected ColoredPuffParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ColoredPuffParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, parameters, spriteProvider);
        float f = this.random.nextFloat() * 0.4F + 0.6F;

        //TODO this is the shade thing. Which, I might even keep
        this.startColor = this.darken(parameters.getFromColor(), f);
        this.endColor = this.darken(parameters.getToColor(), f);

        //This is how the thing is going to expand
        /*this.velocityMultiplier = 0.96F;
        this.velocityX *= 0.1F;
        this.velocityY *= 0.1F;
        this.velocityZ *= 0.1F;
        this.velocityX += velocityX;
        this.velocityY += velocityY;
        this.velocityZ += velocityZ;*/
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.scale *= 0.75F;
        this.maxAge = 60 + this.random.nextInt(12);

    }

    private Vector3f darken(Vector3f color, float multiplier) {
        return new Vector3f(this.darken(color.x(), multiplier), this.darken(color.y(), multiplier), this.darken(color.z(), multiplier));
    }


    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }

    private void updateColor(float tickDelta) {
        float f = ((float)this.age + tickDelta) / ((float)this.maxAge + 1.0F);
        Vector3f vector3f = new Vector3f(this.startColor).lerp(this.endColor, f);
        this.red = vector3f.x();
        this.green = vector3f.y();
        this.blue = vector3f.z();
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        this.updateColor(tickDelta);
        super.buildGeometry(vertexConsumer, camera, tickDelta);
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<ColoredPuffParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(
                ColoredPuffParticleEffect ColoredPuffParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i
        ) {
            return new ColoredPuffParticle(clientWorld, d, e, f, g, h, i, ColoredPuffParticleEffect, this.spriteProvider);
        }
    }
}

