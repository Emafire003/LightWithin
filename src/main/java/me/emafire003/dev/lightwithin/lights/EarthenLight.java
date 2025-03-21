package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.entities.LightEntities;
import me.emafire003.dev.lightwithin.entities.earth_golem.EarthGolemEntity;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.SpawnUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.structureplacerapi.StructurePlacerAPI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;


import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.*;

public class EarthenLight extends InnerLight {

    public static final Item INGREDIENT = Items.MOSSY_COBBLESTONE;

    public EarthenLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.EARTHEN;
    }

    public EarthenLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.EARTHEN;
        color = "72420b";
    }

    public EarthenLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.EARTHEN;
        //color = "#72420b";
        color = "earthen";
    }

    private void checkSafety(){
        if(this.power_multiplier > BalanceConfig.EARTHEN_MAX_POWER){
            power_multiplier = BalanceConfig.EARTHEN_MAX_POWER;
        }
        if(this.power_multiplier < BalanceConfig.EARTHEN_MIN_POWER){
            power_multiplier = BalanceConfig.EARTHEN_MIN_POWER;
        }
        int max_duration = BalanceConfig.EARTHEN_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.EARTHEN_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(this.duration > max_duration){
            this.duration = max_duration;
        }
        if(this.duration < BalanceConfig.EARTHEN_MIN_DURATION){
            this.duration = BalanceConfig.EARTHEN_MIN_DURATION;
        }
    }

    @Override
    public void execute(){
        checkSafety();

        LightComponent component = LightWithin.LIGHT_COMPONENT.get(caster);
        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            if(this.rainbow_col){
                CGLCompat.getLib().setRainbowColor(this.caster);
            }else{
                CGLCompat.getLib().setColor(this.caster, this.color);
            }
        }


        LightParticlesUtil.spawnLightTypeParticle(LightParticles.EARTHENLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
        caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()), LightSounds.EARTHEN_LIGHT, SoundCategory.PLAYERS, 1f, 1f);

        if(caster.getWorld().isClient){
            return;
        }

        //Will create a ravine under the enemies feet, and will also damage them and apply mining fatigue
        if(component.getTargets().equals(TargetType.ENEMIES)){
            //TODO maybe create a boudler projectile in the future
            //TODO probably need to extend the enemy radius
            LivingEntity oldtarget = null;
            for(LivingEntity target : this.targets){
                float r = target.getDimensions(EntityPose.STANDING).width/2;
                float h = target.getDimensions(EntityPose.STANDING).height;

                //This are used to immobilize the target ad let it fall down
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1, 255, false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1, 255, false, false));

                target.damage(caster.getWorld().getDamageSources().inWall(), (float) this.power_multiplier);
                LightParticlesUtil.spawnCylinder(target.getPos().add(0, 0.2, 0), r, 50, h, h/5, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());


                //LightParticlesUtil.spawnCylinderBlock(target.getPos(), 2.5, 50, 7, 0.2, Blocks.DIRT.getDefaultState(), (ServerWorld) caster.getWorld());
                LightParticlesUtil.spawnCircle(target.getPos().add(0, 0.3, 0), 2.5, 150, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                LightParticlesUtil.spawnCircle(target.getPos().add(0, 0.45, 0), 1.5, 150, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.MUD_BRICKS.getDefaultState()), (ServerWorld) caster.getWorld());


                //If the oldtarget and the new one have a distance greater than 3 it will spawn a new hole,
                //otherwise it will skip it, since probably they would end up in the same hole regardless
                if((oldtarget == null || oldtarget.distanceTo(target) > 3) && CheckUtils.checkGriefable((ServerPlayerEntity) caster)){
                    StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "earth_hole"), target.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1f, new BlockPos(-3, -11, -3));
                    if(Config.REPLACEABLE_STRUCTURES && !Config.KEEP_ESSENTIALS_STRUCTURES){
                        placer.loadAndRestoreStructureAnimated(caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), 2, true);
                    }else{
                        placer.loadStructure();
                    }
                    //It also plays here since a hole opens under things
                    caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()), LightSounds.EARTHEN_LIGHT, SoundCategory.PLAYERS, 1f, 1f);
                }
                oldtarget = target;
                //target.playSound(LightSounds.EARTHEN_LIGHT, 0.9f, 1);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) this.power_multiplier, false, true));
            }
            //It will spawn a wall around the allies and self, depending on the power level it could have a secret tunnel to escape underneath
        }else if(component.getTargets().equals(TargetType.ALLIES)){
            //oldtarget and stuuf prevent generating multiple structures in the same area
            LivingEntity oldtarget = null;
            for(LivingEntity target : this.targets){

                //target.playSound(LightSounds.EARTHEN_LIGHT, 0.9f, 1);
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.EARTHENLIGHT_PARTICLE, (ServerWorld) target.getWorld(), target.getPos());

                if(target.equals(caster)){
                    target.addStatusEffect(new StatusEffectInstance(LightEffects.STURDY_ROCK, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) (this.power_multiplier/Config.DIV_SELF), false, false));
                }else{
                    target.addStatusEffect(new StatusEffectInstance(LightEffects.STURDY_ROCK, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) this.power_multiplier, false, false));
                }

                if(!caster.getWorld().isClient && CheckUtils.checkGriefable((ServerPlayerEntity) caster)) {
                    if(oldtarget == null || oldtarget.distanceTo(target) > 3){
                        StructurePlacerAPI placer;
                        if(this.power_multiplier > 4){
                            placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "earth_wall"), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1f, new BlockPos(-3, -5, -6));
                        }else{
                            placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "earth_wall1"), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1f, new BlockPos(-3, -1, -4));
                        }

                        if(Config.REPLACEABLE_STRUCTURES && !Config.KEEP_ESSENTIALS_STRUCTURES){
                            placer.loadAndRestoreStructureAnimated(caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), 2, true);
                        }else{
                            placer.loadStructure();
                        }
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
            caster.addStatusEffect(new StatusEffectInstance(LightEffects.STURDY_ROCK, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) this.power_multiplier, false, false));
            if(!caster.getWorld().isClient && CheckUtils.checkGriefable((ServerPlayerEntity) caster)) {
                StructurePlacerAPI placer;
                if(this.power_multiplier >= 7){
                    LightParticlesUtil.spawnCircle(caster.getPos().add(0, 0.45, 0), 7, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    LightParticlesUtil.spawnCircle(caster.getPos().add(0, 1.45, 0), 5, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    LightParticlesUtil.spawnCircle(caster.getPos().add(0, 3, 0), 3, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    LightParticlesUtil.spawnCircle(caster.getPos().add(0, 5, 0), 2.20, 120, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
                    placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "self_moat_pillar"), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 0.96f, new BlockPos(-7, -6, -7));
                    caster.teleport(caster.getX(), caster.getY()+5, caster.getZ());

                }else if(this.power_multiplier < 7 && this.power_multiplier >= 3){
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
                if(Config.REPLACEABLE_STRUCTURES && !Config.KEEP_ESSENTIALS_STRUCTURES){
                    placer.loadAndRestoreStructureAnimated(caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), 2, true);
                }else{
                    placer.loadStructure();
                }
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.EARTHENLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
            }
        }else if(component.getTargets().equals(TargetType.VARIANT)){
            int rx = caster.getRandom().nextBetween(0, 5);
            int rz = caster.getRandom().nextBetween(0, 5);
            EarthGolemEntity entity = new EarthGolemEntity(LightEntities.EARTH_GOLEM, caster.getWorld());
            entity.setPos(caster.getX()+rx, caster.getY(), caster.getZ()+rz);
            LightParticlesUtil.spawnCircle(caster.getPos().add(0, 0.45, 0), 2, 100, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
            LightParticlesUtil.spawnCircle(caster.getPos().add(0, 1.45, 0), 2, 100, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
            LightParticlesUtil.spawnCircle(caster.getPos().add(0, 0.2, 0), 2, 100, new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), (ServerWorld) caster.getWorld());
            entity.setSummoner(caster);
            SpawnUtils.spawnAround(caster, 1, 6, entity, (ServerWorld) caster.getWorld());
        }


    }
}
