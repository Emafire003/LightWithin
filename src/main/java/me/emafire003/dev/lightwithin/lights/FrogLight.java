package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.coloredglowlib.ColoredGlowLib;
import me.emafire003.dev.coloredglowlib.util.Color;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.lightwithin.util.fabridash.FabriDash;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;
import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class FrogLight extends InnerLight {

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

    public FrogLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, Color color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.EARTHEN;
    }

    public FrogLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.EARTHEN;
        color = new Color(196, 106, 49);
    }

    public FrogLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.EARTHEN;
        color = new Color(196, 106, 49);
    }

    private void checkSafety(){
       LOGGER.info("Oh frog easter egg has been activated!");
    }

    @Override
    public void execute(){
        checkSafety();
        if(this.rainbow_col){
            ColoredGlowLib.setRainbowColorToEntity(this.caster, true);
        }else{
            ColoredGlowLib.setColorToEntity(this.caster, this.color);
        }

        Random random = caster.getRandom();
        int frogs = (int) (this.power_multiplier+random.nextBetween(0, 5));
        LightParticlesUtil.spawnLightTypeParticle(LightParticles.FROGLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
        for(int i = 0; i<frogs; i++){
            for(LivingEntity target : this.targets){
                target.playSound(SoundEvents.ENTITY_FROG_HURT, 1, 0.8f);
                if(!target.isSpectator()){
                    target.damage(DamageSource.MAGIC, frogs);
                }
                if(target instanceof PlayerEntity){
                    ((PlayerEntity) target).sendMessage(Text.translatable("light.description.activation.frog.damage"), true);
                }
            }
            FrogEntity frog = new FrogEntity(EntityType.FROG, caster.getWorld());
            FrogVariant variant = FrogVariant.TEMPERATE;
            int v = random.nextBetween(0, 2);
            if(v == 0){
                variant = FrogVariant.COLD;
            }else if(v == 1){
                variant = FrogVariant.WARM;
            }
            frog.setVariant(variant);
            frog.setPos(caster.getX()+random.nextDouble(), caster.getY()+2, caster.getZ()+random.nextDouble());
            caster.getWorld().spawnEntity(frog);

            //TODO add proper runes etc add to the proper light set thing etc
        }

    }

}
