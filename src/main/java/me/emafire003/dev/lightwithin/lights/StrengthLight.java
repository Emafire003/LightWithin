package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

import java.util.List;

public class StrengthLight extends InnerLight {

    /*Possible triggers:
       - self low health
       - allies low health (checkable like this if type = Heal && target = allies do stuff TODO include pets in this
       - passive mobs on low health
     */

    /*Possible targets:
    * - self
    * - allies
    * - Passive mobs & self*/

    public StrengthLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, Color color, LivingEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.STRENGTH;
    }

    public StrengthLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, LivingEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.STRENGTH;
        color = new Color(203, 9, 71);
    }

    public StrengthLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, LivingEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.STRENGTH;
        color = new Color(203, 9, 71);
    }

    private void checkSafety(){
        //TODO configable
        if(this.power_multiplier > 6){
            power_multiplier = 6;
        }
        if(this.power_multiplier > 4 && this.duration > 7){
            this.duration = 7;
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
        caster.getWorld().playSound((PlayerEntity) caster, caster.getBlockPos(), LightSounds.STRENGTH_LIGHT, SoundCategory.AMBIENT, 1, 1);
        for(LivingEntity target : this.targets){
            target.playSound(LightSounds.STRENGTH_LIGHT, 1, 1);
            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.STRENGTHLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
            }
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, this.duration*20, (int) this.power_multiplier, false, false));
        }
    }
}
