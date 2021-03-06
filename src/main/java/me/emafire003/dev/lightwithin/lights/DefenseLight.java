package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

import java.util.List;

public class DefenseLight extends InnerLight {

    /*Possible triggers:
       - self low health
       - allies low health (checkable like this if type = Heal && target = allies do stuff TODO include pets in this
       - passive mobs on low health
     */

    /*Possible targets:
    * - self
    * - allies
    * - Passive mobs & self*/

    public DefenseLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, Color color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.DEFENCE;
    }

    public DefenseLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.DEFENCE;
        color = new Color(67, 128, 60);
    }

    public DefenseLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.DEFENCE;
        color = new Color(67, 128, 60);
    }

    private void checkSafety(){
        if(this.power_multiplier > Config.DEFENSE_MAX_POWER){
            power_multiplier = Config.DEFENSE_MAX_POWER;
        }
        if(this.duration > Config.DEFENSE_MAX_DURATION){
            this.duration = Config.DEFENSE_MAX_DURATION;
        }
        if(this.power_multiplier > Config.DEFENSE_MAX_POWER*2/3 && this.duration > Config.DEFENSE_MAX_DURATION*7/10){
            this.duration = Config.DEFENSE_MAX_DURATION*7/10;
        }
        if(this.duration < Config.DEFENSE_MIN_DURATION){
            this.duration = Config.DEFENSE_MIN_DURATION;
        }
        if(this.power_multiplier < Config.DEFENSE_MIN_POWER){
            this.power_multiplier = Config.DEFENSE_MIN_POWER;
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
        caster.getWorld().playSound(caster, caster.getBlockPos(), LightSounds.DEFENSE_LIGHT, SoundCategory.AMBIENT, 1, 1);
        for(LivingEntity target : this.targets){
            target.playSound(LightSounds.DEFENSE_LIGHT, 1, 1);
            //LightParticlesUtil.spawnLightTypeParticle(LightParticles.DEFENSELIGHT_PARTICLE, target);
            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.DEFENSELIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
            }
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) this.power_multiplier, false, false));
        }

    }
}
