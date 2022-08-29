package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.structureplacerapi.StructurePlacerAPI;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;
import static me.emafire003.dev.lightwithin.LightWithin.MOD_ID;

public class BlazingLight extends InnerLight {

    /*Possible triggers:
       - self low health
       - allies low health (checkable like this if type = Heal && target = allies do stuff TODO include pets in this
       - surrounded++
       - NEEDS to be on fire, hold a torch, near heat emitting stuff.
       - Ally dying?
     */

    /*Possible targets:
    * - enemies
    * - all (more powerful tho)
*/

    public BlazingLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, Color color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.BLAZING;
    }

    public BlazingLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.BLAZING;
        color = new Color(234, 71, 16);
    }

    public BlazingLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.BLAZING;
        color = new Color(234, 71, 16);
    }

    private double crit_multiplier = 1.5;
    private double r = 0.5;

    private void checkSafety(){
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
        if(Config.BLAZING_CRIT_MULTIPLIER > 1){
            crit_multiplier = Config.BLAZING_CRIT_MULTIPLIER;
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
        caster.getWorld().playSound(caster, caster.getBlockPos(), LightSounds.BLAZING_LIGHT, SoundCategory.AMBIENT, 1, 1);
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), 0, false, false));

        LightComponent component = LIGHT_COMPONENT.get(caster);
        if(component.getTargets().equals(TargetType.ALL)){
            power_multiplier = power_multiplier + Config.BLAZING_ALL_DAMAGE_BONUS;
        }
        if(Config.STRUCTURE_GRIEFING && !caster.getWorld().isClient && (component.getTargets().equals(TargetType.ALL) || component.getTargets().equals(TargetType.ENEMIES))) {
            StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "blazing_light"), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1.0f, new BlockPos(-3, -4, -3));
            placer.loadStructure();
        }
        for(LivingEntity target : this.targets){
            target.playSound(LightSounds.BLAZING_LIGHT, 1, 1);

            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.BLAZINGLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.BLAZINGLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
            }
            
            //TODO make the chance configable EDIT: Maybe not
            //it's basicly a crit, unique for now to the blazing light Currently 10 percent
            if(caster.getRandom().nextInt(10) == 1){
                target.damage(DamageSource.IN_FIRE, (float) (Config.BLAZING_DEFAULT_DAMAGE*this.power_multiplier*crit_multiplier));
                target.setOnFireFor(this.duration*Config.BLAZING_CRIT_FIRE_MULTIPLIER);
                target.playSound(LightSounds.LIGHT_CRIT, 1, 1);
                LightParticlesUtil.spawnDescendingColumn((ServerPlayerEntity) caster, ParticleTypes.FLAME, target.getPos().add(0,3,0));
                if(!caster.getWorld().isClient){
                    StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), new Identifier(MOD_ID, "fire_ring"), caster.getBlockPos());
                    placer.loadStructure();
                }
            }else{
                target.setOnFireFor(this.duration);
                target.damage(DamageSource.IN_FIRE, (float) (Config.BLAZING_DEFAULT_DAMAGE*this.power_multiplier));
            }
        }

        //to spawn the expanding circle of particles
        ServerTickEvents.END_SERVER_TICK.register((server -> {
            if(r < LightWithin.box_expansion_amount){
                r = r + 0.5;
                LightParticlesUtil.spawnCircle(caster.getPos().add(0,0.7,0), r, 100, ParticleTypes.FLAME, (ServerWorld) caster.getWorld());
            }
        }));

    }
}
