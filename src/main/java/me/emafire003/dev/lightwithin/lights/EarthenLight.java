package me.emafire003.dev.lightwithin.lights;

import com.google.common.collect.Maps;
import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.entities.LightEntities;
import me.emafire003.dev.lightwithin.entities.earth_golem.EarthGolemEntity;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.structureplacerapi.StructurePlacerAPI;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import java.util.*;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;
import static me.emafire003.dev.lightwithin.LightWithin.MOD_ID;

public class EarthenLight extends InnerLight {

    /*Possible triggers:
       - self low health
       - allies low health (checkable like this if type = Heal && target = allies do stuff
       - surrounded++
       - NEEDS to have in hand dirt/rock or be around them. 
       - Ally dying?
     */

    /*Possible targets:
    * - enemies -> Dripstone/Hole
    * - ally/self -> moat around/wall depending on something i guess. Don't know what tho.
    * - other -> TODO spawn golem. For now it could be a simple iron golem?*/

    public EarthenLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, Color color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.EARTHEN;
    }

    public EarthenLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.EARTHEN;
        color = new Color(114, 66, 11);
    }

    public EarthenLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.EARTHEN;
        color = new Color(114, 66, 11);
    }

    private void checkSafety(){
        if(this.power_multiplier > Config.FROST_MAX_POWER){
            power_multiplier = Config.FROST_MAX_POWER;
        }
        if(this.power_multiplier < Config.FROST_MIN_POWER){
            power_multiplier = Config.FROST_MIN_POWER;
        }
        if(this.duration > Config.FROST_MAX_DURATION){
            this.duration = Config.FROST_MAX_DURATION;
        }
        if(this.duration < Config.FROST_MIN_DURATION){
            this.duration = Config.FROST_MIN_DURATION;
        }
    }

    @Override
    public void execute(){
        checkSafety();
        if(this.rainbow_col){
            ColoredGlowLib.setRainbowColorToEntity(this.caster, true);
        }else{
            ColoredGlowLib.setColorToEntity(this.caster, this.color);
        }

        caster.getWorld().playSound(caster, caster.getBlockPos(), LightSounds.EARTHEN_LIGHT, SoundCategory.AMBIENT, 1, 1);
        if(caster.getWorld().isClient){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(caster);
        //Will create a ravine under the enemies feet, and will also damage them and apply mining fatigue
        if(component.getTargets().equals(TargetType.ENEMIES)){
            //TODO maybe create a boudler projectile in the future
            //TODO probably need to extend the enemy radius
            LivingEntity oldtarget = null;
            for(LivingEntity target : this.targets){
                float r = target.getDimensions(EntityPose.STANDING).width/2;
                float h = target.getDimensions(EntityPose.STANDING).height;
                target.damage(DamageSource.IN_WALL, (float) this.power_multiplier);
                LightParticlesUtil.spawnCylinder(target.getPos().add(0, 0.2, 0), r, 50, h, h/5, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());


                //LightParticlesUtil.spawnCylinderBlock(target.getPos(), 2.5, 50, 7, 0.2, Blocks.DIRT.getDefaultState(), (ServerWorld) caster.getWorld());
                LightParticlesUtil.spawnCircle(target.getPos().add(0, 0.3, 0), 2.5, 150, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.EARTHENLIGHT_PARTICLE, (ServerWorld) target.getWorld(), target.getPos());
                LightParticlesUtil.spawnCircle(target.getPos().add(0, 0.45, 0), 1.5, 150, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.MUD_BRICKS.getDefaultState()), (ServerWorld) caster.getWorld());

                //If the oldtarget and the new one have a distance greater than 3 it will spawn a new hole,
                //otherwise it will skip it, since probably they would end up in the same hole regardless
                if(oldtarget == null || oldtarget.distanceTo(target) > 3){
                    StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "earth_hole"), target.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1f, new BlockPos(-3, -10, -3));
                    placer.loadStructure();
                }
                oldtarget = target;
                //TODO add minging fatigue?
                //TODO maybe configable?
                target.playSound(LightSounds.EARTHEN_LIGHT, 0.9f, 1);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) this.power_multiplier, false, false));
            }
            //It will spawn a wall around the allies and self, depending on the power level it could have a secret tunnel to escape underneath
        }else if(component.getTargets().equals(TargetType.ALLIES)){
            //oldtarget and stuuf prevent generating multiple structures in the same area
            LivingEntity oldtarget = null;
            for(LivingEntity target : this.targets){

                target.playSound(LightSounds.EARTHEN_LIGHT, 0.9f, 1);
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.EARTHENLIGHT_PARTICLE, (ServerWorld) target.getWorld(), target.getPos());
                target.addStatusEffect(new StatusEffectInstance(LightEffects.SOLID_ROCK, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) this.power_multiplier, false, false));
                if(Config.STRUCTURE_GRIEFING && !caster.getWorld().isClient) {
                    if(oldtarget == null || oldtarget.distanceTo(target) > 3){
                        StructurePlacerAPI placer;
                        if(this.power_multiplier > 4){
                            placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "earth_wall"), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1f, new BlockPos(-3, -5, -6));
                        }else{
                            placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "earth_wall1"), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1f, new BlockPos(-3, -1, -4));
                        }
                        placer.loadStructure();
                    }
                    oldtarget = target;
                    LightParticlesUtil.spawnCircle(target.getPos().add(0, 0.15, 0), 5, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    LightParticlesUtil.spawnCircle(target.getPos().add(0, 0.25, 0), 4.75, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    LightParticlesUtil.spawnCircle(target.getPos().add(0, 0.45, 0), 4.5, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());

                }
            }
            //Depending on the level it will spawn a small moat and pillar around the user, a big pillar only and a big pillar with a big moat.
            //And will also give Solid Rock effect to self, making the player more resistant to knokback
        }else if(component.getTargets().equals(TargetType.SELF)){
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.EARTHENLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
            caster.addStatusEffect(new StatusEffectInstance(LightEffects.SOLID_ROCK, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) this.power_multiplier, false, false));
            caster.playSound(LightSounds.FROST_LIGHT, 1, 1);
            if(Config.STRUCTURE_GRIEFING && !caster.getWorld().isClient) {
                StructurePlacerAPI placer;
                if(this.power_multiplier >= 6){
                    LightParticlesUtil.spawnCircle(caster.getPos().add(0, 0.45, 0), 7, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    LightParticlesUtil.spawnCircle(caster.getPos().add(0, 1.45, 0), 5, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    LightParticlesUtil.spawnCircle(caster.getPos().add(0, 3, 0), 3, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    LightParticlesUtil.spawnCircle(caster.getPos().add(0, 5, 0), 2.20, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "self_moat_pillar"), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 0.96f, new BlockPos(-7, -6, -7));
                    caster.teleport(caster.getX(), caster.getY()+5, caster.getZ());

                }else if(this.power_multiplier < 6 && this.power_multiplier >= 3){
                    LightParticlesUtil.spawnCircle(caster.getPos().add(0, 0.45, 0), 3, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    LightParticlesUtil.spawnCircle(caster.getPos().add(0, 1.45, 0), 2.50, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    LightParticlesUtil.spawnCircle(caster.getPos().add(0, 4.5, 0), 1.5, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "pillar_only"), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 0.96f, new BlockPos(-2, 0, -2));
                    caster.teleport(caster.getX(), caster.getY()+7, caster.getZ());
                }else{
                    LightParticlesUtil.spawnCircle(caster.getPos().add(0, 0.25, 0), 2.5, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    LightParticlesUtil.spawnCircle(caster.getPos().add(0, 1.45, 0), 1, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "small_moat"), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 0.9f, new BlockPos(-3, -5, -3));
                    caster.teleport(caster.getX(), caster.getY()+2, caster.getZ());
                }
                placer.loadStructure();
                caster.playSound(LightSounds.EARTHEN_LIGHT, 1, 1);
            }
        }else if(component.getTargets().equals(TargetType.OTHER)){
            int rx = caster.getRandom().nextBetween(0, 5);
            int rz = caster.getRandom().nextBetween(0, 5);
            EarthGolemEntity entity = new EarthGolemEntity(LightEntities.EARTH_GOLEM, caster.getWorld());
            entity.setPos(caster.getX()+rx, caster.getY(), caster.getZ()+rz);
            LightParticlesUtil.spawnCircle(caster.getPos().add(0, 0.45, 0), 2, 100, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
            LightParticlesUtil.spawnCircle(caster.getPos().add(0, 1.45, 0), 2, 100, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
            LightParticlesUtil.spawnCircle(caster.getPos().add(0, 0.2, 0), 2, 100, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
            entity.setSummoner(caster);
            caster.getWorld().spawnEntity(entity);
        }


    }
}