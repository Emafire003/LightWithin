package me.emafire003.dev.lightwithin.lights;


import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

import java.util.ArrayList;
import java.util.List;


public class HealLight extends InnerLight {

    /*per memoria storica, i primi appunti sulle lights li prendevo cosi:

    Possible triggers:
       - self low health
       - allies low health (checkable like this if type = Heal && target = allies do stuff TOD.O include pets in this
       - passive mobs on low health
     */

    /*Triggers when:
     * - On less that 25% hp
     * - On being poisoned
     * - (Optionally) when surrounded?
     *
     * - Those apply to allies as well
     */

    /*Possible targets:
     * - self
     * - allies
     * - Passive mobs & self*/

    public HealLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.HEAL;
        checkSafety();
    }

    public HealLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.HEAL;
        color = "ff1443";
        checkSafety();
    }

    public HealLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.HEAL;
        //this.color = "#ff4432";
        this.color = "heal";
    }

    private void checkSafety(){
        if(this.power_multiplier > BalanceConfig.HEAL_MAX_POWER){
            this.power_multiplier = BalanceConfig.HEAL_MAX_POWER;
        }
        int max_duration = BalanceConfig.HEAL_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.HEAL_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(this.duration > max_duration){
            this.duration = max_duration;
        }
        if(this.power_multiplier < BalanceConfig.HEAL_MIN_POWER){
            power_multiplier = BalanceConfig.HEAL_MIN_POWER;
        }
        if(this.duration < BalanceConfig.HEAL_MIN_DURATION){
            this.duration = BalanceConfig.HEAL_MIN_DURATION;
        }
    }


    @Override
    public void execute(){
        checkSafety();
        LightComponent component = LightWithin.LIGHT_COMPONENT.get(caster);
        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            if(this.rainbow_col){
                CGLCompat.getLib().setRainbowColor(this.caster);
            }else{
                CGLCompat.getLib().setColor(this.caster, this.color);
            }
        }
        caster.getWorld().playSound(caster.getX(), caster.getY(), caster.getZ(), LightSounds.HEAL_LIGHT, SoundCategory.AMBIENT, 1, 1, true);
        //caster.getWorld().playSound(caster, caster.getBlockPos(), LightSounds.HEAL_LIGHT, SoundCategory.AMBIENT, 1,1);

        for(LivingEntity target : this.targets){


            //TODO techincly are allies should I keep it? Nah
            //target.playSound(LightSounds.HEAL_LIGHT, 1, 1);
            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.HEALLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
            }
            //LightParticlesUtil.spawnLightTypeParticle(LightParticles.HEALLIGHT_PARTICLE, target);
            if(target.equals(caster) && component.getTargets().equals(TargetType.ALLIES)){
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) (this.power_multiplier/Config.DIV_SELF), false, false));
            }else if(component.getTargets().equals(TargetType.VARIANT)){
                List<StatusEffect> remove_status_list = new ArrayList<>();
                target.getActiveStatusEffects().forEach((statusEffect, instance) -> {
                    if(statusEffect.getCategory().equals(StatusEffectCategory.HARMFUL) && statusEffect != LightEffects.LIGHT_FATIGUE){
                        remove_status_list.add(statusEffect);
                    }
                });
                remove_status_list.forEach(target::removeStatusEffect);

                remove_status_list.clear();
            }else{
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) this.power_multiplier, false, false));
            }


        }
    }


}
