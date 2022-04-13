package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import net.minecraft.entity.LivingEntity;

import java.util.List;

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
        type = InnerLightTypes.HEAL;
    }

    public HealLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, LivingEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightTypes.HEAL;
        color = new Color(255, 66, 21);
    }

    public HealLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, LivingEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightTypes.HEAL;
        color = new Color(255, 66, 21);
    }


    @Override
    public void execute(){
        LOGGER.info("Executing the stuff!");
        LOGGER.info("Type: " + this.type);
        if(this.rainbow_col){
            ColoredGlowLib.setRainbowColorToEntity(this.caster, true);
        }else{
            ColoredGlowLib.setColorToEntity(this.caster, this.color);
        }
        for(LivingEntity target : this.targets){
            //TODO maybe configable, default amout: 1.5 hearts
            //ok it works :D
            target.heal((float) (3*this.power_multiplier));
            LOGGER.info("healed: " + 3*this.power_multiplier);
        }
    }
}
