package me.emafire003.dev.lightwithin.lights;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.compat.coloredglowlib.CGLCompat;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.config.TriggerConfig;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.lightwithin.util.TriggerChecks;
import me.emafire003.dev.structureplacerapi.StructurePlacerAPI;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.*;
import static me.emafire003.dev.lightwithin.util.CheckUtils.checkBlocksWithTag;
import static me.emafire003.dev.lightwithin.util.CheckUtils.getEnemies;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.getMinTrigger;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.sendLightTriggered;

public class BlazingLight extends InnerLight {

    public static final Item INGREDIENT = Items.FIRE_CHARGE;
    private final List<TargetType> possibleTargetTypes = Arrays.asList(TargetType.ENEMIES, TargetType.ALL, TargetType.VARIANT);
    private final List<TriggerChecks> triggerChecks = List.of(TriggerChecks.ENTITY_ATTACKS_ENTITY, TriggerChecks.ALLY_ATTACKED, TriggerChecks.ENTITY_BURNING);
    private final Identifier lightId = LightWithin.getIdentifier("blazing");

    private double crit_multiplier = 1.5;
    private double r = 0.5;

    public static final TagKey<Block> BLAZING_TRIGGER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, LightWithin.getIdentifier("blazing_trigger_blocks"));
    public static final TagKey<Item> BLAZING_TRIGGER_ITEMS = TagKey.of(RegistryKeys.ITEM, LightWithin.getIdentifier("blazing_trigger_items"));

    /**
     * Creates an instance of this InnerLight.
     *
     * @param regex A lambda function, which provides you with a string representing the UUID portion dedicated to type determination.
     *              You have to provide a check based on the string for which the player will have that particular light.
     *              Remember that the order with which the lights are registered matters a lot!
     */
    public BlazingLight(TypeCreationRegex regex) {
        super(regex);
        color = "blazing";
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

        if(power_multiplier > BalanceConfig.BLAZING_MAX_POWER){
            power_multiplier = BalanceConfig.BLAZING_MAX_POWER;
        }
        if(power_multiplier < BalanceConfig.BLAZING_MIN_POWER){
            power_multiplier = BalanceConfig.BLAZING_MIN_POWER;
        }
        int max_duration = BalanceConfig.BLAZING_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.BLAZING_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(duration > max_duration){
            duration = max_duration;
        }
        if(duration < BalanceConfig.BLAZING_MIN_DURATION){
            duration = BalanceConfig.BLAZING_MIN_DURATION;
        }
        if(BalanceConfig.BLAZING_CRIT_MULTIPLIER > 1){
            crit_multiplier = BalanceConfig.BLAZING_CRIT_MULTIPLIER;
        }
        return new Pair<>(power_multiplier, duration);

    }

    @Override
    public void startActivation(LightComponent component, PlayerEntity player) {
        List<LivingEntity> targets = new ArrayList<>();

        if(component.getTargets().equals(TargetType.ALL)){
            targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(getBoxExpansionAmount()), (entity1 -> true)));
            targets.remove(player);
            player.sendMessage(Text.translatable("light.description.activation.blazing.all"), true);
        }

        else if(component.getTargets().equals(TargetType.ENEMIES) || component.getTargets().equals(TargetType.VARIANT)){
            targets.addAll(getEnemies(player));
            player.sendMessage(Text.translatable("light.description.activation.blazing.enemies"), true);
        }
        activate(player, targets, component.getPowerMultiplier(), component.getDuration(), component.getMaxCooldown());
    }

    @Override
    protected void activate(PlayerEntity caster, List<LivingEntity> targets, double power_multiplier, int duration, double cooldown_time) {
        power_multiplier = checkSafety(power_multiplier, duration).getFirst();
        duration = checkSafety(power_multiplier, duration).getSecond();

        LightComponent component = LIGHT_COMPONENT.get(caster);

        String blazing_structure_id = "blazing_light";
        String fire_ring_id = "fire_ring";
        ParticleEffect flame_particle = ParticleTypes.FLAME;
        if(component.getTargets().equals(TargetType.VARIANT)){
            flame_particle = ParticleTypes.SOUL_FIRE_FLAME;
            fire_ring_id = "soulfire_ring";
            blazing_structure_id = "blazing_light_soul";

            color = "blazing_variant";
        }

        if(FabricLoader.getInstance().isModLoaded("coloredglowlib")){
           CGLCompat.getLib().setColor(caster, color);
        }

        caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()), LightSounds.BLAZING_LIGHT, SoundCategory.PLAYERS, 1f, 1f);
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, caster.getStatusEffect(LightEffects. LIGHT_ACTIVE).getDuration(), 0, false, false));


        if(component.getTargets().equals(TargetType.ALL)){
            power_multiplier = power_multiplier + BalanceConfig.BLAZING_ALL_DAMAGE_BONUS;
        }
        if(!caster.getWorld().isClient && (CheckUtils.checkGriefable((ServerPlayerEntity) caster) || Config.NON_FUNDAMENTAL_STRUCTURE_GRIEFING)) {
            StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), LightWithin.getIdentifier(blazing_structure_id), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1.0f, new BlockPos(-3, -4, -3));
            if(Config.REPLACEABLE_STRUCTURES){
                placer.loadAndRestoreStructureAnimated(caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), 2, true);
            }else{
                placer.loadStructure();
            }
        }

        if(!caster.getWorld().isClient) {
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.BLAZINGLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
        }
        for(LivingEntity target : targets){
            //target.playSound(LightSounds.BLAZING_LIGHT, 1, 1);

            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.BLAZINGLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
            }

            //TODO make the chance configable EDIT: Maybe not
            //it's a crit, unique for now to the blazing light Currently 10 percent
            if(caster.getRandom().nextInt(10) == 1){
                target.damage(caster.getWorld().getDamageSources().inFire(), (float) (BalanceConfig.BLAZING_DEFAULT_DAMAGE*power_multiplier*crit_multiplier));
                target.setOnFireFor(duration*BalanceConfig.BLAZING_CRIT_FIRE_MULTIPLIER);
                caster.getWorld().playSound(null, BlockPos.ofFloored(target.getPos()), LightSounds.LIGHT_CRIT, SoundCategory.PLAYERS, 1, 1f);
                LightParticlesUtil.spawnDescendingColumn((ServerPlayerEntity) caster, flame_particle, target.getPos().add(0,3,0));
                if(!caster.getWorld().isClient){
                    StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), LightWithin.getIdentifier(fire_ring_id), caster.getBlockPos());
                    placer.loadStructure();
                }
            }else{
                target.setOnFireFor(duration);
                target.damage(caster.getWorld().getDamageSources().inFire(), (float) (BalanceConfig.BLAZING_DEFAULT_DAMAGE*power_multiplier));
            }
        }

        //to spawn the expanding circle of particles
        ParticleEffect finalFlame_particle = flame_particle;
        ServerTickEvents.END_SERVER_TICK.register((server -> {
            if(r < getBoxExpansionAmount()){
                r = r + 0.5;
                LightParticlesUtil.spawnCircle(caster.getPos().add(0,0.7,0), r, 100, finalFlame_particle, (ServerWorld) caster.getWorld());
            }
        }));
    }

    @Override
    public void triggerCheck(PlayerEntity player, LightComponent component, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        double trigger_sum = 0;
        /**If the player has ALL as target, he needs to be hurt (or an ally has to die, but that depends on the trigger event)*/
        if(component.getTargets().equals(TargetType.ALL)){

            //Checks if the player is very low health
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+ TriggerConfig.BLAZING_ALL_VERY_LOW_HEALTH;
                //Checks if the player has low health
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ALL_LOW_HEALTH;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ALL_SURROUNDED;
            }
            if(player.isOnFire()){
                trigger_sum = trigger_sum+TriggerConfig.BLAZING_ALL_ONFIRE;
            }
            //Checks if the player has low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ALL_ARMOR_DURABILITY;
            }
            //Checks if the player has the optimal criteria for activation
            if(checkLightConditions(player)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ALL_CONDITIONS;
            }
            if(trigger_sum >= getMinTrigger()){
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }
        /**CHECKS if the player has ENEMIES as target, either his or his allies health needs to be low*/
        else if(component.getTargets().equals(TargetType.ENEMIES) || component.getTargets().equals(TargetType.VARIANT)){

            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT/2)){
                trigger_sum = trigger_sum+TriggerConfig.BLAZING_ENEMIES_VERY_LOW_HEALTH;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ENEMIES_SURROUNDED;
            }
            if(player.isOnFire()){
                trigger_sum = trigger_sum+TriggerConfig.BLAZING_ENEMIES_ONFIRE;
            }
            //Checks if the player'sallies have low armor durability
            if(!player.equals(target) && Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ENEMIES_ALLY_ARMOR_DURABILITY;
            }
            //Checks if the player has low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ENEMIES_ARMOR_DURABILITY;
            }
            //Checks if the player has the optimal criteria for activation
            if(checkLightConditions(player)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ENEMIES_CONDITIONS;
            }
            if(trigger_sum >= getMinTrigger()){
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }
    }

    @Override
    public boolean checkLightConditions(PlayerEntity player) {
        if(player.isOnFire()){
            return true;
        }

        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        if(main.isIn(BlazingLight.BLAZING_TRIGGER_ITEMS) || off.isIn(BlazingLight.BLAZING_TRIGGER_ITEMS)){
            return true;
        }
        return checkBlocksWithTag(player, Config.TRIGGER_BLOCK_RADIUS, BlazingLight.BLAZING_TRIGGER_BLOCKS);
    }
}
