package me.emafire003.dev.lightwithin.lights;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.config.TriggerConfig;
import me.emafire003.dev.lightwithin.particles.LightParticles;
import me.emafire003.dev.lightwithin.sounds.LightSounds;
import me.emafire003.dev.lightwithin.particles.LightParticlesUtil;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.lightwithin.util.TriggerChecks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.getAllies;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.*;

public class HealLight extends InnerLight {

    public final Item INGREDIENT = Items.GOLDEN_APPLE;
    private final Identifier lightId = LightWithin.getIdentifier("heal");

    /*per memoria storica, i primi appunti sulle lights li prendevo cosi:

    Possible triggers:
       - self low health
       - allies low health (checkable like this if type = Heal && target = allies do stuff TOD.O include pets in this
       - passive mobs on low health
     */

    /*Triggers when:
     * - On less that 25% hp
     * - On being poisoned
     * - (Optionally) when surrounded?
     *
     * - Those apply to allies as well
     */

    /*Possible targets:
     * - self
     * - allies
     * - Passive mobs & self*/

    /*public HealLight(List<LivingEntity> targets, double cooldown_time, double power_multiplier, int duration, PlayerEntity caster) {
        super(targets, cooldown_time, power_multiplier, duration, caster);
        type = InnerLightType.HEAL;
        //color = "#ff4432";
        color = "heal";
    }*/


