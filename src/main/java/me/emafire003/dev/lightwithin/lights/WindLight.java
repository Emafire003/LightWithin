package me.emafire003.dev.lightwithin.lights;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.config.TriggerConfig;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.TriggerChecks;
import me.emafire003.dev.lightwithin.util.fabridash.FabriDash;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.*;
import static me.emafire003.dev.lightwithin.util.CheckUtils.checkMultipleBlocksWithTags;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.getMinTrigger;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.sendLightTriggered;

public class WindLight extends InnerLight {

    public static final TagKey<Block> WIND_TRIGGER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, LightWithin.getIdentifier("wind_trigger_blocks"));
    public static final Item INGREDIENT = Items.WIND_CHARGE;

    private final List<TargetType> possibleTargetTypes = Arrays.asList(TargetType.SELF, TargetType.ALL, TargetType.ALLIES);
    private final List<TriggerChecks> triggerChecks = List.of(TriggerChecks.ENTITY_ATTACKED, TriggerChecks.ENTITY_FALLING);
    private final Identifier lightId = LightWithin.getIdentifier("wind");

    /**
     * Creates an instance of this InnerLight. Remember to register it!
     *
     * @param regex A lambda function, which provides you with a string representing the UUID portion dedicated to type determination.
     *              You have to provide a check based on the string for which the player will have that particular light.
     *              Remember that the order with which the lights are registered matters a lot!
     */
    public WindLight(TypeCreationRegex regex) {
        super(regex);
        this.color = "wind";
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
        if(power_multiplier > BalanceConfig.WIND_MAX_POWER){
            power_multiplier = BalanceConfig.WIND_MAX_POWER;
        }
        if(power_multiplier < BalanceConfig.WIND_MIN_POWER){
            power_multiplier = BalanceConfig.WIND_MIN_POWER;
        }
        int max_duration = BalanceConfig.WIND_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.WIND_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(duration > max_duration){
            duration = max_duration;
        }
        if(duration < BalanceConfig.WIND_MIN_DURATION){
            duration = BalanceConfig.WIND_MIN_DURATION;
        }
        return new Pair<>(power_multiplier, duration);
    }

    @Override
    public void startActivation(LightComponent component, PlayerEntity player) {
        List<LivingEntity> targets = new ArrayList<>();
        if(component.getTargets().equals(TargetType.ALLIES)){
            targets.addAll(getAllies(player));
            player.sendMessage(Text.translatable("light.description.activation.wind.allies"), true);
        }else if(component.getTargets().equals(TargetType.SELF)){
            targets.add(player);
            player.sendMessage(Text.translatable("light.description.activation.wind.self"), true);
        }else if(component.getTargets().equals(TargetType.ALL)){
            targets.addAll(player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(getBoxExpansionAmount()), (entity1 -> true)));
            targets.remove(player);
            player.sendMessage(Text.translatable("light.description.activation.wind.all"), true);
        }
        activate(player, targets, component.getPowerMultiplier(), component.getDuration(), component.getMaxCooldown());
    }

    @Override
    protected void activate(PlayerEntity caster, List<LivingEntity> targets, double power_multiplier, int duration, double cooldown_time) {
        super.activate(caster, targets, power_multiplier, duration, cooldown_time);
        LightComponent component = LightWithin.LIGHT_COMPONENT.get(caster);

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
            for(LivingEntity target : targets){
                FabriDash.dash(target, (float) power_multiplier, true);
                world.spawnParticles(((ServerPlayerEntity )caster), ParticleTypes.CLOUD, false, caster.getX(), caster.getY()+1, caster.getZ(), 65, 0, 0.2, 0, 0.35);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration*20, (int) (power_multiplier/2), false, true));
            }
        }
        //If the target is allies, a series of boost will be given to allies and self
        else if(component.getTargets().equals(TargetType.ALLIES)){
            //oldtarget and stuff prevent generating multiple structures in the same area
            for(LivingEntity target : targets){

                //these are allies, should i still play it? no
                //target.playSound(LightSounds.WIND_LIGHT, 0.9f, 1);
                if(target.equals(caster)){
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration*20, (int) ((power_multiplier/2)/Config.DIV_SELF), false, false));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, duration*20, (int) ((power_multiplier/2)/Config.DIV_SELF), false, false));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, duration*20, 0, false, false));
                    target.addStatusEffect(new StatusEffectInstance(LightEffects.WIND_WALKING, duration*20, 0, false, true));
                }else{
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration*20, (int) (power_multiplier/2), false, false));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, duration*20, (int) (power_multiplier/2), false, false));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, duration*20, 0, false, false));
                    target.addStatusEffect(new StatusEffectInstance(LightEffects.WIND_WALKING, duration*20, 0, false, true));
                }

                LightParticlesUtil.spawnLightTypeParticle(LightParticles.WINDLIGHT_PARTICLE, (ServerWorld) target.getWorld(), target.getPos());
            }
        }//If the target is self, the player will perform a dash (will be launched forward)
        else if(component.getTargets().equals(TargetType.SELF)) {
            LightParticlesUtil.spawnLightTypeParticle(LightParticles.WINDLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), caster.getPos());

            world.spawnParticles(((ServerPlayerEntity )caster), ParticleTypes.CLOUD, false, caster.getX(), caster.getY()+1, caster.getZ(), 200, 0.1, 0.2, 0.1, 0.35);

            FabriDash.dash(caster, (float) power_multiplier, false);
            caster.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, duration*20, 0, false, false));
            caster.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration*20, (int) (power_multiplier/1.5), false, false));
            caster.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, duration*20, (int) (power_multiplier/1.5), false, false));

            //caster.playSound(LightSounds.WIND_LIGHT, 1, 1);
        }
    }

    @Override
    public void triggerCheck(PlayerEntity player, LightComponent component, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        double trigger_sum = 0;
        //Yes it ignores protection but whatever. It's a feature not a bug. I can always add it later
        ItemStack boots = player.getInventory().getArmorStack(3);
        int fall_trigger = Config.FALL_TRIGGER;
        int fe_fa_level = EnchantmentHelper.getLevel(Enchantments.FEATHER_FALLING, boots);

        for(int i = 0; i < fe_fa_level; i++){
            fall_trigger = fall_trigger+10;
        }
        //Moved the targetType variant to ALL so I need to make a compatibility from the old one to the new one
        if(component.getTargets().equals(TargetType.VARIANT)){
            component.setTargets(TargetType.ALL);
        }
        /// If the player has ALL as target, he needs to be hurt (or an ally has to die, but that depends on the trigger)*/
        if(component.getTargets().equals(TargetType.ALL)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+ TriggerConfig.WIND_ALL_VERY_LOW_HEALTH;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.WIND_ALL_SURROUNDED;
            }

            if(CheckUtils.checkFalling(player) && player.fallDistance > fall_trigger){
                trigger_sum = trigger_sum+TriggerConfig.WIND_ALL_FALLING_HIGH;
            }else  if(CheckUtils.checkFalling(player)){
                trigger_sum = trigger_sum+TriggerConfig.WIND_ALL_FALLING;
            }

            if(checkLightConditions(player)){
                trigger_sum = trigger_sum+TriggerConfig.WIND_ALL_CONDITIONS;
            }

            if(trigger_sum >= getMinTrigger()) {
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }
        else if(component.getTargets().equals(TargetType.SELF)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.WIND_SELF_VERY_LOW_HEALTH;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum = trigger_sum + TriggerConfig.WIND_SELF_LOW_HEALTH;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.WIND_SELF_SURROUNDED;
            }

            if(CheckUtils.checkFalling(player) && player.fallDistance > fall_trigger){
                trigger_sum = trigger_sum+TriggerConfig.WIND_SELF_FALLING_HIGH;
            }else  if(CheckUtils.checkFalling(player)){
                trigger_sum = trigger_sum+TriggerConfig.WIND_SELF_FALLING;
            }

            if(checkLightConditions(player)){
                trigger_sum = trigger_sum+TriggerConfig.WIND_SELF_CONDITIONS;
            }

            if(trigger_sum >= getMinTrigger()) {
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }else if(component.getTargets().equals(TargetType.ALLIES)){
            if(CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + TriggerConfig.WIND_ALLIES_ALLY_LOW_HEALTH;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.WIND_ALLIES_VERY_LOW_HEALTH;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player)  || CheckUtils.checkSurrounded(target))){
                trigger_sum = trigger_sum + TriggerConfig.WIND_ALLIES_SURROUNDED;
            }

            if(CheckUtils.checkFalling(player) && player.fallDistance > fall_trigger){
                trigger_sum = trigger_sum + TriggerConfig.WIND_ALLIES_FALLING_HIGH;
            }
            if(CheckUtils.CheckAllies.checkAlly(player, target) && CheckUtils.checkFalling(target)){
                trigger_sum = trigger_sum + TriggerConfig.WIND_ALLIES_ALLY_FALLING;
            }

            if(CheckUtils.CheckAllies.checkAlly(player, target) && CheckUtils.checkFalling(target) && target.fallDistance > fall_trigger){
                trigger_sum = trigger_sum + TriggerConfig.WIND_ALLIES_ALLY_FALLING_HIGH;
            }

            if(checkLightConditions(player)){
                trigger_sum = trigger_sum + TriggerConfig.WIND_ALLIES_CONDITIONS;
            }

            if(trigger_sum >= getMinTrigger()) {
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }
    }

    /**Used to check if the player has something that can be considered a Cold Source
     * for the Frost Light
     *
     * @param player The player to perform checks on*/
    @Override
    public boolean checkLightConditions(PlayerEntity player) {
        if(player.getY() >= 64){
            if(!player.getEntityWorld().isSkyVisible(player.getBlockPos())){
                return true;
            }
            return true;
        }

        return checkMultipleBlocksWithTags(player, Config.TRIGGER_BLOCK_RADIUS, 7, WindLight.WIND_TRIGGER_BLOCKS);
    }
}
