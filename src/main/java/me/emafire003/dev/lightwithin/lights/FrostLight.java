package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.blocks.LightBlocks;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.StructurePlacer;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.PlaceCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.*;

public class FrostLight extends InnerLight {

    /*Possible triggers:
       - self low health
       - allies low health (checkable like this if type = Heal && target = allies do stuff TODO include pets in this
       - surrounded++
       - NEEDS to be on fire, hold a torch, near heat emitting stuff.
       - Ally dying?
     */

    /*Possible targets:
    * - enemies
    * - all
    * - ally/self -> protective wall and other things. Maybe a buff of some sort.*/

    public FrostLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, Color color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.DEFENCE;
    }

    public FrostLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.DEFENCE;
        color = new Color(141, 251, 255);
    }

    public FrostLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.DEFENCE;
        color = new Color(141, 251, 255);
    }

    private int tickCounter = 0;
    private int seconds = 10*20; //TODO Configable

    private void checkSafety(){
        //TODO configable
        if(this.power_multiplier > Config.BLAZING_MAX_POWER){
            power_multiplier = Config.BLAZING_MAX_POWER;
        }
        if(this.power_multiplier < Config.BLAZING_MIN_POWER){
            power_multiplier = Config.BLAZING_MIN_POWER;
        }
        if(this.duration > Config.BLAZING_MAX_DURATION){
            this.duration = Config.BLAZING_MAX_DURATION;
        }
        if(this.duration < Config.BLAZING_MIN_DURATION){
            this.duration = Config.BLAZING_MIN_DURATION;
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
        caster.getWorld().playSound(caster, caster.getBlockPos(), LightSounds.FROST_LIGHT, SoundCategory.AMBIENT, 1, 1);

        if(!caster.getWorld().isClient){
            //TODO implement random rotation / correct rotation based on looking direction
            StructurePlacer placer = new StructurePlacer((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "frost_wall"), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1.0f, new BlockPos(-2, -1, -2));
            placer.loadStructure((ServerWorld) caster.getWorld());
            /*StructurePlacer placer = new StructurePlacer((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "frost_light"), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1.0f, new BlockPos(-4, -3, -3));
            placer.loadStructure((ServerWorld) caster.getWorld());*/
        }


        LightParticlesUtil.spawnSnowflake((ServerPlayerEntity) caster, caster.getPos().add(0, 2, 0));

        //TODO fix ghost blocks
        boolean self_or_allies = (LIGHT_COMPONENT.get(caster).getTargets().equals(TargetType.SELF) || LIGHT_COMPONENT.get(caster).getTargets().equals(TargetType.ALLIES));
        for(LivingEntity target : this.targets){
            target.playSound(LightSounds.FROST_LIGHT, 1, 1);
            if(self_or_allies){
                target.addStatusEffect(new StatusEffectInstance(LightEffects.FREEZE_RESISTANCE, this.duration));
            }else{
                BlockPos norm_pos = target.getBlockPos();
                target.teleport(norm_pos.getX(), norm_pos.getY(), norm_pos.getZ());
                target.addStatusEffect(new StatusEffectInstance(LightEffects.FROST, this.duration*20, 0, false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, this.duration*20, 0, false, false));

                Direction facing = target.getHorizontalFacing();
                int state = 0;
                if(facing.equals(Direction.NORTH)){
                    state = 0;
                }else if(facing.equals(Direction.EAST)){
                    state = 1;
                }else if(facing.equals(Direction.SOUTH)){
                    state = 2;
                }else if(facing.equals(Direction.WEST)){
                    state = 3;
                }

                target.getWorld().setBlockState(norm_pos, LightBlocks.FROZEN_PLAYER_BOTTOM_BLOCK.getDefaultState(), state);
                target.getWorld().setBlockState(norm_pos.add(0,1,0), LightBlocks.FROZEN_PLAYER_TOP_BLOCK.getDefaultState(), state);

                if(!caster.getWorld().isClient){
                    LightParticlesUtil.spawnLightTypeParticle(LightParticles.FROSTLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
                    LightParticlesUtil.spawnLightTypeParticle(LightParticles.FROSTLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
                }
                target.damage(DamageSource.FREEZE, (float) this.power_multiplier);
            }

        }

    }
}
