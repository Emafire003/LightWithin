package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class StrengthLight extends InnerLight {

    public static final Item INGREDIENT = Items.BLAZE_POWDER;
    public StrengthLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.STRENGTH;
    }

    public StrengthLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.STRENGTH;
        color = "#cc082c";
    }

    public StrengthLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.STRENGTH;
        //color = "#cc082c";
        color = "strength";
    }

    private void checkSafety(){
        if(this.power_multiplier > BalanceConfig.STRENGTH_MAX_POWER){
            power_multiplier = BalanceConfig.STRENGTH_MAX_POWER;
        }
        int max_duration = BalanceConfig.STRENGTH_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.STRENGTH_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(this.duration > max_duration){
            this.duration = max_duration;
        }
        if(this.duration < BalanceConfig.STRENGTH_MIN_DURATION){
            this.duration = BalanceConfig.STRENGTH_MIN_DURATION;
        }
        if(this.power_multiplier < BalanceConfig.STRENGTH_MIN_POWER){
            this.power_multiplier = BalanceConfig.STRENGTH_MIN_POWER;
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

        caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()), LightSounds.STRENGTH_LIGHT, SoundCategory.PLAYERS, 1f, 1f);
        for(LivingEntity target : this.targets){
            //target.playSound(LightSounds.STRENGTH_LIGHT, 1, 1);
            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.STRENGTHLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
            }
            if(component.getTargets().equals(TargetType.VARIANT)){
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) this.power_multiplier, false, false));
            }else if(target.equals(caster) && component.getTargets().equals(TargetType.ALLIES)){
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) (this.power_multiplier/Config.DIV_SELF), false, false));
            } else{
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) this.power_multiplier, false, false));
            }
        }
    }
}
