package me.emafire003.dev.lightwithin.events;

import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.networking.LightReadyPacketS2C;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class LightTriggerChecks {
    //If the sum of the things happening to player is greater than this the light activates
    public static double MIN_TRIGGER = 5; //TODO maybe adjustable. The weights of the individual things too

    public static void sendReadyPacket(ServerPlayerEntity player, boolean b){
        try{
            ServerPlayNetworking.send(player, LightReadyPacketS2C.ID, new LightReadyPacketS2C(b));
        }catch(Exception e){
            LOGGER.error("FAILED to send data packets to the client!");
            e.printStackTrace();
        }
    }

    //TODO probably it should be a mix of armor and hp. Like, no armor and HP = when player is under 50%HP, when high armor/defense triggers on 10%HP
    //TODO nevermind, it should probably take into consideration the amount of the last attack damage. If the attack damage is s enough to send the player to the configured health it will trigger
    public static void checkHeal(PlayerEntity player, LightComponent component, LivingEntity attacker, LivingEntity target){
        double trigger_sum = 0;
        /**CHECKS for the self part*/
        if(component.getTargets().equals(TargetType.SELF)) {
            //Checks for very low and kinda low health
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+5;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+20)){
                trigger_sum = trigger_sum+2;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum+1;
            }
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+1;
            }

            if(CheckUtils.checkPoisoned(player)){
                trigger_sum = trigger_sum+3;
            }

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }
        /**CHECKS for the allies part*/
        else if(component.getTargets().equals(TargetType.ALLIES)){
            if(CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + 4;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+1;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum+1;
            }
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum+1;
            }

            if(CheckUtils.checkAllyPoisoned(player, target)){
                trigger_sum = trigger_sum+2;
            }
            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }

        }
        else if(component.getTargets().equals(TargetType.OTHER)){
            if(CheckUtils.checkPassiveHealth(player, target, Config.HP_PERCENTAGE_OTHER)){
                trigger_sum = 5;
            }
            if(CheckUtils.checkPoisoned(target)){
                trigger_sum = 5;
            }

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }

        }
    }

    public static void checkDefense(PlayerEntity player, LightComponent component, LivingEntity attacker, LivingEntity target){
        double trigger_sum = 0;
        /**CHECKS for the self part*/
        if(component.getTargets().equals(TargetType.SELF)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + 5;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+30)){
                trigger_sum =trigger_sum+3;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum+2;
            }
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+2;
            }

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }

        }
        /**CHECKS for the allies part*/
        else if(component.getTargets().equals(TargetType.ALLIES)){
            if(CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + 4;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + 1;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum+2;
            }
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum+2;
            }

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }

        }else if(component.getTargets().equals(TargetType.OTHER)){
            if(CheckUtils.checkPassiveHealth(player, target, Config.HP_PERCENTAGE_OTHER)){
                trigger_sum = trigger_sum + 5;
            }

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }

        }
    }

    public static void checkStrength(PlayerEntity player, LightComponent component, Entity attacker, Entity target){
        double trigger_sum = 0;
        /**CHECKS for the self part*/
        if(component.getTargets().equals(TargetType.SELF)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + 5;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+30)){
                trigger_sum = trigger_sum + 3;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum+1;
            }
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+2;
            }


            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }

        }
        /**CHECKS for the allies part*/
        else if(component.getTargets().equals(TargetType.ALLIES)){
            if(CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + 4;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum +1;
            }
            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum+1;
            }
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum+2;
            }

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }else if(component.getTargets().equals(TargetType.OTHER)
                && CheckUtils.checkPassiveHealth(player, target, Config.HP_PERCENTAGE_OTHER)
                && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_OTHER)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
    }

    public static void checkBlazing(PlayerEntity player, LightComponent component, Entity attacker, Entity target){
        double trigger_sum = 0;
        /**If the player has ALL as target, he needs to be hurt (or an ally has to die, but that depends on the trigger event)*/
        if(component.getTargets().equals(TargetType.ALL)){

            //Checks if the player is very low health
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+10)){
                trigger_sum = trigger_sum+4;
                //Checks if the player has low health
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+35)){
                trigger_sum=trigger_sum+2;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+1;
            }
            //Checks if the player has low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+1;
            }
            //Checks if the player has the optimal criteria for activation
            if(CheckUtils.checkBlazing(player)){
                trigger_sum=trigger_sum+3;
            }
            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }
        /**CHECKS if the player has ENEMIES as target, either his or his allies health needs to be low*/
        else if(component.getTargets().equals(TargetType.ENEMIES)){


            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+10)){
                trigger_sum = trigger_sum+2;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+1;
            }
            //Checks if the player'sallies have low armor durability
            if(!player.equals(target) && Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+1;
            }
            //Checks if the player has low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+1;
            }
            //Checks if the player has the optimal criteria for activation
            if(CheckUtils.checkBlazing(player)){
                trigger_sum=trigger_sum+3;
            }
            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }

    }

    //TODO check for freezing to trigger
    public static void checkFrost(PlayerEntity player, LightComponent component, Entity attacker, LivingEntity target){
        double trigger_sum = 0;
        /**If the player has ALL as target, he needs to be hurt (or an ally has to die, but that depends on the trigger)*/
        if(component.getTargets().equals(TargetType.ALL)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+5)){
                trigger_sum = trigger_sum + 4;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+25)){
                trigger_sum = trigger_sum + 2;
            }

            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+1;
            }
            //Checks if the player has low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+1;
            }

            if(CheckUtils.checkFrost(player)){
                trigger_sum = trigger_sum + 3;
            }

            if(trigger_sum >= MIN_TRIGGER){
                sendReadyPacket((ServerPlayerEntity) player, true);
            }

        }
        /**CHECKS if the player has ENEMIES as target, either his or his allies health needs to be low*/
        else if(component.getTargets().equals(TargetType.ENEMIES) && (CheckUtils.CheckAllies.checkAlly(player, target) || player.equals(target))){
            if(CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + 4;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+5)){
                trigger_sum = trigger_sum + 2;
            }

            //Checks if the player'sallies have low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+1;
            }
            //Checks if the player has low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+1;
            }

            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+1;
            }

            if(CheckUtils.checkFrost(player)){
                trigger_sum = trigger_sum + 3;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }else if(component.getTargets().equals(TargetType.SELF)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+5)){
                trigger_sum = trigger_sum + 4;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+30)){
                trigger_sum = trigger_sum + 2;
            }

            //Checks if the player has low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)){
                trigger_sum=trigger_sum+1;
            }

            if(CheckUtils.checkFrost(player)){
                trigger_sum = trigger_sum + 3;
            }

            if(trigger_sum <= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }else if(component.getTargets().equals(TargetType.ALLIES)){
            if(!player.equals(target) && CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES+5)){
                trigger_sum = trigger_sum + 4;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + 1;
            }

            //Checks if the player'sallies have low armor durability
            if(Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+1;
            }

            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+1;
            }

            if(CheckUtils.checkFrost(player)){
                trigger_sum = trigger_sum + 3;
            }

            if(trigger_sum <= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }
    }

    public static void checkEarthen(PlayerEntity player, LightComponent component, LivingEntity attacker, LivingEntity target){
        double trigger_sum = 0;
        /**If the player or their allies are on low health or surrounded, a golem will spawn if the player has the OTHER target*/
        if(component.getTargets().equals(TargetType.OTHER)){
            if(CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + 4;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+5)){
                trigger_sum = trigger_sum + 3;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+1;
            }

            if(CheckUtils.checkEarthen(player)){
                trigger_sum = trigger_sum + 3;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }
        /**CHECKS if the player has ENEMIES as target, either his or his allies health needs to be low*/
        else if(component.getTargets().equals(TargetType.ENEMIES) && (CheckUtils.CheckAllies.checkAlly(player, target) || player.equals(target))){
            if(CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum + 3;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+5)){
                trigger_sum = trigger_sum + 4;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+1;
            }

            if(CheckUtils.checkEarthen(player)){
                trigger_sum = trigger_sum + 3;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }else if(component.getTargets().equals(TargetType.SELF) && player.equals(target)){

            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+3;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+20)){
                trigger_sum = trigger_sum+2;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+1;
            }

            if(CheckUtils.checkEarthen(player)){
                trigger_sum = trigger_sum + 3;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }else if(component.getTargets().equals(TargetType.ALLIES)){
            if(CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES)){
                trigger_sum = trigger_sum+4;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum+1;
            }

            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+1;
            }

            if(CheckUtils.checkEarthen(player)){
                trigger_sum = trigger_sum + 3;
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
        int fall_trigger = 25;
        int fe_fa_level = EnchantmentHelper.getLevel(Enchantments.FEATHER_FALLING, boots);

        if(fe_fa_level == 2){
            fall_trigger = 30;
        }else if(fe_fa_level == 3){
            fall_trigger = 35;
        }else if(fe_fa_level >= 4){
            fall_trigger = 45;
        }
        /**If the player has ALL as target, he needs to be hurt (or an ally has to die, but that depends on the trigger)*/
        if(component.getTargets().equals(TargetType.OTHER)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+5) && player.equals(target)){
                trigger_sum = trigger_sum+4;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+1;
            }

            //TODO maybe remove the checkfalling for the OTHER target
            if(CheckUtils.checkFalling(player)){
                trigger_sum=trigger_sum+1;
            }

            if(CheckUtils.checkFalling(player) && player.fallDistance > fall_trigger){
                trigger_sum = trigger_sum+3;
            }
            if(CheckUtils.checkWind(player)){
                trigger_sum = trigger_sum+3;
            }


            //TODO quando c'è il falling check, se i blocchi sono più di 25 + 10 o boh ogni feather falling triggera sicuro. Sennò deve usare anche altro

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }
        //TODO maybe remove player equals target
        else if(component.getTargets().equals(TargetType.SELF)&& player.equals(target)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+5)){
                trigger_sum = trigger_sum + 4;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+30)){
                trigger_sum = trigger_sum + 1;
            }
            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+1;
            }

            if(CheckUtils.checkFalling(player)){
                trigger_sum = trigger_sum+1;
            }

            //boots.getEnchantments().contains() //TODO see how this works
            if(CheckUtils.checkFalling(player) && player.fallDistance > fall_trigger){
                trigger_sum = trigger_sum+4;
            }
            if(CheckUtils.checkWind(player)){
                trigger_sum = trigger_sum+3;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }else if(component.getTargets().equals(TargetType.ALLIES)){
            if(CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES+5)){
                trigger_sum = trigger_sum + 4;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + 1;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player)  || CheckUtils.checkSurrounded(target))){
                trigger_sum = trigger_sum + 1;
            }

            if(CheckUtils.checkFalling(player) && player.fallDistance > 10){
                trigger_sum = trigger_sum + 2;
            }

            //TODO maybe check the height for the allies too?
            if(CheckUtils.CheckAllies.checkAlly(player, target) && CheckUtils.checkFalling(target)){
                trigger_sum = trigger_sum + 2;
            }

            if(CheckUtils.CheckAllies.checkAlly(player, target) && CheckUtils.checkFalling(target) && target.fallDistance > 20){
                trigger_sum = trigger_sum + 4;
            }

            if(CheckUtils.checkWind(player)){
                trigger_sum = trigger_sum + 3;
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
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+5)){
                trigger_sum = trigger_sum + 4;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum + 1;
            }

            //Checks if the player'sallies have low armor durability
            if(!player.equals(target) && Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+1;
            }

            if(CheckUtils.checkAqua(player)){
                trigger_sum = trigger_sum + 3;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }
        /**CHECKS if the player has ENEMIES as target, either his or his allies health needs to be low*/
        else if(component.getTargets().equals(TargetType.ENEMIES)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+5)){
                trigger_sum = trigger_sum + 3;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+30)){
                trigger_sum = trigger_sum + 2;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum + 1;
            }

            //Checks if the player'sallies have low armor durability
            if(!player.equals(target) && Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+1;
            }

            if(CheckUtils.checkAqua(player)){
                trigger_sum = trigger_sum + 3;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }else if(component.getTargets().equals(TargetType.SELF)){
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+5)){
                trigger_sum = trigger_sum + 4;
            }else if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+30)){
                trigger_sum = trigger_sum + 2;
            }

            if(Config.CHECK_SURROUNDED && (CheckUtils.checkSurrounded(player))){
                trigger_sum = trigger_sum + 1;
            }

            //Checks if the player'sallies have low armor durability
            if(!player.equals(target) && Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+1;
            }

            if(CheckUtils.checkAqua(player)){
                trigger_sum = trigger_sum + 3;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }else if(component.getTargets().equals(TargetType.ALLIES)){
            if(!player.equals(target) && CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES+5)){
                trigger_sum = trigger_sum + 4;
            }
            if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF)){
                trigger_sum = trigger_sum + 1;
            }

            //Checks if the player'sallies have low armor durability
            if(!player.equals(target) && Config.CHECK_ARMOR_DURABILITY && CheckUtils.checkAllyArmor(player, target, Config.DUR_PERCENTAGE_ALLIES)){
                trigger_sum=trigger_sum+1;
            }

            //Checks if the player is surrounded
            if(Config.CHECK_SURROUNDED && CheckUtils.checkSurrounded(player)){
                trigger_sum=trigger_sum+1;
            }

            if(CheckUtils.checkAqua(player)){
                trigger_sum=trigger_sum+3;
            }

            if(trigger_sum >= MIN_TRIGGER) {
                sendReadyPacket((ServerPlayerEntity) player, true);
            }
        }
    }

    public static void checkFrog(PlayerEntity player, LightComponent component, LivingEntity attacker, Entity target){
        if(CheckUtils.checkSelfDanger(player, Config.HP_PERCENTAGE_SELF+15)
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
