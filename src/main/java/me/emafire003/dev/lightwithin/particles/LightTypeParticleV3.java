package me.emafire003.dev.lightwithin.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class LightTypeParticleV3 extends AnimatedParticle {
    LightTypeParticleV3(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, -1.50F);
        this.velocityMultiplier = 0.6F;
        this.velocityX = velocityX + this.random.nextFloat()/5 * this.random.nextBetween(-1, +1); //og *0.1
        this.velocityY = velocityY;
        this.velocityZ = velocityZ + this.random.nextFloat()/5 * this.random.nextBetween(-1, +1); //og *0.1
        this.scale *= 0.75F;
        this.maxAge = 60 + this.random.nextInt(12);
        this.setSpriteForAge(spriteProvider);
        int val = this.random.nextInt(15);
        if (val == 0) {
            this.setColor(0.6F + this.random.nextFloat() * 0.2F, 1F, 1F);
        }else if(val == 1){
            this.setColor(1F, 0.6F + this.random.nextFloat() * 0.2F, 1F);
        }else if(val == 2){
            this.setColor(1F, 1f, 0.6F + this.random.nextFloat() * 0.2F);
        }

    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new LightTypeParticleV3(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}

