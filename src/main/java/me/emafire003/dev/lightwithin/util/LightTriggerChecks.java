package me.emafire003.dev.lightwithin.util;

import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.config.TriggerConfig;
import me.emafire003.dev.lightwithin.networking.LightReadyPacketS2C;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TimedTaskRunner;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class LightTriggerChecks {
    //If the sum of the things happening to player is greater than this the light activates
    public static double MIN_TRIGGER = 5;

    public static void sendReadyPacket(ServerPlayerEntity player, boolean b){
        try{
            ServerPlayNetworking.send(player, LightReadyPacketS2C.ID, new LightReadyPacketS2C(b));
        }catch(Exception e){
            LOGGER.error("FAILED to send data packets to the client!");
            e.printStackTrace();
        }
    }

    public static void checkHeal(PlayerEntity player, LightComponent component, LivingEntity attacker, LivingEntity target){
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

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
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
            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
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

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }

        }
    }

    //TODO togliere valori strani per la health percent

    public static void checkDefense(PlayerEntity player, LightComponent component, LivingEntity attacker, LivingEntity target){
        double trigger_sum = 0;
        /**CHECKS for the self part*/
        if(component.getTargets().equals(TargetType.SELF)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.DEF_SELF_VERY_LOW_HEALTH;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum =trigger_sum+TriggerConfig.DEF_SELF_LOW_HEALTH;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum+TriggerConfig.DEF_SELF_SURROUNDED;
            }
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+TriggerConfig.DEF_SELF_ARMOR_DURABILITY;
            }

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }

        }
        /**CHECKS for the allies part*/
        else if(component.getTargets().equals(TargetType.ALLIES)){
            if(CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + TriggerConfig.DEF_ALLIES_ALLY_LOW_HEALTH;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.DEF_ALLIES_VERY_LOW_HEALTH;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum+TriggerConfig.DEF_ALLIES_SURROUNDED;
            }
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum+TriggerConfig.DEF_ALLIES_ALLY_ARMOR_DURABILITY;
            }

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }

        }else if(component.getTargets().equals(TargetType.VARIANT)){
            if(CheckUtils.checkPassiveHealth(player, target, Config.HP_PERCENTAGE_VARIANT)){
                trigger_sum = trigger_sum + TriggerConfig.DEF_VARIANT_PASSIVE_LOW_HEALTH;
            }

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }

        }
    }

    public static void checkStrength(PlayerEntity player, LightComponent component, Entity attacker, Entity target){
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

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
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

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }
    }

    public static void checkBlazing(PlayerEntity player, LightComponent component, Entity attacker, Entity target){
        double trigger_sum = 0;
        /**If the player has ALL as target, he needs to be hurt (or an ally has to die, but that depends on the trigger event)*/
        if(component.getTargets().equals(TargetType.ALL)){

            //Checks if the player is very low health
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+TriggerConfig.BLAZING_ALL_VERY_LOW_HEALTH;
                //Checks if the player has low health
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ALL_LOW_HEALTH;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ALL_SURROUNDED;
            }
            //Checks if the player has low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ALL_ARMOR_DURABILITY;
            }
            //Checks if the player has the optimal criteria for activation
            if(CheckUtils.checkBlazing(player)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ALL_CONDITIONS;
            }
            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
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
            //Checks if the player'sallies have low armor durability
            if(!player.equals(target) && Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ENEMIES_ALLY_ARMOR_DURABILITY;
            }
            //Checks if the player has low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ENEMIES_ARMOR_DURABILITY;
            }
            //Checks if the player has the optimal criteria for activation
            if(CheckUtils.checkBlazing(player)){
                trigger_sum=trigger_sum+TriggerConfig.BLAZING_ENEMIES_CONDITIONS;
            }
            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }

    }

    public static void checkFrost(PlayerEntity player, LightComponent component, Entity attacker, LivingEntity target){
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

            if(CheckUtils.checkFrost(player)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_ALL_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
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

            if(CheckUtils.checkFrost(player)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_ENEMIES_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
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

            if(CheckUtils.checkFrost(player)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_SELF_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }else if(component.getTargets().equals(TargetType.ALLIES)){
            if(!player.equals(target) && CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_ALLIES_ALLY_LOW_HEALTH;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_ALLIES_VERY_LOW_HEALTH;
            }

            //Checks if the player'sallies have low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+TriggerConfig.FROST_ALLIES_ALLY_ARMOR_DURABILITY;
            }

            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.FROST_ALLIES_SURROUNDED;
            }

            if(CheckUtils.checkFrost(player)){
                trigger_sum = trigger_sum + TriggerConfig.FROST_ALLIES_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }
    }

    public static void checkEarthen(PlayerEntity player, LightComponent component, LivingEntity attacker, LivingEntity target){
        double trigger_sum = 0;
        /**If the player or their allies are on low health or surrounded, a golem will spawn if the player has the OTHER target*/
        if(component.getTargets().equals(TargetType.VARIANT)){
            if(CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + TriggerConfig.EARTHEN_VARIANT_ALLY_LOW_HEALTH;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.EARTHEN_VARIANT_VERY_LOW_HEALTH;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.EARTHEN_VARIANT_SURROUNDED;
            }

            if(CheckUtils.checkEarthen(player)){
                trigger_sum = trigger_sum + TriggerConfig.EARTHEN_VARIANT_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }
        /**CHECKS if the player has ENEMIES as target, either his or his allies health needs to be low*/
        else if(component.getTargets().equals(TargetType.ENEMIES) && (CheckUtils.CheckAllies.checkAlly(player, target) || player.equals(target))){
            if(CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + TriggerConfig.EARTHEN_ENEMIES_ALLY_LOW_HEALTH;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.EARTHEN_ENEMIES_VERY_LOW_HEALTH;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.EARTHEN_ENEMIES_SURROUNDED;
            }

            if(CheckUtils.checkEarthen(player)){
                trigger_sum = trigger_sum + TriggerConfig.EARTHEN_ENEMIES_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }else if(component.getTargets().equals(TargetType.SELF) && player.equals(target)){

            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+TriggerConfig.EARTHEN_SELF_VERY_LOW_HEALTH;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum = trigger_sum+TriggerConfig.EARTHEN_SELF_LOW_HEALTH;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.EARTHEN_SELF_SURROUNDED;
            }

            if(CheckUtils.checkEarthen(player)){
                trigger_sum = trigger_sum + TriggerConfig.EARTHEN_SELF_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }else if(component.getTargets().equals(TargetType.ALLIES)){
            if(CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum+TriggerConfig.EARTHEN_ALLIES_ALLY_LOW_HEALTH;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+TriggerConfig.EARTHEN_ALLIES_VERY_LOW_HEALTH;
            }

            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.EARTHEN_ALLIES_SURROUNDED;
            }

            if(CheckUtils.checkEarthen(player)){
                trigger_sum = trigger_sum + TriggerConfig.EARTHEN_ALLIES_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }
    }

    public static void checkWind(PlayerEntity player, LightComponent component, Entity attacker, LivingEntity target){
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
        /**If the player has ALL as target, he needs to be hurt (or an ally has to die, but that depends on the trigger)*/
        if(component.getTargets().equals(TargetType.ALL)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+TriggerConfig.WIND_ALL_VERY_LOW_HEALTH;
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

            if(CheckUtils.checkWind(player)){
                trigger_sum = trigger_sum+TriggerConfig.WIND_ALL_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
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

            if(CheckUtils.checkWind(player)){
                trigger_sum = trigger_sum+TriggerConfig.WIND_SELF_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
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

            if(CheckUtils.checkWind(player)){
                trigger_sum = trigger_sum + TriggerConfig.WIND_ALLIES_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }
    }

    //Probably needs, player, attacker, attacked, ally
    public static void checkAqua(PlayerEntity player, LightComponent component, Entity attacker, LivingEntity target){
        double trigger_sum = 0;
        /**If the player has ALL as target, he needs to be hurt (or an ally has to die, but that depends on the trigger)*/
        if(component.getTargets().equals(TargetType.ALL)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ALL_VERY_LOW_HEALTH;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ALL_SURROUNDED;
            }

            //Checks if the player'sallies have low armor durability
            if(!player.equals(target) && Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_ALL_ALLY_ARMOR_DURABILITY;
            }

            if(CheckUtils.checkAqua(player)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ALL_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }
        /**CHECKS if the player has ENEMIES as target, either his or his allies health needs to be low*/
        else if(component.getTargets().equals(TargetType.ENEMIES)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ENEMIES_VERY_LOW_HEALTH;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ENEMIES_LOW_HEALTH;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ENEMIES_SURROUNDED;
            }

            //Checks if the player'sallies have low armor durability
            if(!player.equals(target) && Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_ENEMIES_ALLY_ARMOR_DURABILITY;
            }

            if(CheckUtils.checkAqua(player)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ENEMIES_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }else if(component.getTargets().equals(TargetType.SELF)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_SELF_VERY_LOW_HEALTH;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+Config.HP_PERCENTAGE_INCREMENT)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_SELF_LOW_HEALTH;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_SELF_SURROUNDED;
            }

            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_SELF_ARMOR_DURABILITY;
            }

            if(CheckUtils.checkAqua(player)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_SELF_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }

        }else if(component.getTargets().equals(TargetType.ALLIES)){
            if(!player.equals(target) && CheckUtils.checkAllyHealth(player, attacker, Config.HP_PERCENTAGE_ALLIES+5)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ALLIES_ALLY_LOW_HEALTH;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + TriggerConfig.AQUA_ALLIES_VERY_LOW_HEALTH;
            }

            //Checks if the player'sallies have low armor durability
            if(!player.equals(target) && Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_ALLIES_ALLY_ARMOR_DURABILITY;
            }

            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_ALLIES_SURROUNDED;
            }

            if(CheckUtils.checkAqua(player)){
                trigger_sum=trigger_sum+TriggerConfig.AQUA_ALLIES_CONDITIONS;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }
    }

    public static void checkFrog(PlayerEntity player, LightComponent component, LivingEntity attacker, Entity target){
        if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+25)
                || CheckUtils.checkSurrounded(player)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
        else if(target instanceof FrogEntity){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }else if(attacker instanceof FrogEntity){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
    }
}
