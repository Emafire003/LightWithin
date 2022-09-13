package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.util.fabridash.FabriDash;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.*;

public class WindLight extends InnerLight {

    /*Possible triggers:
       - self low health
       - allies low health
       - surrounded+++
       - NEEDS to have in hand dirt/rock or be around them.
     */

    /*Possible targets:
    * - self, -> dash away + enemis pushed away/high velocity and jump
    * - ally/self -> launch up in the air and give jump boost velocity and
    * - ALL MAYBE, but not sure. -> everything/one boosted away*/

    public WindLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, Color color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.EARTHEN;
    }

    public WindLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.EARTHEN;
        color = new Color(210, 243, 255);
    }

    public WindLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.EARTHEN;
        color = new Color(210, 243, 255);
    }

    private void checkSafety(){
        if(this.power_multiplier > Config.FROST_MAX_POWER){
            power_multiplier = Config.FROST_MAX_POWER;
        }
        if(this.power_multiplier < Config.FROST_MIN_POWER){
            power_multiplier = Config.FROST_MIN_POWER;
        }
        if(this.duration > Config.FROST_MAX_DURATION){
            this.duration = Config.FROST_MAX_DURATION;
        }
        if(this.duration < Config.FROST_MIN_DURATION){
            this.duration = Config.FROST_MIN_DURATION;
        }
    }

    @Override
    public void execute(){
        checkSafety();
        if(this.rainbow_col){
            ColoredGlowLib.setRainbowColorToEntity(this.caster, true);
        }else{
            ColoredGlowLib.setColorToEntity(this.caster, this.color);
        }

        caster.getWorld().playSound(caster, caster.getBlockPos(), LightSounds.WIND_LIGHT, SoundCategory.AMBIENT, 1, 1);
        LightComponent component = LIGHT_COMPONENT.get(caster);
        //Will create a ravine under the enemies feet, and will also damage them and apply mining fatigue
        if(component.getTargets().equals(TargetType.OTHER)){
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.WINDLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
            ((ServerPlayerEntity )caster).getWorld().spawnParticles(((ServerPlayerEntity )caster), ParticleTypes.CLOUD, false, caster.getX(), caster.getY()+1, caster.getZ(), 65, 0, 0.2, 0, 0.35);
            for(LivingEntity target : this.targets){
                FabriDash.dash(target, (float) this.power_multiplier, true);
                ((ServerPlayerEntity )caster).getWorld().spawnParticles(((ServerPlayerEntity )caster), ParticleTypes.CLOUD, false, caster.getX(), caster.getY()+1, caster.getZ(), 65, 0, 0.2, 0, 0.35);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, this.duration*20, (int) (this.power_multiplier/2), false, true));
            }
        }
        if(component.getTargets().equals(TargetType.ALLIES)){
            //oldtarget and stuuf prevent generating multiple structures in the same area
            for(LivingEntity target : this.targets){

                target.playSound(LightSounds.WIND_LIGHT, 0.9f, 1);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, this.duration*20, (int) (this.power_multiplier), false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, this.duration*20, (int) (this.power_multiplier), false, false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, this.duration*20, (int) (this.power_multiplier/2.5), false, false));

                LightParticlesUtil.spawnLightTypeParticle(LightParticles.WINDLIGHT_PARTICLE, (ServerWorld) target.getWorld(), target.getPos());
            }
            //Depending on the level it will spawn a small moat and pillar around the user, a big pillar only and a big pillar with a big moat.
            //And will also give Solid Rock effect to self, making the player more resistant to knokback
        }else if(component.getTargets().equals(TargetType.SELF)) {
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.WINDLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());

            ((ServerPlayerEntity )caster).getWorld().spawnParticles(((ServerPlayerEntity )caster), ParticleTypes.CLOUD, false, caster.getX(), caster.getY()+1, caster.getZ(), 200, 0.1, 0.2, 0.1, 0.35);

            FabriDash.dash(caster, (float) this.power_multiplier, false);
            caster.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, this.duration*20, 0, false, false));
            caster.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, this.duration*20, (int) (this.power_multiplier/1.5), false, false));
            caster.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, this.duration*20, (int) (this.power_multiplier/1.5), false, false));

            caster.playSound(LightSounds.WIND_LIGHT, 1, 1);
        }

    }

}
