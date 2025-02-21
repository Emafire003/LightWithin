package me.emafire003.dev.lightwithin.status_effects;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.fabridash.FabriDash;
import me.emafire003.dev.particleanimationlib.effects.AnimatedBallEffect;
import me.emafire003.dev.particleanimationlib.effects.CuboidEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;


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
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        //Boh non funziona un tubo ok.

        float height = entity.getDimensions(entity.getPose()).height;
        Box box = Box.of(entity.getPos().add(0, height/1.5, 0), 0.6+height-height/10, 0.6+height-height/10, 0.6+height-height/10); //og is 1.7


        //Used to debug the size of the collision area

        if(!entity.getWorld().isClient){
            CuboidEffect cuboidEffect = CuboidEffect.builder((ServerWorld) entity.getWorld(), ParticleTypes.BUBBLE, new Vec3d(box.minX, box.minY, box.minZ))
                    .targetPos(new Vec3d(box.maxX, box.maxY, box.maxZ)).updatePositions(true).updateTargetPositions(true)
                    .blockSnap(false).padding(0).particles(20).build();
            cuboidEffect.setIterations(2);
            cuboidEffect.run();
        }

        //TODO wiki Gets the nearby entities that ARE NOT ALLIES (meaning anyone except allies will get zapped and knocked back)
        List<LivingEntity> nearby_entities = entity.getWorld().getEntitiesByClass(LivingEntity.class, box, (entity1 -> {
            //TODO test the ally thing
            return !CheckUtils.CheckAllies.checkAlly(entity, entity1) && !entity1.equals(entity);
        }));

        nearby_entities.forEach(target -> {
            if(target instanceof PlayerEntity && target.isSpectator()){
                return;
            }
            //Vec3d v = entity.getPos().add(.5, .5, .5).subtract(target.getPos());
            Vec3d v = entity.getPos().add(.5, .5, .5).subtract(target.getPos());
            v = v.multiply(1, 0.00001, 1).multiply(-1.5);
            v = v.normalize().multiply(.6+((double) amplifier /10)); //This is the one that multiplies
            LightWithin.LOGGER.info("The velocity is: " + v);
            target.setVelocityClient(v.x, v.y, v.z);
            target.setVelocity(v);
            if(target instanceof ServerPlayerEntity && !target.getWorld().isClient()){
                FabriDash.sendVelocityPacket((ServerPlayerEntity) target, v);
            }

            //TODO also add a shock if the entity is attecked? Like a smaller knockback and a zap?
            //At level 0 it won't do any damage
            target.damage(target.getDamageSources().lightningBolt(), amplifier);
            //TODO add zap playsound
        });

        super.applyUpdateEffect(entity, amplifier);
    }


    @Override
    public void onApplied(LivingEntity target, AttributeContainer attributes, int amplifier) {
        super.onApplied(target, attributes, amplifier);

        if(target.getWorld().isClient()){
            return;
        }

        /*AnimatedBallEffect ballEffect = AnimatedBallEffect.builder((ServerWorld) target.getWorld(), LightParticles.LIGHTNING_PARTICLE, target.getPos())
                .entityOrigin(target).originOffset(new Vec3d(0,0.5,0)).updatePositions(true) // This is used to follow the player
                .size(1.5f).particles(20).particlesPerIteration(20)
                .build();*/
        float height = target.getDimensions(target.getPose()).height;
        AnimatedBallEffect ballEffect = AnimatedBallEffect.builder((ServerWorld) target.getWorld(), LightParticles.LIGHTNING_PARTICLE, target.getPos())
                .entityOrigin(target).originOffset(new Vec3d(0,height/3.5,0)).updatePositions(true) // This is used to follow the player
                .size(height-height/12).particles((int) (20+(height/10))).particlesPerIteration((int) (20+(height/10)))
                .build();

        target.sendMessage(Text.literal("The height: " + target.getDimensions(target.getPose()).height));
        target.sendMessage(Text.literal("The width: " + target.getDimensions(target.getPose()).width));

        //TODO set the duration of the effects
        //TODO add the chaning size and ither stuff to allow for entity pose changes
        ballEffect.runFor(10);


    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier){
        super.onRemoved(entity, attributes, amplifier);
        if(!(entity instanceof PlayerEntity)){
            return;
        }
    }

}