    private final List<TargetType> possibleTargetTypes = Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.VARIANT);

    private final List<TriggerChecks> triggerChecks = List.of(TriggerChecks.ENTITY_ATTACKED);

    public HealLight(TypeCreationRegex regex) {
        super(regex);
        this.color = "heal";
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
    protected Pair<Double, Integer> checkSafety(double power_multiplier, int duration) {
        if(power_multiplier > BalanceConfig.HEAL_MAX_POWER){
            power_multiplier = BalanceConfig.HEAL_MAX_POWER;
        }
        int max_duration = BalanceConfig.HEAL_MAX_DURATION;
        if(Config.MULTIPLY_DURATION_LIMIT){
            max_duration = (int) (BalanceConfig.HEAL_MAX_DURATION * Config.DURATION_MULTIPLIER);
        }
        if(duration > max_duration){
            duration = max_duration;
        }
        if(power_multiplier < BalanceConfig.HEAL_MIN_POWER){
            power_multiplier = BalanceConfig.HEAL_MIN_POWER;
        }
        if(duration < BalanceConfig.HEAL_MIN_DURATION){
            duration = BalanceConfig.HEAL_MIN_DURATION;
        }
        return new Pair<>(power_multiplier, duration);
    }

    @Override
    public void startActivation(LightComponent component, PlayerEntity player) {
        List<LivingEntity> targets = new ArrayList<>();

        if(component.getTargets().equals(TargetType.SELF)){
            targets.add(player);
            player.sendMessage(Text.translatable("light.description.activation.heal.allies"), true);
        }
        //There could be a bug where the player stands near only 1 ally that is 50% life or lower and
        // then enderpearls to other companions and cures them. But it's ok because of lore,
        // like the light saw an ally struggling and activated. Then it heals whoever is near.
        // It's not a bug, it's a feature now.
        //Yay.
        else if(component.getTargets().equals(TargetType.ALLIES)){
            targets.addAll(getAllies(player));
            player.sendMessage(Text.translatable("light.description.activation.heal.allies"), true);
        }

        //Finds peaceful creatures and allies, also the player
        else if(component.getTargets().equals(TargetType.VARIANT)){
            targets.addAll(player.getWorld().getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true)));
            List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
            targets.add(player);
            for(LivingEntity ent : entities){
                // may need this to prevent bugs EDIT i don't even remember what "this" referred to eheh
                if(CheckUtils.CheckAllies.checkAlly(player, ent)){
                    if(Config.ALWAYS_AFFECT_ALLIES || CheckUtils.checkSelfDanger(ent, Config.HP_PERCENTAGE_ALLIES)){
                        targets.add(ent);
                    }
                }else if(ent instanceof TameableEntity){
                    if(player.equals(((TameableEntity) ent).getOwner())){
                        if(Config.ALWAYS_AFFECT_ALLIES || CheckUtils.checkSelfDanger(ent, Config.HP_PERCENTAGE_VARIANT)){
                            targets.add(ent);
                        }
                    }
                }
            }
            player.sendMessage(Text.translatable("light.description.activation.heal.variant"), true);
        }
        activate(player, targets, component.getPowerMultiplier(), component.getDuration(), component.getMaxCooldown());
    }

    @Override
    protected void activate(PlayerEntity caster, List<LivingEntity> targets, double power_multiplier, int duration, double cooldown_time) {
        power_multiplier = checkSafety(power_multiplier, duration).getFirst();
        duration = checkSafety(power_multiplier, duration).getSecond();

        LightComponent component = LightWithin.LIGHT_COMPONENT.get(caster);
        super.activate(caster, targets, power_multiplier, duration, cooldown_time);

        caster.getWorld().playSound(null, BlockPos.ofFloored(caster.getPos()), LightSounds.HEAL_LIGHT, SoundCategory.PLAYERS, 1f, 1f);

        //caster.getWorld().playSound(caster.getX(), caster.getY(), caster.getZ(), LightSounds.HEAL_LIGHT, SoundCategory.PLAYERS, 0.1f, 1, true);
        //caster.getWorld().playSound(caster, caster.getBlockPos(), LightSounds.HEAL_LIGHT, SoundCategory.AMBIENT, 1,1);

        for(LivingEntity target : targets){


            //target.playSound(LightSounds.HEAL_LIGHT, 1, 1);
            if(!caster.getWorld().isClient){
                LightParticlesUtil.spawnLightTypeParticle(LightParticles.HEALLIGHT_PARTICLE, (ServerWorld) caster.getWorld(), target.getPos());
            }
            //LightParticlesUtil.spawnLightTypeParticle(LightParticles.HEALLIGHT_PARTICLE, target);
            if(target.equals(caster) && component.getTargets().equals(TargetType.ALLIES)){
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) (power_multiplier/Config.DIV_SELF), false, false));
            }else if(component.getTargets().equals(TargetType.VARIANT)){
                List<RegistryEntry<StatusEffect>> remove_status_list = new ArrayList<>();
                target.getActiveStatusEffects().forEach((statusEffect, instance) -> {
                    if(statusEffect.value().getCategory().equals(StatusEffectCategory.HARMFUL) && statusEffect != LightEffects.LIGHT_FATIGUE){
                        remove_status_list.add(statusEffect);
                    }
                });
                remove_status_list.forEach(target::removeStatusEffect);

                remove_status_list.clear();
            }else{
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, caster.getStatusEffect(LightEffects.LIGHT_ACTIVE).getDuration(), (int) power_multiplier, false, false));
            }
        }
    }

    @Override
    public void triggerCheck(PlayerEntity player, LightComponent component, LivingEntity attacker, LivingEntity target){
        double trigger_sum = 0;
        /**CHECKS for the self part*/
        if(component.getTargets().equals(TargetType.SELF)) {
            //Checks for very low and kinda low health
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_SELF_VERY_LOW_HEALTH;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_SELF_LOW_HEALTH;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_SELF_SURROUNDED;
            }
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_SELF_ARMOR_DURABILITY;
            }

            if(CheckUtils.checkPoisoned(player)){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_SELF_POISONED;
            }

            if(trigger_sum >= getMinTrigger()){
                sendLightTriggered((ServerPlayerEntity) player);
            }
        }
        /**CHECKS for the allies part*/
        else if(component.getTargets().equals(TargetType.ALLIES)){
            if(CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_ALLIES_ALLY_LOW_HEALTH;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_ALLIES_VERY_LOW_HEALTH;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_ALLIES_SURROUNDED;
            }
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_ALLIES_ALLY_ARMOR_DURABILITY;
            }

            if(CheckUtils.checkAllyPoisoned(player, target)){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_ALLIES_ALLY_POISONED;
            }
            if(trigger_sum >= getMinTrigger()){
                sendLightTriggered((ServerPlayerEntity) player);
            }

        }
        else if(component.getTargets().equals(TargetType.VARIANT)){
            if(CheckUtils.checkPassiveHealth(player, target, Config.HP_PERCENTAGE_VARIANT)){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_VARIANT_PASSIVE_LOW_HEALTH;
            }
            if(CheckUtils.checkHasHarmfulStatusEffect(target)){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_VARIANT_OTHER_HARMFUL_EFFECT;
            }else if(CheckUtils.checkHasHarmfulStatusEffect(player)){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_VARIANT_HARMFUL_EFFECT;
            }

            if(CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_VARIANT_ALLY_LOW_HEALTH;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.HEAL_VARIANT_VERY_LOW_HEALTH;
            }

            if(trigger_sum >= getMinTrigger()){
                sendLightTriggered((ServerPlayerEntity) player);
            }

        }
    }

    @Override
    public Item getIngredient() {
        return INGREDIENT;
    }

    @Override
    public Identifier getLightId() {
        return lightId;
    }
}
