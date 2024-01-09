package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

import java.util.List;

public class DefenseLight extends InnerLight {

    /*Possible triggers:
       - self low health
       - allies low health (checkable like this if type = Heal && target = allies do stuff TODO include pets in this
       - passive mobs on low health
     */

    /*Triggers when:
    * - Low armor && less than 50% HP
    * - Low armor && surrounded
    * - Armor breaking && less than 50% HP
    *
    * - when facing a boss (a thing that has a > 150 HP) && HP < 55%
    * Applies to allies*/

    /*Possible targets:
    * - self
    * - allies
    * - Passive mobs & self*/

    public DefenseLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.DEFENCE;
    }

    public DefenseLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.DEFENCE;
        color = "427f3b";
    }

    public DefenseLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.DEFENCE;
        color = "427f3b";
    }

    private void checkSafety(){
        if(this.power_multiplier > Config.DEFENSE_MAX_POWER){
            power_multiplier = Config.DEFENSE_MAX_POWER;
        }
        if(this.duration > Config.DEFENSE_MAX_DURATION){
            this.duration = Config.DEFENSE_MAX_DURATION;
        }
        if(this.power_multiplier > Config.DEFENSE_MAX_POWER*2/3 && this.duration > Config.DEFENSE_MAX_DURATION*7/10){
            this.duration = Config.DEFENSE_MAX_DURATION*7/10;
        }
        int max_duration = Config.DEFENSE_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (Config.DEFENSE_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(this.duration > max_duration){
            this.duration = max_duration;
        }
        if(this.power_multiplier < Config.DEFENSE_MIN_POWER){
            this.power_multiplier = Config.DEFENSE_MIN_POWER;
        }
    }


    @Override
    public void execute(){
        checkSafety();

        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            if(this.rainbow_col){
                CGLCompat.getLib().setRainbowColorToEntity(this.caster, true);
            }else{
                CGLCompat.getLib().setColorToEntity(this.caster, CGLCompat.fromHex(this.color));
            }
        }

        caster.getWorld().playSound(caster.getX(), caster.getY(), caster.getZ(), LightSounds.DEFENSE_LIGHT, SoundCategory.AMBIENT, 1, 1, true);
        for(LivingEntity target : this.targets){
            //target.playSound(LightSounds.DEFENSE_LIGHT, 1, 1);
            //LightParticlesUtil.spawnLightTypeParticle(LightParticles.DEFENSELIGHT_PARTICLE, target);
            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.DEFENSELIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
            }
            if(target.equals(caster) && LightWithin.LIGHT_COMPONENT.get(caster).getTargets().equals(TargetType.ALLIES)){
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) (this.power_multiplier/Config.DIV_SELF), false, false));
            }else{
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) this.power_multiplier, false, false));
            }
        }

    }
}
