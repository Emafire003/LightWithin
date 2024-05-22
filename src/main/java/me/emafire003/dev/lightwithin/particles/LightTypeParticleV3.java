package me.emafire003.dev.lightwithin.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class LightTypeParticleV3 extends AnimatedParticle {
    LightTypeParticleV3(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, -1.75F);
        this.velocityMultiplier = 0.6F;
        this.velocityX = velocityX*0.1;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ*0.1;
        this.scale *= 0.75F;
        this.maxAge = 60 + this.random.nextInt(12);
        this.setSpriteForAge(spriteProvider);
        int val = this.random.nextInt(15);
        if (val == 0) {
            this.setColor(0.3F + this.random.nextFloat() * 0.2F, 1F, 1F);
        }else if(val == 1){
            this.setColor(1F, 0.3F + this.random.nextFloat() * 0.2F, 1F);
        }else if(val == 2){
            this.setColor(1F, 1f, 0.3F + this.random.nextFloat() * 0.2F);
        }

    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new LightTypeParticleV3(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}

