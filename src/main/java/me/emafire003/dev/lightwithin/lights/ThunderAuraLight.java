package me.emafire003.dev.lightwithin.lights;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.config.TriggerConfig;
import me.emafire003.dev.lightwithin.events.LightTriggeringAndEvents;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.lightwithin.util.TriggerChecks;
import me.emafire003.dev.particleanimationlib.effects.LineEffect;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static me.emafire003.dev.lightwithin.LightWithin.*;
import static me.emafire003.dev.lightwithin.util.CheckUtils.checkRecentlyStruckByLightning;
import static me.emafire003.dev.lightwithin.util.CheckUtils.checkThundering;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.getMinTrigger;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.sendLightTriggered;

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
    private final List<TargetType> possibleTargetTypes = Arrays.asList(TargetType.ALLIES, TargetType.ALLIES, TargetType.ALL, TargetType.ALL, TargetType.VARIANT);
    private final List<TriggerChecks> triggerChecks = List.of(TriggerChecks.ENTITY_ATTACKS_ENTITY, TriggerChecks.ALLY_ATTACKED, TriggerChecks.ALLY_DIES, TriggerChecks.ENTITY_STRUCK_BY_LIGHTNING);
    private final Identifier lightId = LightWithin.getIdentifier("thunder_aura");

    //TODO verify that this is ok not to be used
    public static final String COLOR = "AFCE23";

    /// Used in the ALL target type, is the amount of lightnings that a player can still spawn in
    public static HashMap<UUID, Integer> LIGHTNING_USES_LEFT = new HashMap<>();

    public static final TagKey<Item> THUNDER_AURA_TRIGGER_ITEMS = TagKey.of(RegistryKeys.ITEM, LightWithin.getIdentifier("thunder_aura_trigger_items"));


    /**
     * Creates an instance of this InnerLight. Remember to register it!
     *
     * @param regex A lambda function, which provides you with a string representing the UUID portion dedicated to type determination.
     *              You have to provide a check based on the string for which the player will have that particular light.
     *              Remember that the order with which the lights are registered matters a lot!
     */
    public ThunderAuraLight(TypeCreationRegex regex) {
        super(regex);
        this.color = "thunder_aura";
    }

    @Override
    public List<TargetType> getPossibleTargetTypes() {
        return possibleTargetTypes;
    }

    @Override
    public List<TriggerChecks> getTriggerChecks() {
        return triggerChecks;
    }

    @Override
    public Item getIngredient() {
        return INGREDIENT;
    }

    @Override
    public Identifier getLightId() {
        return lightId;
    }

    @Override
    protected Pair<Double, Integer> checkSafety(double power_multiplier, int duration) {
        if(power_multiplier > BalanceConfig.THUNDER_AURA_MAX_POWER){
            power_multiplier = BalanceConfig.THUNDER_AURA_MAX_POWER;
        }
        if(power_multiplier < BalanceConfig.THUNDER_AURA_MIN_POWER){
            power_multiplier = BalanceConfig.THUNDER_AURA_MIN_POWER;
        }
        int max_duration = BalanceConfig.THUNDER_AURA_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.THUNDER_AURA_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(duration > max_duration){
            duration = max_duration;
        }
        if(duration < BalanceConfig.THUNDER_AURA_MIN_DURATION){
            duration = BalanceConfig.THUNDER_AURA_MIN_DURATION;
        }
        return new Pair<>(power_multiplier, duration);
    }

    @Override
    public void startActivation(LightComponent component, PlayerEntity player) {
        List<LivingEntity> targets = new ArrayList<>();

        if(component.getTargets().equals(TargetType.ALLIES)){
            targets.addAll(getAllies(player));
            player.sendMessage(Text.translatable("light.description.activation.thunder_aura.allies"), true);
        }else if(component.getTargets().equals(TargetType.ALL)){
            player.sendMessage(Text.translatable("light.description.activation.thunder_aura.all"), true);
        }else if(component.getTargets().equals(TargetType.VARIANT)){
            targets.add(player);
            player.sendMessage(Text.translatable("light.description.activation.thunder_aura.variant"), true);
        }
        activate(player, targets, component.getPowerMultiplier(), component.getDuration(), component.getMaxCooldown());
    }

    @Override
    protected void activate(PlayerEntity caster, List<LivingEntity> targets, double power_multiplier, int duration, double cooldown_time) {
        super.activate(caster, targets, power_multiplier, duration, cooldown_time);
        power_multiplier = checkSafety(power_multiplier, duration).getFirst();
        duration = checkSafety(power_multiplier, duration).getSecond();

        LightComponent component = LIGHT_COMPONENT.get(caster);

        if(!caster.getWorld().isClient()){
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.TYPES_PARTICLES.get(lightId), (ServerWorld) caster.getWorld(), caster.getPos());
            caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()), LightSounds.TYPES_SOUNDS.get(lightId), SoundCategory.PLAYERS, 1f, 1f);
        }

        //Allies shield thing
        if(component.getTargets().equals(TargetType.ALLIES)){
            targets.remove(caster);
            caster.addStatusEffect(new StatusEffectInstance(LightEffects.THUNDER_AURA, duration*20, (int) ((power_multiplier -1)/Config.DIV_SELF), false, true));
            int finalDuration = duration;
            double finalPower_multiplier = power_multiplier;
            targets.forEach(target -> {
                if(!caster.getWorld().isClient && !caster.equals(target)){
                    Vec3d origin = caster.getPos().add(0, caster.getDimensions(caster.getPose()).height()/2, 0);
                    Vec3d finish = target.getPos().add(0, target.getDimensions(target.getPose()).height()/2, 0);
                    //TODO this would be nice as a "completable effect" like it spawns the particles in sequence etc
                    LineEffect line = LineEffect.builder((ServerWorld) caster.getWorld(), LightParticles.LIGHTNING_PARTICLE, origin)
                            .particle(ParticleTypes.ELECTRIC_SPARK)         .targetPos(finish).particles((int) origin.distanceTo(finish)*2).particleLimit(100).limitParticlesEveryNIterations(1).build();

                    line.runFor(0.5);
                }
                target.addStatusEffect(new StatusEffectInstance(LightEffects.THUNDER_AURA, finalDuration *20, (int) finalPower_multiplier -1, false, true));
            });
        }//Extra thundery weather (superstorm). The weather change is global, but the extra lightnings are in a localized area
        else if(component.getTargets().equals(TargetType.VARIANT)){
            caster.addStatusEffect(new StatusEffectInstance(LightEffects.STORM_AURA, duration*20, (int) power_multiplier, false, false));
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

    @Override
    public void triggerCheck(PlayerEntity player, LightComponent component, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        double trigger_sum = 0;

        if(component.getTargets().equals(TargetType.ALL)){
            //Triggers with: Low health, Very low health, Self Armor broken,
            // thunder aura, raining +1

            //Checks if the player is very low health
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+ TriggerConfig.THUNDER_AURA_ALL_VERY_LOW_HEALTH; //+3
                //Checks if the player has low health
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum=trigger_sum+TriggerConfig.THUNDER_AURA_ALL_LOW_HEALTH; //+2
            }
            //Checks if the player has low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+TriggerConfig.THUNDER_AURA_ALL_ARMOR_DURABILITY;//+1
            }
            if(CheckUtils.checkRaining(player.getWorld())){
                trigger_sum = trigger_sum+TriggerConfig.THUNDER_AURA_ALL_RAINING; //+1
            }
            //Checks if the player has the optimal criteria for activation
            if(checkLightConditions(player)){
                trigger_sum=trigger_sum+TriggerConfig.THUNDER_AURA_ALL_CONDITIONS; //+3
            }
            if(trigger_sum >= getMinTrigger()){
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }else if(component.getTargets().equals(TargetType.ALLIES)){
            //Ally low health, ally very low health, self very low health,
            //ally struck by lightning, surrounded by allies, conditions

            if(!player.equals(target) && CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES+5)){
                trigger_sum = trigger_sum + TriggerConfig.THUNDER_AURA_ALLIES_ALLY_LOW_HEALTH; //+2
            }
            if(!player.equals(target) && CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES-5)){
                trigger_sum = trigger_sum + TriggerConfig.THUNDER_AURA_ALLIES_VERY_LOW_HEALTH;//+3
            }

            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+TriggerConfig.THUNDER_AURA_ALLIES_VERY_LOW_HEALTH; //+2
            }

            if(CheckUtils.checkRaining(player.getWorld())){
                trigger_sum = trigger_sum+TriggerConfig.THUNDER_AURA_ALLIES_RAINING; //+1
            }
            if(!player.equals(target) && target instanceof LivingEntity && CheckUtils.CheckAllies.checkAlly(player, target) && checkRecentlyStruckByLightning(target)){
                trigger_sum=trigger_sum+TriggerConfig.THUNDER_AURA_ALLIES_STRUCK_BY_LIGHTNING; //+1
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED_BY_ALLIES && CheckUtils.checkSurroundedByAllies(player)){
                trigger_sum=trigger_sum+TriggerConfig.THUNDER_AURA_ALLIES_SURROUNDED_BY_ALLIES;//+1
            }

            if(checkLightConditions(player)){
                trigger_sum=trigger_sum+TriggerConfig.THUNDER_AURA_ALLIES_CONDITIONS;
            }

            if(trigger_sum >= getMinTrigger()) {
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }
        /**CHECKS if the player has ENEMIES as target, either his or his allies health needs to be low*/
        else if(component.getTargets().equals(TargetType.VARIANT)){

            //Self low health, very low health, rainy weather (+2), surrounded, conditions
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+TriggerConfig.THUNDER_AURA_VARIANT_VERY_LOW_HEALTH; //+3
                //Checks if the player has low health
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum=trigger_sum+TriggerConfig.THUNDER_AURA_VARIANT_LOW_HEALTH; //+2
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.THUNDER_AURA_VARIANT_SURROUNDED;//+1
            }
            if(CheckUtils.checkRaining(player.getWorld())){
                trigger_sum = trigger_sum+TriggerConfig.THUNDER_AURA_VARIANT_RAINING; //+2
            }
            //Checks if the player has the optimal criteria for activation
            if(checkLightConditions(player)){
                trigger_sum=trigger_sum+TriggerConfig.THUNDER_AURA_VARIANT_CONDITIONS; //+3
            }
            if(trigger_sum >= getMinTrigger()){
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }
    }

    /**Used to check if the player has something that can be considered a ThunderAura source
     *
     * @param player The player to perform checks on*/
    //TODO somehow make these datadriven/customizable at some point?
    @Override
    public boolean checkLightConditions(PlayerEntity player) {
        if(checkThundering(player.getWorld())){
            return true;
        }
        //If the player is standing on a is copper rod then lightning conditions met
        if(player.getWorld().getBlockState(player.getBlockPos().down()).isOf(Blocks.LIGHTNING_ROD)
                || player.getWorld().getBlockState(player.getBlockPos()).isOf(Blocks.LIGHTNING_ROD)){
            return true;
        }
        if(checkRecentlyStruckByLightning(player)){
            return true;
        }

        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        return main.isIn(ThunderAuraLight.THUNDER_AURA_TRIGGER_ITEMS) || off.isIn(ThunderAuraLight.THUNDER_AURA_TRIGGER_ITEMS);

    }

    @Override
    public String toString() {
        return this.lightId.getPath();
    }
}
