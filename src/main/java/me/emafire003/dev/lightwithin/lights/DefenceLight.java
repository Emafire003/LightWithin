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
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

import java.util.List;

public class DefenceLight extends InnerLight {

    public static final Item INGREDIENT = Items.TURTLE_SCUTE;

    public DefenceLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.DEFENCE;
    }

    public DefenceLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.DEFENCE;
        color = "427f3b";
    }

    public DefenceLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.DEFENCE;
        //color = "#427f3b";
        color = "defence";
    }

    private void checkSafety(){
        if(this.power_multiplier > BalanceConfig.DEFENSE_MAX_POWER){
            power_multiplier = BalanceConfig.DEFENSE_MAX_POWER;
        }
        if(this.duration > BalanceConfig.DEFENSE_MAX_DURATION){
            this.duration = BalanceConfig.DEFENSE_MAX_DURATION;
        }
        int max_duration = BalanceConfig.DEFENSE_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.DEFENSE_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(this.duration > max_duration){
            this.duration = max_duration;
        }
        if(this.power_multiplier < BalanceConfig.DEFENSE_MIN_POWER){
            this.power_multiplier = BalanceConfig.DEFENSE_MIN_POWER;
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

        caster.getWorld().playSound(caster.getX(), caster.getY(), caster.getZ(), LightSounds.DEFENSE_LIGHT, SoundCategory.PLAYERS, 1, 1, true);
        for(LivingEntity target : this.targets){
            //target.playSound(LightSounds.DEFENSE_LIGHT, 1, 1);
            //LightParticlesUtil.spawnLightTypeParticle(LightParticles.DEFENSELIGHT_PARTICLE, target);
            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.DEFENSELIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
            }
            if(target.equals(caster) && component.getTargets().equals(TargetType.ALLIES)){
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) (this.power_multiplier/Config.DIV_SELF), false, false));
            }else{
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) this.power_multiplier, false, false));
            }
        }

    }
}
