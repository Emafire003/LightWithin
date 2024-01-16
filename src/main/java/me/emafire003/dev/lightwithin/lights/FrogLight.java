package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

import java.util.List;

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

    public FrogLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.FROG;
    }

    public FrogLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.FROG;
        color = "c46931";
    }

    public FrogLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.FROG;
        color = "c46931";
    }

    private void checkSafety(){
       LOGGER.info("Oh frog easter egg has been activated!");
    }

    @Override
    public void execute(){
        checkSafety();
        LightComponent component = LightWithin.LIGHT_COMPONENT.get(caster);
        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            if(this.rainbow_col){
                CGLCompat.getLib().setRainbowColorToEntity(this.caster, true);
            }else{
                CGLCompat.getLib().setColorToEntity(this.caster, CGLCompat.fromHex(this.color));
            }
        }

        Random random = caster.getRandom();
        int frogs = (int) (this.power_multiplier+random.nextBetween(0, 5));
        LightParticlesUtil.spawnLightTypeParticle(LightParticles.FROGLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
        for(int i = 0; i<frogs; i++){
            for(LivingEntity target : this.targets){
                target.playSound(SoundEvents.ENTITY_FROG_HURT, 1, 0.8f);
                if(!target.isSpectator()){
                    target.damage(caster.getWorld().getDamageSources().magic(), frogs);
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
        }

    }

}
