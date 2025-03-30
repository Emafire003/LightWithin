package me.emafire003.dev.lightwithin.lights;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.getAllies;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.getMinTrigger;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.sendLightTriggered;

public class StrengthLight extends InnerLight {

    public static final Item INGREDIENT = Items.BLAZE_POWDER;
    private final List<TargetType> possibleTargetTypes = Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.VARIANT);
    private final List<TriggerChecks> triggerChecks = List.of(TriggerChecks.ENTITY_ATTACKS_ENTITY, TriggerChecks.ALLY_ATTACKED);
    private final Identifier lightId = LightWithin.getIdentifier("strength");

    /**
     * Creates an instance of this InnerLight
     *
     * @param regex A lambda function, which provides you with a string representing the UUID portion dedicated to type determination.
     *              You have to provide a check based on the string for which the player will have that particular light.
     *              Remember that the order with which the lights are registered matters a lot!
     */
    public StrengthLight(TypeCreationRegex regex) {
        super(regex);
        color = "strength";
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
        if(power_multiplier > BalanceConfig.STRENGTH_MAX_POWER){
            power_multiplier = BalanceConfig.STRENGTH_MAX_POWER;
        }
        int max_duration = BalanceConfig.STRENGTH_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.STRENGTH_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(duration > max_duration){
            duration = max_duration;
        }
        if(duration < BalanceConfig.STRENGTH_MIN_DURATION){
            duration = BalanceConfig.STRENGTH_MIN_DURATION;
        }
        if(power_multiplier < BalanceConfig.STRENGTH_MIN_POWER){
            power_multiplier = BalanceConfig.STRENGTH_MIN_POWER;
        }
        return new Pair<>(power_multiplier, duration);
    }

    @Override
    public void triggerCheck(PlayerEntity player, LightComponent component, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        double trigger_sum = 0;
        /**CHECKS for the self part*/
        if(component.getTargets().equals(TargetType.SELF) || component.getTargets().equals(TargetType.VARIANT)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.STR_SELF_VARIANT_VERY_LOW_HEALTH;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum = trigger_sum + TriggerConfig.STR_SELF_VARIANT_LOW_HEALTH;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum+TriggerConfig.STR_SELF_VARIANT_SURROUNDED;
            }
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+TriggerConfig.STR_SELF_VARIANT_ARMOR_DURABILITY;
            }

            if(trigger_sum >= getMinTrigger()){
                sendLightTriggered((ServerPlayerEntity) player);
            }

        }
        /**CHECKS for the allies part*/
        else if(component.getTargets().equals(TargetType.ALLIES)){
            if(CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + TriggerConfig.STR_ALLIES_ALLY_LOW_HEALTH;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum +TriggerConfig.STR_ALLIES_VERY_LOW_HEALTH;
            }
            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum+TriggerConfig.STR_ALLIES_SURROUNDED;
            }
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum+TriggerConfig.STR_ALLIES_ALLY_ARMOR_DURABILITY;
            }

            if(trigger_sum >= getMinTrigger()){
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }
    }

    @Override
    public void startActivation(LightComponent component, PlayerEntity player) {
        List<LivingEntity> targets = new ArrayList<>();

        if(component.getTargets().equals(TargetType.SELF)){
            targets.add(player);
            player.sendMessage(Text.translatable("light.description.activation.strength.self"), true);
        }else if(component.getTargets().equals(TargetType.VARIANT)){
            targets.add(player);
            player.sendMessage(Text.translatable("light.description.activation.strength.variant"), true);
        }
        else if(component.getTargets().equals(TargetType.ALLIES)){
            targets.addAll(getAllies(player));
            player.sendMessage(Text.translatable("light.description.activation.strength.allies"), true);
        }

        activate(player, targets, component.getPowerMultiplier(), component.getDuration(), component.getMaxCooldown());
    }

    @Override
    protected void activate(PlayerEntity caster, List<LivingEntity> targets, double power_multiplier, int duration, double cooldown_time) {
        power_multiplier = checkSafety(power_multiplier, duration).getFirst();
        duration = checkSafety(power_multiplier, duration).getSecond();

        LightComponent component = LightWithin.LIGHT_COMPONENT.get(caster);
        super.activate(caster, targets, power_multiplier, duration, cooldown_time);

        caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()), LightSounds.STRENGTH_LIGHT, SoundCategory.PLAYERS, 1f, 1f);
        for(LivingEntity target : targets){
            //target.playSound(LightSounds.STRENGTH_LIGHT, 1, 1);
            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.STRENGTHLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
            }
            if(component.getTargets().equals(TargetType.VARIANT)){
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) power_multiplier, false, false));
            }else if(target.equals(caster) && component.getTargets().equals(TargetType.ALLIES)){
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) (power_multiplier/Config.DIV_SELF), false, false));
            } else{
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) power_multiplier, false, false));
            }
        }
    }

}
