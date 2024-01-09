package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.util.fabridash.FabriDash;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.*;

public class WindLight extends InnerLight {

    /*Possible triggers:
       - self low health
       - allies low health
       - surrounded+++
     */

    /* Triggers:
    * - Falling from more than 5 blocks
    * - Surrounded && HP < 60%
    * - HP < 25%
    * - when at height of more than 128 blocks + HP < 50%
    * */

    /*Possible targets:
    * - self, -> dash away + enemis pushed away/high velocity and jump
    * - ally/self -> launch up in the air and give jump boost velocity and
    * - ALL MAYBE, but not sure. -> everything/one boosted away*/

    public WindLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.WIND;
    }

    public WindLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.WIND;
        color = "d1f2ff";
    }

    public WindLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.WIND;
        color = "d1f2ff";
    }

    private void checkSafety(){
        if(this.power_multiplier > Config.WIND_MAX_POWER){
            power_multiplier = Config.WIND_MAX_POWER;
        }
        if(this.power_multiplier < Config.WIND_MIN_POWER){
            power_multiplier = Config.WIND_MIN_POWER;
        }
        int max_duration = Config.WIND_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (Config.WIND_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(this.duration > max_duration){
            this.duration = max_duration;
        }
        if(this.duration < Config.WIND_MIN_DURATION){
            this.duration = Config.WIND_MIN_DURATION;
        }
    }

    @Override
    public void execute(){
        checkSafety();
    caster.sendMessage(Text.literal("Light triggered"));
        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            if(this.rainbow_col){
                CGLCompat.getLib().setRainbowColorToEntity(this.caster, true);
            }else{
                CGLCompat.getLib().setColorToEntity(this.caster, CGLCompat.fromHex(this.color));
            }
        }

        caster.getWorld().playSound(caster.getX(), caster.getY(), caster.getZ(), LightSounds.WIND_LIGHT, SoundCategory.AMBIENT, 1, 1, true);
        //caster.getWorld().playSound(caster, caster.getBlockPos(), LightSounds.WIND_LIGHT, SoundCategory.AMBIENT, 1, 1);
        LightComponent component = LIGHT_COMPONENT.get(caster);
        ServerWorld world = (ServerWorld) (caster).getWorld();
        //If the light target is OTHER it will blow away every entity in radious
        if(component.getTargets().equals(TargetType.VARIANT)){
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.WINDLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
            world.spawnParticles(((ServerPlayerEntity )caster), ParticleTypes.CLOUD, false, caster.getX(), caster.getY()+1, caster.getZ(), 65, 0, 0.2, 0, 0.35);
            for(LivingEntity target : this.targets){
                FabriDash.dash(target, (float) this.power_multiplier, true);
                world.spawnParticles(((ServerPlayerEntity )caster), ParticleTypes.CLOUD, false, caster.getX(), caster.getY()+1, caster.getZ(), 65, 0, 0.2, 0, 0.35);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, this.duration*20, (int) (this.power_multiplier/2), false, true));
            }
        }
        //If the target is allies, a series of boost will be given to allies and self
        else if(component.getTargets().equals(TargetType.ALLIES)){
            //oldtarget and stuff prevent generating multiple structures in the same area
            for(LivingEntity target : this.targets){

                //TODO these are allies, should i still play it? no
                //target.playSound(LightSounds.WIND_LIGHT, 0.9f, 1);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, this.duration*20, (int) (this.power_multiplier/2.5), false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, this.duration*20, (int) (this.power_multiplier/2.5), false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, this.duration*20, (int) (this.power_multiplier/2.5), false, false));

                LightParticlesUtil.spawnLightTypeParticle(LightParticles.WINDLIGHT_PARTICLE, (ServerWorld) target.getWorld(), target.getPos());
            }
        }//If the target is self, the player will perform a dash (will be launched forward)
        else if(component.getTargets().equals(TargetType.SELF)) {
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.WINDLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());

            world.spawnParticles(((ServerPlayerEntity )caster), ParticleTypes.CLOUD, false, caster.getX(), caster.getY()+1, caster.getZ(), 200, 0.1, 0.2, 0.1, 0.35);

            FabriDash.dash(caster, (float) this.power_multiplier, false);
            caster.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, this.duration*20, 0, false, false));
            caster.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, this.duration*20, (int) (this.power_multiplier/1.5), false, false));
            caster.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, this.duration*20, (int) (this.power_multiplier/1.5), false, false));

            //caster.playSound(LightSounds.WIND_LIGHT, 1, 1);
        }

    }

}
