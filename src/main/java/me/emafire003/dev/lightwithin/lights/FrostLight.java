package me.emafire003.dev.lightwithin.lights;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.blocks.LightBlocks;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.config.TriggerConfig;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.particles.PrecompiledParticleEffects;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.lightwithin.util.TriggerChecks;
import me.emafire003.dev.structureplacerapi.StructurePlacerAPI;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static me.emafire003.dev.lightwithin.LightWithin.*;
import static me.emafire003.dev.lightwithin.util.CheckUtils.checkBlocksWithTag;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.getMinTrigger;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.sendLightTriggered;

public class FrostLight extends InnerLight {

    public static final TagKey<Block> FROST_TRIGGER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, LightWithin.getIdentifier("frost_trigger_blocks"));
    public static final TagKey<Item> FROST_TRIGGER_ITEMS = TagKey.of(RegistryKeys.ITEM, LightWithin.getIdentifier("frost_trigger_items"));

    public static final Item INGREDIENT = Items.SNOWBALL;
    private final List<TargetType> possibleTargetTypes = Arrays.asList(TargetType.ENEMIES, TargetType.ALLIES, TargetType.ALL, TargetType.SELF);
    private final List<TriggerChecks> triggerChecks = List.of(TriggerChecks.ENTITY_ATTACKED, TriggerChecks.ALLY_DIES, TriggerChecks.ENTITY_FREEZING);
    private final Identifier lightId = LightWithin.getIdentifier("frost");

    /**
     * Creates an instance of this InnerLight
     *
     * @param regex A lambda function, which provides you with a string representing the UUID portion dedicated to type determination.
     *              You have to provide a check based on the string for which the player will have that particular light.
     *              Remember that the order with which the lights are registered matters a lot!
     */
    public FrostLight(TypeCreationRegex regex) {
        super(regex);
        this.color = "frost";
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
        if(power_multiplier > BalanceConfig.FROST_MAX_POWER){
            power_multiplier = BalanceConfig.FROST_MAX_POWER;
        }
        if(power_multiplier < BalanceConfig.FROST_MIN_POWER){
            power_multiplier = BalanceConfig.FROST_MIN_POWER;
        }
        int max_duration = BalanceConfig.FROST_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.FROST_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(duration > max_duration){
            duration = max_duration;
        }
        if(duration < BalanceConfig.FROST_MIN_DURATION){
            duration = BalanceConfig.FROST_MIN_DURATION;
        }
        return new Pair<>(power_multiplier, duration);
    }

    @Override
    public void startActivation(LightComponent component, PlayerEntity player) {
        List<LivingEntity> targets = new ArrayList<>();
        if(component.getTargets().equals(TargetType.ALL)){
            targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(getBoxExpansionAmount()), (entity1 -> true)));
            targets.remove(player);
            player.sendMessage(Text.translatable("light.description.activation.frost.all"), true);
        }

        else if(component.getTargets().equals(TargetType.ENEMIES)){
            targets.addAll(getEnemies(player));
            player.sendMessage(Text.translatable("light.description.activation.frost.enemies"), true);
        }else if(component.getTargets().equals(TargetType.ALLIES)){
            targets.addAll(getAllies(player));
            player.sendMessage(Text.translatable("light.description.activation.frost.allies"), true);
        }if(component.getTargets().equals(TargetType.SELF)){
            targets.add(player);
            player.sendMessage(Text.translatable("light.description.activation.frost.self"), true);
        }
        activate(player, targets, component.getPowerMultiplier(), component.getDuration(), component.getMaxCooldown());
    }

    @Override
    protected void activate(PlayerEntity caster, List<LivingEntity> targets, double power_multiplier, int duration, double cooldown_time) {
        super.activate(caster, targets, power_multiplier, duration, cooldown_time);
        power_multiplier = checkSafety(power_multiplier, duration).getFirst();
        duration = checkSafety(power_multiplier, duration).getSecond();

        LightComponent component = LightWithin.LIGHT_COMPONENT.get(caster);

        caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()), LightSounds.FROST_LIGHT, SoundCategory.PLAYERS, 1f, 1f);

        if(!caster.getWorld().isClient && (CheckUtils.checkGriefable((ServerPlayerEntity) caster) || Config.NON_FUNDAMENTAL_STRUCTURE_GRIEFING) && (component.getTargets().equals(TargetType.ALL) || component.getTargets().equals(TargetType.ENEMIES))) {
            StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), LightWithin.getIdentifier("frost_light"), caster.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1.0f, new BlockPos(-4, -3, -3));
            if(Config.REPLACEABLE_STRUCTURES){
                placer.loadAndRestoreStructureAnimated(caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), 2, true);
            }else{
                placer.loadStructure();
            }

        }

        if(!caster.getWorld().isClient()){
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.FROSTLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());
            PrecompiledParticleEffects.spawnSnowflake((ServerWorld) caster.getWorld(), caster.getPos().add(0, 2, 0));
        }

        for(LivingEntity target : targets){
            //target.playSound(LightSounds.FROST_LIGHT, 1, 1);

            if((component.getTargets().equals(TargetType.SELF) || component.getTargets().equals(TargetType.ALLIES))){
                if(target.equals(caster) && component.getTargets().equals(TargetType.ALLIES)){
                    target.addStatusEffect(new StatusEffectInstance(LightEffects.FREEZE_RESISTANCE, (int) (duration*20/Config.DIV_SELF)));
                }else{
                    target.addStatusEffect(new StatusEffectInstance(LightEffects.FREEZE_RESISTANCE, (int) (duration)*20));
                }

                Direction facing = target.getHorizontalFacing();
                if(!caster.getWorld().isClient && (CheckUtils.checkGriefable((ServerPlayerEntity) caster) || Config.NON_FUNDAMENTAL_STRUCTURE_GRIEFING)){

                    StructurePlacerAPI placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), LightWithin.getIdentifier("frost_wall"), target.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, true, 1.0f, new BlockPos(-2, -1, -2));
                    if(facing.equals(Direction.EAST)){
                        placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), LightWithin.getIdentifier("frost_wall"), target.getBlockPos(), BlockMirror.NONE, BlockRotation.CLOCKWISE_90, true, 1.0f, new BlockPos(2, -1, -2));
                    }else if(facing.equals(Direction.SOUTH)){
                        placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), LightWithin.getIdentifier("frost_wall"), target.getBlockPos(), BlockMirror.NONE, BlockRotation.CLOCKWISE_180, true, 1.0f, new BlockPos(2, -1, 2));
                    }else if(facing.equals(Direction.WEST)){
                        placer = new StructurePlacerAPI((ServerWorld) caster.getWorld(), LightWithin.getIdentifier("frost_wall"), target.getBlockPos(), BlockMirror.NONE, BlockRotation.COUNTERCLOCKWISE_90, true, 1.0f, new BlockPos(-2, -1, 2));
                    }

                    if(Config.REPLACEABLE_STRUCTURES && !Config.KEEP_ESSENTIALS_STRUCTURES){
                        placer.loadAndRestoreStructureAnimated(caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), 2, true);
                    }else{
                        placer.loadStructure();
                    }
                }
            }else{
                if(!caster.getWorld().isClient){
                    LightParticlesUtil.spawnLightTypeParticle(LightParticles.FROSTLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());

                    target.addStatusEffect(new StatusEffectInstance(LightEffects.FROST, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), 0, false, false));

                    target.damage(caster.getWorld().getDamageSources().freeze(), (float) power_multiplier);

                    Box box =  target.getDimensions(target.getPose()).getBoxAt(target.getPos());
                    box.expand(1);
                    Stream<BlockPos> stream_pos = BlockPos.stream(box);
                    stream_pos.forEach( (pos) -> caster.getWorld().setBlockState(pos, LightBlocks.CLEAR_ICE.getDefaultState()));
                }
            }
    }
}

    @Override
    public void triggerCheck(PlayerEntity player, LightComponent component, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        double trigger_sum = 0;
        /**If the player has ALL as target, he needs to be hurt (or an ally has to die, but that depends on the trigger)*/
        if(component.getTargets().equals(TargetType.ALL)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_ALL_VERY_LOW_HEALTH;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_ALL_LOW_HEALTH;
            }

            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.FROST_ALL_SURROUNDED;
            }
            //Checks if the player has low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+TriggerConfig.FROST_ALL_ARMOR_DURABILITY;
            }

            if(player.isFrozen()){
                trigger_sum=trigger_sum+TriggerConfig.FROST_ALL_FREEZING;
            }
            if(checkLightConditions(player)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_ALL_CONDITIONS;
            }

            if(trigger_sum >= getMinTrigger()){
                sendLightTriggered((ServerPlayerEntity) player);
            }

        }
        /**CHECKS if the player has ENEMIES as target, either him or his allies health needs to be low*/
        else if(component.getTargets().equals(TargetType.ENEMIES) && (CheckUtils.CheckAllies.checkAlly(player, target) || player.equals(target))){
            if(CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_ENEMIES_ALLY_LOW_HEALTH;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+5)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_ENEMIES_VERY_LOW_HEALTH;
            }
            if(player.isFrozen()){
                trigger_sum=trigger_sum+TriggerConfig.FROST_ENEMIES_FREEZING;
            }
            //Checks if the player'sallies have low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+TriggerConfig.FROST_ENEMIES_ALLY_ARMOR_DURABILITY;
            }
            //Checks if the player has low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+TriggerConfig.FROST_ENEMIES_ARMOR_DURABILITY;
            }

            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.FROST_ENEMIES_SURROUNDED;
            }

            if(checkLightConditions(player)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_ENEMIES_CONDITIONS;
            }

            if(trigger_sum >= getMinTrigger()) {
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }else if(component.getTargets().equals(TargetType.SELF)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_SELF_VERY_LOW_HEALTH;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_SELF_LOW_HEALTH;
            }

            //Checks if the player has low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+TriggerConfig.FROST_SELF_ARMOR_DURABILITY;
            }
            if(player.isFrozen()){
                trigger_sum=trigger_sum+TriggerConfig.FROST_SELF_FREEZING;
            }
            if(checkLightConditions(player)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_SELF_CONDITIONS;
            }

            if(trigger_sum >= getMinTrigger()) {
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }else if(component.getTargets().equals(TargetType.ALLIES)){
            if(!player.equals(target) && CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_ALLIES_ALLY_LOW_HEALTH;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_ALLIES_VERY_LOW_HEALTH;
            }
            if(player.isFrozen()){
                trigger_sum=trigger_sum+TriggerConfig.FROST_ALLIES_FREEZING;
            }
            if(target.isFrozen()){
                trigger_sum=trigger_sum+TriggerConfig.FROST_ALLIES_ALLY_FREEZING;
            }
            //Checks if the player'sallies have low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+TriggerConfig.FROST_ALLIES_ALLY_ARMOR_DURABILITY;
            }

            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.FROST_ALLIES_SURROUNDED;
            }

            if(checkLightConditions(player)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_ALLIES_CONDITIONS;
            }

            if(trigger_sum >= getMinTrigger()) {
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }
    }

    @Override
    public boolean checkLightConditions(PlayerEntity player) {
        if(player.isFrozen()){
            return true;
        }

        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        if(main.isIn(FrostLight.FROST_TRIGGER_ITEMS) || off.isIn(FrostLight.FROST_TRIGGER_ITEMS)){
            return true;
        }
        return checkBlocksWithTag(player, Config.TRIGGER_BLOCK_RADIUS, FrostLight.FROST_TRIGGER_BLOCKS);
    }
}
