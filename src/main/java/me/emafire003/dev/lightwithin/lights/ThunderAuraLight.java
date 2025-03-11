package me.emafire003.dev.lightwithin.lights;

import me.emafire003.dev.lightwithin.LightWithin;
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
import me.emafire003.dev.particleanimationlib.effects.LineEffect;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
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

While active or under the allies thunder aura effect, immunity to lightning damage.

Allies:
    - Forcefield around ally which bounces off enemies and enemies' projectiles (maybe, otherwise just enemies)
    - Immunity to lightning damage
    - Entites that get in contact with the filed get zapped for tot damage based on power level
AlL:
    - Either smite at a distance, like point and summon lighting up to power level times yes
Variant:
    - Summons thunderstorm? With many lightnings, like times the power level per second

TRIGGERS when (if conditions are met): Struck by lightning, Ally death, Attacking entity, Ally being attacked

* */
public class ThunderAuraLight extends InnerLight {

    public static final Item INGREDIENT = Items.COPPER_INGOT; //Glowstone? Iron? Lightning rod?

    public static final String COLOR = "AFCE23";

    /// Used in the ALL target type, is the amount of lightnings that a player can still spawn in
    public static HashMap<UUID, Integer> LIGHTNING_USES_LEFT = new HashMap<>();

    public static final TagKey<Item> THUNDER_AURA_TRIGGER_ITEMS = TagKey.of(RegistryKeys.ITEM, LightWithin.getIdentifier("thunder_aura_trigger_items"));


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
        if(this.power_multiplier > BalanceConfig.THUNDER_AURA_MAX_POWER){
            power_multiplier = BalanceConfig.THUNDER_AURA_MAX_POWER;
        }
        if(this.power_multiplier < BalanceConfig.THUNDER_AURA_MIN_POWER){
            power_multiplier = BalanceConfig.THUNDER_AURA_MIN_POWER;
        }
        int max_duration = BalanceConfig.THUNDER_AURA_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.THUNDER_AURA_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(this.duration > max_duration){
            this.duration = max_duration;
        }
        if(this.duration < BalanceConfig.THUNDER_AURA_MIN_DURATION){
            this.duration = BalanceConfig.THUNDER_AURA_MIN_DURATION;
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

        LightComponent component = LIGHT_COMPONENT.get(caster);

        if(!caster.getWorld().isClient()){
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.THUNDER_AURA_LIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
            caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()), LightSounds.THUNDER_AURA_LIGHT, SoundCategory.PLAYERS, 1f, 1f);
        }

        //Allies shield thing
        if(component.getTargets().equals(TargetType.ALLIES)){
            targets.forEach(target -> {
                if(!caster.getWorld().isClient && !caster.equals(target)){
                    Vec3d origin = caster.getPos().add(0, caster.getDimensions(caster.getPose()).height()/2, 0);
                    Vec3d finish = target.getPos().add(0, target.getDimensions(target.getPose()).height()/2, 0);
                    //TODO this would be nice as a "completable effect" like it spawns the particles in sequence etc
                    LineEffect line = LineEffect.builder((ServerWorld) caster.getWorld(), LightParticles.LIGHTNING_PARTICLE, origin)
                            .particle(ParticleTypes.ELECTRIC_SPARK)         .targetPos(finish).particles((int) origin.distanceTo(finish)*2).particleLimit(100).limitParticlesEveryNIterations(1).build();

                    caster.sendMessage(Text.literal("Spawinging the line, with particles number: " + (int) origin.distanceTo(finish)*2));
                    line.runFor(0.5);
                }
                target.addStatusEffect(new StatusEffectInstance(LightEffects.THUNDER_AURA, this.duration*20, (int) this.power_multiplier -1, false, true));
            });
        }//Extra thundery weather (superstorm). The weather change is global, but the extra lightnings are in a localized area
        else if(component.getTargets().equals(TargetType.VARIANT)){
            caster.addStatusEffect(new StatusEffectInstance(LightEffects.STORM_AURA, this.duration*20, (int) this.power_multiplier, false, false));
        }
        /// "ALL" is defined inside {@link LightTriggeringAndEvents#registerThunderAuraAllEffect()}
        /// Anyways, it will allow the player to spawn a lightning at the point they are looking at.
        /// A max number of power multiplier of lightnings

    }

    /**Spawns a lot of lightnings inside the specified box area for the specified duration
     * at random positions
     * <p>
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
