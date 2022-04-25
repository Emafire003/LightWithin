package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;
import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

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

    public HealLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, Color color, LivingEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.HEAL;
    }

    public HealLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, LivingEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.HEAL;
        color = new Color(255, 66, 21);
    }

    public HealLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, LivingEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.HEAL;
        color = new Color(255, 66, 21);
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
        //caster.getWorld().playSound((PlayerEntity) caster, caster.getBlockPos(), LightSounds.HEAL_LIGHT, SoundCategory.PLAYERS, 1f, 1f);
        caster.playSound(LightSounds.HEAL_LIGHT, 1f, 0.9f);
        for(LivingEntity target : this.targets){
            //TODO maybe configable, default amout: 1.5 hearts
            //ok it works :D
            //target.heal((float) (3*this.power_multiplier));
            //target.playSound(LightSounds.HEAL_LIGHT, 1f, 1f);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, this.duration*20, (int) this.power_multiplier, false, false));
        }
    }
}
