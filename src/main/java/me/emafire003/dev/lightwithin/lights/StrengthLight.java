package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

import java.util.List;

public class StrengthLight extends InnerLight {

    /*Possible triggers:
       - self low health
       - allies low health (checkable like this if type = Heal && target = allies do stuff TODO include pets in this
       - passive mobs on low health
     */

    /*Triggers:
    * - When surrounded && hp < 50%
    * - When HP < 25%
    * - When weakened
    * - when facing a boss (a thing that has more than 150 HP) && HP < 55% [self]
    * - (upon attacking a more dangerous foe, aka either more life or more
    *
    * Applies to allies too*/

    /*Possible targets:
    * - self
    * - allies
    */

    public StrengthLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.STRENGTH;
    }

    public StrengthLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.STRENGTH;
        color = "cc082c";
    }

    public StrengthLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.STRENGTH;
        color = "cc082c";
    }

    private void checkSafety(){
        if(this.power_multiplier > Config.DEFENSE_MAX_POWER){
            power_multiplier = Config.DEFENSE_MAX_POWER;
        }
        int max_duration = Config.STRENGTH_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (Config.STRENGTH_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(this.duration > max_duration){
            this.duration = max_duration;
        }
        if(this.power_multiplier > Config.DEFENSE_MAX_POWER*2/3 && this.duration > Config.DEFENSE_MAX_DURATION*7/10){
            this.duration = Config.DEFENSE_MAX_DURATION*7/10;
        }//TODO wtf is this? Again?
        if(this.duration < Config.DEFENSE_MIN_DURATION){
            this.duration = Config.DEFENSE_MIN_DURATION;
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

        caster.getWorld().playSound(caster.getX(), caster.getY(), caster.getZ(), LightSounds.STRENGTH_LIGHT, SoundCategory.AMBIENT, 1, 1, true);
        for(LivingEntity target : this.targets){
            //target.playSound(LightSounds.STRENGTH_LIGHT, 1, 1);
            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.STRENGTHLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
            }
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) this.power_multiplier, false, false));
        }
    }
}
