package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;

public class BlazingLight extends InnerLight {

    /*Possible triggers:
       - self low health
       - allies low health (checkable like this if type = Heal && target = allies do stuff TODO include pets in this
       - passive mobs on low health ???
       - surrounded++
       - NEEDS to be on fire, hold a torch, near heat emitting stuff.
       - Ally dying?
     */

    /*Possible targets:
    * - enemies
    * - all (more powerful tho)
    * - mobs only maybe undead, Fire coloumn on their heads. Maybe only players?
    * - ally -> if gets it summons wall
    * - self (rare) -> same ^*/

    public BlazingLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, Color color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.DEFENCE;
    }

    public BlazingLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.DEFENCE;
        color = new Color(200, 128, 60);
    }

    public BlazingLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.DEFENCE;
        color = new Color(200, 128, 60);
    }

    private void checkSafety(){
        //TODO configable
        if(this.power_multiplier > 5){
            power_multiplier = 5;
        }
        if(this.power_multiplier < 1){
            power_multiplier = 1;
        }
        if(this.duration < 5){
            this.duration = 5;
        }
    }

    private double r = 0.5;

    @Override
    public void execute(){
        checkSafety();
        if(this.rainbow_col){
            ColoredGlowLib.setRainbowColorToEntity(this.caster, true);
        }else{
            ColoredGlowLib.setColorToEntity(this.caster, this.color);
        }
        caster.getWorld().playSound(caster, caster.getBlockPos(), LightSounds.DEFENSE_LIGHT, SoundCategory.AMBIENT, 1, 1);


        //TODO caster.isFireImmune() set it to return true when the player has the effect active
        LightComponent component = LIGHT_COMPONENT.get(caster);
        if(component.getTargets().equals(TargetType.ALL)){
            power_multiplier = power_multiplier + 1; //TODO configurable
        }
        for(LivingEntity target : this.targets){
            //TODO swap with burn
            target.playSound(LightSounds.BLAZING_LIGHT, 1, 1);


            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.BLAZINGLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
            }
            
            //TODO make the chance configable
            //it's basicly a crit, unique for now to the blazing light Currently 10 percent
            if(caster.getRandom().nextInt(10) == 1){
                //TODO does not work either, also the checkBlazing
                target.damage(DamageSource.IN_FIRE, (float) (2*this.power_multiplier*1.5));//TODO config the multiplier of the multiplier?
                target.setOnFireFor(this.duration*2);//TODO configure the fire seconds bonus?
                target.playSound(LightSounds.LIGHT_CRIT, 1, 1);
                LightParticlesUtil.spawnDescendingColumn((ServerPlayerEntity) caster, ParticleTypes.FLAME, target.getPos().add(0,3,0));
            }else{
                target.setOnFireFor(this.duration);//TODO configure the fire seconds bonus?
                target.damage(DamageSource.IN_FIRE, (float) (2*this.power_multiplier));//TODO config the multiplier of the multiplier?

            }
        }
        LightParticlesUtil.spawnLightTypeParticle(LightParticles.BLAZINGLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
        
        ServerTickEvents.END_SERVER_TICK.register((server -> {
            if(r < LightWithin.box_expansion_amount){
                r = r + 0.5;
                LightParticlesUtil.spawnCircle(caster.getPos().add(0,0.7,0), r, 100, ParticleTypes.FLAME, (ServerWorld) caster.getWorld());
            }
        }));

    }
}
