package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.events.LightTriggeringAndEvents;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
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

    public static final Item INGREDIENT = Items.LIGHTNING_ROD; //Glowstone? Iron?

    public static final String COLOR = "AFCE23";

    public static HashMap<UUID, Integer> LIGHTNING_USES_LEFT = new HashMap<>();

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

    //TODO fix these up
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

        //Allies shield thing
        if(component.getTargets().equals(TargetType.ALLIES)){
            //The -1 is because status effect levels start from 0, so it's 0 to 9 but the players sees I, II, III, IV ecc
            targets.forEach(target -> {
                target.addStatusEffect(new StatusEffectInstance(LightEffects.THUNDER_AURA, this.duration*20, (int) this.power_multiplier -1, false, true));
            });
            //TODO playsound, maybe a static for the people with the barrier
        }else if(component.getTargets().equals(TargetType.ALL)){
            /** Defined inside {@link LightTriggeringAndEvents#registerThunderAuraAllEffect()}
             * Anywasy, it will allow the player to spawn a lightning at the point they are looking at.
             * A max number of power multiplier of lightnings*/
            //TODO playsound
        }
        //Extra thundery weather (superstorm). The weather change is global, but the extra lightnings are in a localized area
        else if(component.getTargets().equals(TargetType.VARIANT)){
            //Must be on the server
            //TODO playsound
            caster.addStatusEffect(new StatusEffectInstance(LightEffects.STORM_AURA, this.duration*20, (int) this.power_multiplier, false, false));
        }

    }

    /**Spawns a lot of lightnings inside the specified box area for the specified duration
     * at random positions
     *
     * The duration is in seconds*/
    //Def values 20, 10, 3
    public static void spawnStormLightnings(Box area, int seconds, int lightnings_per_second, LivingEntity caster){
        //TODO move this into the effect?
        AtomicInteger tickCounter = new AtomicInteger();
        int totalTicks = seconds*20;
        int interval_between_lightnings = 20/(Math.max(1, lightnings_per_second));

        //It's used to wait for the storm to load in, takes a second
        AtomicBoolean waitingPhase = new AtomicBoolean(true);
        ServerTickEvents.END_SERVER_TICK.register(server -> {

            if(waitingPhase.get()){
                //Waits for one second
                if(tickCounter.get() < 25){
                    tickCounter.getAndIncrement();
                    return;
                }else{
                    //Then stops the wait phase and turns the counter back to 0
                    waitingPhase.set(false);
                    tickCounter.set(0);
                }
            }

            if(tickCounter.get() > totalTicks || tickCounter.get() == -1){
                return;
            }
            //This checks if it's time sto spawn a lightning or not. It is when the current tick is compatible with th interval between lightnings
            if(tickCounter.get()%interval_between_lightnings == 0){
                LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, caster.getWorld());

                Vec3d pos = new Vec3d(ThreadLocalRandom.current().nextDouble(area.minX, area.maxX), ThreadLocalRandom.current().nextDouble(area.minY, area.maxY), ThreadLocalRandom.current().nextDouble(area.minZ, area.maxZ));
                //Ensures it's in the air and not inside other blocks
                while(!caster.getWorld().getBlockState(BlockPos.ofFloored(pos.x, pos.y, pos.z)).isAir()){
                    pos = new Vec3d(ThreadLocalRandom.current().nextDouble(area.minX, area.maxX), ThreadLocalRandom.current().nextDouble(area.minY, area.maxY), ThreadLocalRandom.current().nextDouble(area.minZ, area.maxZ));
                }

                //Checks weather or not to force it on the ground, TODO WIKI with a chance of 40%
                if(caster.getRandom().nextBetween(1,10) <= 4){
                    //As long as it's air it will go down half a block, trying to find the terrain
                    while(caster.getWorld().getBlockState(BlockPos.ofFloored(pos.x, pos.y, pos.z)).isAir() && area.contains(pos)){
                        pos = pos.add(0, -0.5, 0);
                    }
                    //I add another +0.5 since if the terrain is found, the position is set below it. Same thing the position silps outside the box
                    pos = pos.add(0, 0.5, 0);
                }

                lightning.setPosition(pos);
                ( (ServerWorld )caster.getWorld()).spawnParticles(LightParticles.LIGHTNING_PARTICLE, pos.x, pos.y+0.25, pos.z, 25, 0.3, 0.3, 0.3, 0.7);
                if(caster instanceof ServerPlayerEntity){
                    lightning.setChanneler((ServerPlayerEntity) caster);
                }
                caster.getWorld().spawnEntity(lightning);
            }

            tickCounter.getAndIncrement();
        });
    }


}
