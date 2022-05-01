package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;

import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

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

    public DefenseLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, Color color, LivingEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.DEFENCE;
    }

    public DefenseLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, LivingEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.DEFENCE;
        color = new Color(67, 128, 60);
    }

    public DefenseLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, LivingEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.DEFENCE;
        color = new Color(67, 128, 60);
    }


    @Override
    public void execute(){
        LOGGER.info("Executing the stuff!");
        LOGGER.info("Type: " + this.type + " duration " + this.duration + " power " + this.power_multiplier);
        if(this.rainbow_col){
            ColoredGlowLib.setRainbowColorToEntity(this.caster, true);
        }else{
            ColoredGlowLib.setColorToEntity(this.caster, this.color);
        }
        caster.getWorld().playSound((PlayerEntity) caster, caster.getBlockPos(), LightSounds.DEFENSE_LIGHT, SoundCategory.AMBIENT, 1, 1);
        for(LivingEntity target : this.targets){
            target.playSound(LightSounds.DEFENSE_LIGHT, 1, 1);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, this.duration*20, (int) this.power_multiplier, false, false));
        }
        //((PlayerEntity) caster).addEnchantedHitParticles(caster);
        if(caster instanceof ServerPlayerEntity){
            LightParticlesUtil.spawnDefenseParticles((ServerPlayerEntity) caster);
        }

    }
}
