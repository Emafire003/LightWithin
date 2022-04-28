package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;

import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class StrenghtLight extends InnerLight {

    /*Possible triggers:
       - self low health
       - allies low health (checkable like this if type = Heal && target = allies do stuff TODO include pets in this
       - passive mobs on low health
     */

    /*Possible targets:
    * - self
    * - allies
    * - Passive mobs & self*/

    public StrenghtLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, Color color, LivingEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.STRENGTH;
    }

    public StrenghtLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, LivingEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.STRENGTH;
        color = new Color(203, 9, 71);
    }

    public StrenghtLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, LivingEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.STRENGTH;
        color = new Color(203, 9, 71);
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
        caster.getWorld().playSound((PlayerEntity) caster, caster.getBlockPos(), LightSounds.HEAL_LIGHT, SoundCategory.AMBIENT, 1, 1);
        for(LivingEntity target : this.targets){
            target.playSound(LightSounds.HEAL_LIGHT, 1, 1);
            ((PlayerEntity) caster).sendMessage(new LiteralText("Hello tryed to play sound from the light"), false);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, this.duration*20, (int) this.power_multiplier, false, false));
        }
    }
}
