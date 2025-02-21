package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.forestaura_puffs.ForestPuffColor;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.particleanimationlib.effects.AnimatedBallEffect;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.emafire003.dev.lightwithin.LightWithin.*;

/*Planned stuff:

Allies:
    - Forcefield around ally which bounces off enemies and enemies' projectiles (maybe, otherwise just enemies)
    - Immunity to lightning damage
    - Entites that get in contact with the filed get zapped for tot damage based on power level
AlL:
    - Either smite at a distance, like point and summon lighting up to power level times
    - Or, summon power level times a lighting bolt when hitting a player
    - Immunity to lightning damage
Variant:
    - Summons thunderstorm? yeah could be cool like summons a localized thunderstorm which is fixed in place and randomly
    zaps/bolts entities in the area. Like a particle cloud or the thunderstorm itself but zaps stuff a lot more frequently. Like once every second. And the range is the power level

* */
public class ThunderAuraLight extends InnerLight {

    public static final Item INGREDIENT = Items.LIGHTNING_ROD; //Glowstone?

    public static final TagKey<Block> FOREST_AURA_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier(MOD_ID, "forest_aura_blocks"));

    public static final String COLOR = "AFCE23";

    public ThunderAuraLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, String color, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, color, caster, rainbow_col);
        type = InnerLightType.THUNDER_AURA;
    }

    public ThunderAuraLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster, boolean rainbow_col) {
        super(targets, cooldown_time, power_multiplier, duration, caster, rainbow_col);
        type = InnerLightType.THUNDER_AURA;
        color = COLOR;
    }

    public ThunderAuraLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.THUNDER_AURA;
        color = "thunder_aura";
    }

    private void checkSafety(){
        if(this.power_multiplier > BalanceConfig.FOREST_AURA_MAX_POWER){
            power_multiplier = BalanceConfig.FOREST_AURA_MAX_POWER;
        }
        if(this.power_multiplier < BalanceConfig.FOREST_AURA_MIN_POWER){
            power_multiplier = BalanceConfig.FOREST_AURA_MIN_POWER;
        }
        int max_duration = BalanceConfig.FOREST_AURA_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.FOREST_AURA_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(this.duration > max_duration){
            this.duration = max_duration;
        }
        if(this.duration < BalanceConfig.FOREST_AURA_MIN_DURATION){
            this.duration = BalanceConfig.FOREST_AURA_MIN_DURATION;
        }
    }


    @Override
    public void execute(){

        checkSafety();
        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
            if(this.rainbow_col){
                CGLCompat.getLib().setRainbowColor(this.caster);
            }else{
                CGLCompat.getLib().setColor(this.caster, this.color);
            }
        }

        caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()),LightSounds.FOREST_AURA_PUFF,SoundCategory.PLAYERS, 1f, 1f);
        LightComponent component = LIGHT_COMPONENT.get(caster);

        LightParticlesUtil.spawnLightTypeParticle(LightParticles.FOREST_AURA_LIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());

        if(component.getTargets().equals(TargetType.SELF)){
            //The -1 is because status effect levels start from 0
            caster.addStatusEffect(new StatusEffectInstance(LightEffects.FOREST_AURA, this.duration*20, (int) this.power_multiplier-1, false, false));
        }
        else if(component.getTargets().equals(TargetType.ALL)){

        }

    }

}
