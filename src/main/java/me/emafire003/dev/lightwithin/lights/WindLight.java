package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.util.fabridash.FabriDash;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.MOD_ID;

public class WindLight extends InnerLight {

    public static final Item INGREDIENT = Items.FEATHER;
    public static final TagKey<Block> WIND_TRIGGER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "wind_trigger_blocks"));

    public WindLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.WIND;
    }

    public WindLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.WIND;
        color = "#d1f2ff";
    }

    public WindLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.WIND;
        //color = "#d1f2ff";
        color = "wind";
    }

    private void checkSafety(){
        if(this.power_multiplier > BalanceConfig.WIND_MAX_POWER){
            power_multiplier = BalanceConfig.WIND_MAX_POWER;
        }
        if(this.power_multiplier < BalanceConfig.WIND_MIN_POWER){
            power_multiplier = BalanceConfig.WIND_MIN_POWER;
        }
        int max_duration = BalanceConfig.WIND_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.WIND_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(this.duration > max_duration){
            this.duration = max_duration;
        }
        if(this.duration < BalanceConfig.WIND_MIN_DURATION){
            this.duration = BalanceConfig.WIND_MIN_DURATION;
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

        if(caster.getWorld().isClient()){
            return;
        }

        caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()), LightSounds.WIND_LIGHT, SoundCategory.PLAYERS, 1f, 1f);

        //caster.getWorld().playSound(caster, caster.getBlockPos(), LightSounds.WIND_LIGHT, SoundCategory.PLAYERS, 1, 1);
        ServerWorld world = (ServerWorld) (caster).getWorld();
        //If the light target is OTHER it will blow away every entity in radius
        if(component.getTargets().equals(TargetType.ALL)){
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
                if(target.equals(caster)){
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, this.duration*20, (int) ((this.power_multiplier/2)/Config.DIV_SELF), false, false));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, this.duration*20, (int) ((this.power_multiplier/2)/Config.DIV_SELF), false, false));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, this.duration*20, 0, false, false));
                }else{
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, this.duration*20, (int) (this.power_multiplier/2), false, false));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, this.duration*20, (int) (this.power_multiplier/2), false, false));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, this.duration*20, 0, false, false));
                }

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
