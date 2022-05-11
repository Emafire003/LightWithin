package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;

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
        type = InnerLightType.HEAL;
        checkSafety();
    }

    public HealLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, LivingEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.HEAL;
        color = new Color(255, 66, 21);
        checkSafety();
    }

    public HealLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, LivingEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.HEAL;
        color = new Color(255, 66, 21);
        checkSafety();
    }

    private void checkSafety(){
        if(this.power_multiplier > 8){
            this.power_multiplier = 8;
        }
        if(this.duration < 4){
            this.duration = 4;
        }
        if(this.duration > 6 && this.power_multiplier > 5){
               this.duration = 6;

        }
        if(this.duration > 8 && this.power_multiplier > 4){
            this.duration = 8;
        }
        if(this.power_multiplier <= 1){
            power_multiplier = 2;
        }
    }


    @Override
    public void execute(){
        if(this.rainbow_col){
            ColoredGlowLib.setRainbowColorToEntity(this.caster, true);
        }else{
            ColoredGlowLib.setColorToEntity(this.caster, this.color);
        }
        caster.getWorld().playSound((PlayerEntity) caster, caster.getBlockPos(), LightSounds.HEAL_LIGHT, SoundCategory.AMBIENT, 1,1);
        for(LivingEntity target : this.targets){
            target.playSound(LightSounds.HEAL_LIGHT, 1, 1);
            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.HEALLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
            }
            //LightParticlesUtil.spawnLightTypeParticle(LightParticles.HEALLIGHT_PARTICLE, target);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, this.duration*20, (int) this.power_multiplier, false, false));
        }
    }
}
