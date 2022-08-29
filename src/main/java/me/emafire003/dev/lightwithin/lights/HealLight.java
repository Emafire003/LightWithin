package me.emafire003.dev.lightwithin.lights;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

import java.util.HashMap;
import java.util.List;

public class HealLight extends InnerLight {

    /*Possible triggers:
       - self low health
       - allies low health (checkable like this if type = Heal && target = allies do stuff TODO include pets in this
       - passive mobs on low health
     */

    /*Possible targets:
    * - self
    * - allies
    * - Passive mobs & self*/

    public HealLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, Color color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.HEAL;
        checkSafety();
    }

    public HealLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.HEAL;
        color = new Color(255, 66, 21);
        checkSafety();
    }

    public HealLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.HEAL;
        color = new Color(255, 66, 21);
        checkSafety();
    }

    private void checkSafety(){
        if(this.power_multiplier > Config.HEAL_MAX_POWER){
            this.power_multiplier = Config.HEAL_MAX_POWER;
        }
        if(this.duration > Config.HEAL_MAX_DURATION){
            this.duration = Config.HEAL_MAX_DURATION;
        }
        if(this.duration > Config.HEAL_MAX_DURATION*4/5 && this.power_multiplier > Config.HEAL_MAX_POWER/2){ //maxdur-1/5 && multiplier > maxpow/2 = maxdur-1/5
            this.duration = Config.HEAL_MAX_DURATION*4/5;
        }

        if(this.power_multiplier < Config.HEAL_MIN_POWER){
            power_multiplier = Config.HEAL_MIN_POWER;
        }
        if(this.duration < Config.HEAL_MIN_DURATION){
            this.duration = Config.HEAL_MIN_DURATION;
        }
    }


    @Override
    public void execute(){
        if(this.rainbow_col){
            ColoredGlowLib.setRainbowColorToEntity(this.caster, true);
        }else{
            ColoredGlowLib.setColorToEntity(this.caster, this.color);
        }
        caster.getWorld().playSound(caster, caster.getBlockPos(), LightSounds.HEAL_LIGHT, SoundCategory.AMBIENT, 1,1);
        for(LivingEntity target : this.targets){
            target.playSound(LightSounds.HEAL_LIGHT, 1, 1);
            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.HEALLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
            }
            //LightParticlesUtil.spawnLightTypeParticle(LightParticles.HEALLIGHT_PARTICLE, target);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) this.power_multiplier, false, false));
        }
    }
}
