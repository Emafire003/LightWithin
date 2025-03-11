package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.fabridash.FabriDash;
import me.emafire003.dev.particleanimationlib.effects.AnimatedBallEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Objects;


public class ThunderAuraEffect extends StatusEffect {

    public ThunderAuraEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xAFCE23);
    }


    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    //for some reason this does not work, so work-arounds!
    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {

        float height = entity.getDimensions(entity.getPose()).height();
        Box box = Box.of(entity.getPos().add(0, height/1.5, 0), 0.6+height-height/10, 0.6+height-height/10, 0.6+height-height/10); //og is 1.7


        //Used to debug the size of the collision area
        /*if(!entity.getWorld().isClient){
            CuboidEffect cuboidEffect = CuboidEffect.builder((ServerWorld) entity.getWorld(), ParticleTypes.BUBBLE, new Vec3d(box.minX, box.minY, box.minZ))
                    .targetPos(new Vec3d(box.maxX, box.maxY, box.maxZ)).updatePositions(true).updateTargetPositions(true)
                    .blockSnap(false).padding(0).particles(20).build();
            cuboidEffect.setIterations(2);
            cuboidEffect.run();
        }*/

        //TODO wiki Gets the nearby entities that ARE NOT ALLIES (meaning anyone except allies will get zapped and knocked back)
        List<LivingEntity> nearby_entities = entity.getWorld().getEntitiesByClass(LivingEntity.class, box, (entity1 -> !CheckUtils.CheckAllies.checkAlly(entity, entity1) && !entity1.equals(entity)));

        nearby_entities.forEach(target -> {
            if(target instanceof PlayerEntity && target.isSpectator()){
                return;
            }

            //At level 0 it won't do any damage
            target.damage(target.getDamageSources().lightningBolt(), amplifier);
            target.getWorld().playSound(null, BlockPos.ofFloored(target.getPos()), LightSounds.THUNDER_AURA_ZAP, SoundCategory.PLAYERS, 0.7f, 1.07f);
            //Spawns a few particles when the entity gets zapped
            if(!target.getWorld().isClient()){
                ((ServerWorld) target.getWorld()).spawnParticles(LightParticles.LIGHTNING_PARTICLE, target.getX(), target.getY(), target.getZ(), 5, 0.2, 0.2, 0.2, 1f);
                ((ServerWorld) target.getWorld()).spawnParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY(), target.getZ(), 5, 0.2, 0.2, 0.2, 1f);
            }

            Vec3d v = entity.getPos().add(.5, .5, .5).subtract(target.getPos());
            v = v.multiply(1, 0.00001, 1).multiply(-1.5);
            v = v.normalize().multiply(.6+((double) amplifier /10)); //This is the one that multiplies

            target.setVelocityClient(v.x, v.y, v.z);
            target.setVelocity(v);
            if(target instanceof ServerPlayerEntity && !target.getWorld().isClient()){
                FabriDash.sendVelocityPacket((ServerPlayerEntity) target, v);
            }
        });

        return super.applyUpdateEffect(entity, amplifier);
    }


    @Override
    public void onApplied(LivingEntity target, int amplifier) {
        super.onApplied(target, amplifier);

        if(target.getWorld().isClient()){
            return;
        }

        float height = target.getDimensions(target.getPose()).height();
        AnimatedBallEffect ballEffect = AnimatedBallEffect.builder((ServerWorld) target.getWorld(), LightParticles.LIGHTNING_PARTICLE, target.getPos())
                .entityOrigin(target).originOffset(new Vec3d(0,height/3.5,0)).updatePositions(true) // This is used to follow the player
                .size(height-height/12).particles((int) (20+(height/10))).particlesPerIteration((int) (20+(height/10)))
                .build();

        ballEffect.runFor((double) Objects.requireNonNull(target.getStatusEffect(LightEffects.THUNDER_AURA)).getDuration()/20);


    }

}