package me.emafire003.dev.lightwithin.events;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.networking.LightReadyPacketS2C;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;

import java.util.*;

import static me.emafire003.dev.lightwithin.LightWithin.*;

public class LightTriggeringAndEvents {

    public static void sendReadyPacket(ServerPlayerEntity player, boolean b){
        try{
            ServerPlayNetworking.send(player, LightReadyPacketS2C.ID, new LightReadyPacketS2C(b));
        }catch(Exception e){
            LOGGER.error("FAILED to send data packets to the client!");
            e.printStackTrace();
        }
    }

    public static void checkHeal(PlayerEntity player, LightComponent component, LivingEntity attacker, LivingEntity target){
        /**CHECKS for the self part*/
        if(component.getTargets().equals(TargetType.SELF)
                && CheckUtils.checkSelfHealth(player, Config.HP_PERCENTAGE_SELF)
                &&  ((CheckUtils.checkSurrounded(player)
                    && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF))
                 || CheckUtils.checkPoisoned(player))
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
        /**CHECKS for the allies part*/
        else if(component.getTargets().equals(TargetType.ALLIES)
                && CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES)
                &&  ((CheckUtils.checkSurrounded(player)
                && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF))
                || CheckUtils.checkPoisoned(target))
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }else if(component.getTargets().equals(TargetType.OTHER)
                && CheckUtils.checkPassiveHealth(player, target, Config.HP_PERCENTAGE_OTHER)
                && (CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_OTHER) || CheckUtils.checkPoisoned(target))
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
    }

    public static void checkDefense(PlayerEntity player, LightComponent component, LivingEntity attacker, LivingEntity target){
        /**CHECKS for the self part*/
        if(component.getTargets().equals(TargetType.SELF)
                && CheckUtils.checkSelfHealth(player, Config.HP_PERCENTAGE_SELF)
                && CheckUtils.checkSurrounded(player)
                && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)
            ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
        /**CHECKS for the allies part*/
        else if(component.getTargets().equals(TargetType.ALLIES)
                && CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES)
                && CheckUtils.checkSurrounded(player)
                && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_ALLIES)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }else if(component.getTargets().equals(TargetType.OTHER)
                && CheckUtils.checkPassiveHealth(player, target, Config.HP_PERCENTAGE_OTHER)
                && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_OTHER)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
    }

    public static void checkStrength(PlayerEntity player, LightComponent component, Entity attacker, Entity target){
        /**CHECKS for the self part*/
        if(component.getTargets().equals(TargetType.SELF)
                && CheckUtils.checkSelfHealth(player, Config.HP_PERCENTAGE_SELF)
                && CheckUtils.checkSurrounded(player)
                && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
        /**CHECKS for the allies part*/
        else if(component.getTargets().equals(TargetType.ALLIES)
                && CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES)
                && CheckUtils.checkSurrounded(player)
                && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_ALLIES)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }else if(component.getTargets().equals(TargetType.OTHER)
                && CheckUtils.checkPassiveHealth(player, target, Config.HP_PERCENTAGE_OTHER)
                && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_OTHER)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
    }

    public static void checkBlazing(PlayerEntity player, LightComponent component, Entity attacker, Entity target){
        /**If the player has ALL as target, he needs to be hurt (or an ally has to die, but that depends on the trigger)*/
        if(component.getTargets().equals(TargetType.ALL)
                && CheckUtils.checkSelfHealth(player, Config.HP_PERCENTAGE_SELF+10)
                && CheckUtils.checkSurrounded(player)
                && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)
                && CheckUtils.checkBlazing(player)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
        /**CHECKS if the player has ENEMIES as target, either his or his allies health needs to be low*/
        else if(component.getTargets().equals(TargetType.ENEMIES)
                && CheckUtils.checkSelfHealth(player, Config.HP_PERCENTAGE_SELF+10)
                && CheckUtils.checkSurrounded(player)
                && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_ALLIES)
                && CheckUtils.checkBlazing(player)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
    }

    public static void checkFrost(PlayerEntity player, LightComponent component, Entity attacker, LivingEntity target){
        /**If the player has ALL as target, he needs to be hurt (or an ally has to die, but that depends on the trigger)*/
        if(component.getTargets().equals(TargetType.ALL)
                && CheckUtils.checkSelfHealth(player, Config.HP_PERCENTAGE_SELF+5)
                && CheckUtils.checkSurrounded(player)
                && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_SELF)
                && CheckUtils.checkFrost(player)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
        /**CHECKS if the player has ENEMIES as target, either his or his allies health needs to be low*/
        else if(component.getTargets().equals(TargetType.ENEMIES)
                && (CheckUtils.CheckAllies.checkAlly(player, target) || player.equals(target))
                && (CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES+5) || CheckUtils.checkSelfHealth(player, Config.HP_PERCENTAGE_SELF+5))
                && CheckUtils.checkSurrounded(player)
                && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_ALLIES)
                && CheckUtils.checkFrost(player)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }else if(component.getTargets().equals(TargetType.SELF)
                && CheckUtils.checkSelfHealth(player, Config.HP_PERCENTAGE_SELF+5)
                && CheckUtils.checkSurrounded(player)
                && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_ALLIES)
                && CheckUtils.checkFrost(player)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }else if(component.getTargets().equals(TargetType.ALLIES)
                && CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES+5)
                && CheckUtils.checkSurrounded(player)
                && CheckUtils.checkArmorDurability(player, Config.DUR_PERCENTAGE_ALLIES)
                && CheckUtils.checkFrost(player)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
    }

    public static void checkEarthen(PlayerEntity player, LightComponent component, LivingEntity attacker, LivingEntity target){
        /**If the player or their allies are on low health or surrounded, a golem will spawn if the player has the OTHER target*/
        if(component.getTargets().equals(TargetType.OTHER)
                && (CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES) || CheckUtils.checkSelfHealth(player, Config.HP_PERCENTAGE_SELF+5))
                && CheckUtils.checkSurrounded(player)
                && CheckUtils.checkEarthen(player)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
        /**CHECKS if the player has ENEMIES as target, either his or his allies health needs to be low*/
        else if(component.getTargets().equals(TargetType.ENEMIES)
                && (CheckUtils.CheckAllies.checkAlly(player, target) || player.equals(target))
                && (CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES) || CheckUtils.checkSelfHealth(player, Config.HP_PERCENTAGE_SELF+5))
                && CheckUtils.checkSurrounded(player)
                && CheckUtils.checkEarthen(player)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }else if(component.getTargets().equals(TargetType.SELF)
                && player.equals(target)
                && CheckUtils.checkSelfHealth(player, Config.HP_PERCENTAGE_SELF)
                && CheckUtils.checkSurrounded(player)
                && CheckUtils.checkEarthen(player)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }else if(component.getTargets().equals(TargetType.ALLIES)
                && CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES)
                && CheckUtils.checkSurrounded(player)
                && CheckUtils.checkEarthen(player)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
    }

    public static void checkWind(PlayerEntity player, LightComponent component, Entity attacker, LivingEntity target){
        /**If the player has ALL as target, he needs to be hurt (or an ally has to die, but that depends on the trigger)*/
        if(component.getTargets().equals(TargetType.OTHER)
                && ((CheckUtils.checkSelfHealth(player, Config.HP_PERCENTAGE_SELF+5)
                && player.equals(target)
                && CheckUtils.checkSurrounded(player)) || CheckUtils.checkFalling(player))
                && CheckUtils.checkWind(player)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
        else if(component.getTargets().equals(TargetType.SELF)
                && player.equals(target)
                && ((CheckUtils.checkSelfHealth(player, Config.HP_PERCENTAGE_SELF+5)
                && CheckUtils.checkSurrounded(player)) || CheckUtils.checkFalling(player))
                && CheckUtils.checkWind(player)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }else if(component.getTargets().equals(TargetType.ALLIES)
                && (CheckUtils.checkAllyHealth(player, target, Config.HP_PERCENTAGE_ALLIES+5)
                && (CheckUtils.checkSurrounded(player)  || CheckUtils.checkSurrounded(target))
                )||(CheckUtils.checkFalling(player) || CheckUtils.checkFalling(target))
                && CheckUtils.checkWind(player)
        ){
            sendReadyPacket((ServerPlayerEntity) player, true);
        }
    }

    public static void checkFrog(PlayerEntity player, LightComponent component, LivingEntity attacker, Entity target){
        if(CheckUtils.checkSelfHealth(player, Config.HP_PERCENTAGE_SELF+15)
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

    /**Checks if you can trigger the light or not
     * */
    public static boolean isTriggerable(PlayerEntity player){
        if(player.getWorld().isClient){
            return false;
        }
        if(LIGHT_COMPONENT.get(player).getType().equals(InnerLightType.NONE)){
            return false;
        }
        if(player.hasStatusEffect(LightEffects.LIGHT_FATIGUE)){
            return false;
        }
        if(player.hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
            return false;
        }
        return true;
    }

    public static void entityAttackAllyEntityTriggerCheck(PlayerEntity player, LivingEntity attacker, LivingEntity target){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);
        if(component.getType().equals(InnerLightType.STRENGTH)){
            checkStrength(player, component, attacker, target);
        }
        if(component.getType().equals(InnerLightType.BLAZING)){
            checkBlazing(player, component, attacker, target);
        }
    }

    public static void entityAttackEntityTriggerCheck(PlayerEntity player, LivingEntity attacker, LivingEntity target){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);
        if(component.getType().equals(InnerLightType.HEAL)){
            checkHeal(player, component, attacker, target);
        }
        if(component.getType().equals(InnerLightType.DEFENCE)){
            checkDefense(player, component, attacker, target);
        }
        if(component.getType().equals(InnerLightType.FROST)){
            checkFrost(player, component, attacker, target);
        }
        if(component.getType().equals(InnerLightType.EARTHEN)){
            checkEarthen(player, component, attacker, target);
        }
        if(component.getType().equals(InnerLightType.WIND)){
            checkWind(player, component, attacker, target);
        }
        if(component.getType().equals(InnerLightType.FROG)){
            checkFrog(player, component, attacker, target);
        }
    }

    public static void registerListeners(){
        LOGGER.info("Registering events listeners...");

        //TODO lights could be levelled up maybe

        //Player (or other entity) being attacked by something else
        EntityAttackEntityEvent.EVENT.register(((attacker, target) -> {
            //Checks if someone is attacked and if they are the one getting attacked
            //If the target is the player with the light, he is also the target
            if(target instanceof PlayerEntity){
                entityAttackEntityTriggerCheck((PlayerEntity) target, attacker, (PlayerEntity) target);
                return;
            }
            //if the target is a pet of someone with a light, the pet is the target. (He is also considered an ally)
            if(target instanceof TameableEntity){
                if(((TameableEntity) target).getOwner() instanceof PlayerEntity){
                    entityAttackEntityTriggerCheck((PlayerEntity) ((TameableEntity) target).getOwner(), attacker, target);
                }
                return;
            }

            //if the one getting attacked is a passive entity, the entity is the target
            //while the player who triggers the light is the one nearby
            if(target instanceof PassiveEntity){
                List<PlayerEntity> entities1 = target.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(target.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(target)){
                        entityAttackEntityTriggerCheck(p, attacker, target);
                    }
                }
            }

            //if someone/something gets attaccked and is an ally of a player nearby the target is the one getting attacked,
            //while the player who triggers the light is the one present nearby
            //if(target.getScoreboardTeam() != null){
            List<PlayerEntity> entities = target.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(target.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, target) && !p.equals(target)){
                    entityAttackEntityTriggerCheck(p, attacker, target);
                    entityAttackAllyEntityTriggerCheck(p, attacker, target);
                }
            }
            //}

        }));

        //Player attacking something
        //Will need the stuff that is here to the other thingy up there
        //TODO may need reworking. Currently if the ally gets attacked it will trigger the light of the allied player
        //TODO so, it wouldn't actually be the player the one attaccking and then activating :/
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->{
            if(!isTriggerable(player)){
                return ActionResult.PASS;
            }
            LightComponent component = LIGHT_COMPONENT.get(player);
            if(component.getType().equals(InnerLightType.STRENGTH)){
                checkStrength(player, component, player, entity);
            }
            if(component.getType().equals(InnerLightType.BLAZING)){
                checkBlazing(player, component, player, entity);
            }
            if(component.getType().equals(InnerLightType.FROG)){
                checkFrog(player, component, player, entity);
            }
            return ActionResult.PASS;
        } );

        AllyDeathEvent.EVENT.register(((entity, source) -> {
            List<PlayerEntity> players = entity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(entity.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));

            for(PlayerEntity player : players){
                if(CheckUtils.CheckAllies.checkAlly(entity, player)){
                    /** Start to check for potential lights from here*/
                    if(!isTriggerable(player)){
                        return;
                    }
                    LightComponent component = LIGHT_COMPONENT.get(player);
                    //needs to default to something sooo
                    Entity attacker = entity;
                    if(source.getSource() != null){
                        attacker = source.getSource();
                    }
                    if(component.getType().equals(InnerLightType.BLAZING)){
                        checkBlazing(player, component, attacker, entity);
                    }
                    if(component.getType().equals(InnerLightType.FROST)){
                        checkFrost(player, component, attacker, entity);
                    }
                    if(component.getType().equals(InnerLightType.WIND)){
                        checkWind(player, component, attacker, entity);
                    }
                    /**End*/
                }
            }

        }));

        PlayerJoinEvent.EVENT.register((player, server) -> {
            if(player.getWorld().isClient){
                return ActionResult.PASS;
            }
            createUniqueLight(player);

            return ActionResult.PASS;
        });
    }

    public static void createUniqueLight(PlayerEntity player){
        LightComponent component = LIGHT_COMPONENT.get(player);
        String id = player.getUuidAsString().toLowerCase();
        //3eec9f18-1d0e-3f17-917c-6994e7d034d1

        if(Config.RESET_ON_JOIN){
            component.clear();
        }
        if(!component.getType().equals(InnerLightType.NONE) || component.getType() == null){
            return;
        }
        String[] id_bits = id.split("-");
        //Type bit & target bit
        //If the second part of the UUID starts with a letter form a to h && the second character is a digit -> Heal
        Pair<InnerLightType, TargetType> type_and_target = determineTypeAndTarget(id_bits, 1, 3);
        //type
        component.setType(type_and_target.getFirst());
        //Target
        component.setTargets(type_and_target.getSecond());

        //Cooldown Bit
        //The max cooldown is 100
        component.setMaxCooldown(determineCooldown(id_bits, 0));

        //Duration bit
        component.setDuration(determineDuration(id_bits, 2));

        //Power bit
        component.setPowerMultiplier(determinePower(id_bits, 4));

        component.setRainbow(true);
    }

    public static TargetType determineAttackTarget(String[] id_bits, int target_bit){
        //If it's all letters or numbers, or if there is at least one number from 5-9 or e/f then all
        //NOPE --if the char at the position 2 is abc && the nextone is a digit then it's other-- NOPE
        //in the other cases it's enemies
        boolean all_cond1 = id_bits[target_bit].matches("[0-9]+") || id_bits[target_bit].matches("[f-p]+");
        boolean all_cond2 = false;
        for(int i = 0; i<id_bits[target_bit].length()-1; i++){
            if(all_cond1){
                break;
            }
            char a = id_bits[target_bit].charAt(i);
            if(String.valueOf(a).matches("[5-9]")){
                all_cond2 = true;
                break;
            }
        }
        if(all_cond1 || all_cond2){
            return TargetType.ALL;
        }/*else if(String.valueOf(id_bits[target_bit].charAt(2)).matches("[a-c]") && Character.isDigit(id_bits[target_bit].charAt(3))){
            return TargetType.OTHER;
        }else*/{
            return TargetType.ENEMIES;
        }
    }

    /**
     *
     *
     * @param targets_ordered A list of targets. The first object on the list will be most likely and so on.*/
    public static TargetType determineTarget(String[] id_bits, int target_bit, List<TargetType> targets_ordered){
        //If it's all letters or numbers, or if there is at least one number from 5-9 or e/f then all
        //NOPE --if the char at the position 2 is abc && the nextone is a digit then it's other-- NOPE
        //in the other cases it's enemies
        boolean cond1 = id_bits[target_bit].matches("[0-9]+") || id_bits[target_bit].matches("[f-p]+");
        boolean cond2 = false;
        for(int i = 0; i<id_bits[target_bit].length()-1; i++){
            if(cond1){
                break;
            }
            char a = id_bits[target_bit].charAt(i);
            if(String.valueOf(a).matches("[5-9]")){
                cond2 = true;
                break;
            }
        }
        if((cond1 == cond2) && targets_ordered.size() < 3){
            return targets_ordered.get(2);
        }
        else if(cond1 || cond2 && targets_ordered.size() < 2){
            return targets_ordered.get(1);
        }else if(String.valueOf(id_bits[target_bit].charAt(2)).matches("[a-c]") && Character.isDigit(id_bits[target_bit].charAt(3)) && targets_ordered.size() < 3){
            return targets_ordered.get(3);
        }else if(String.valueOf(id_bits[target_bit].charAt(3)).matches("[l-m]") && Character.isDigit(id_bits[target_bit].charAt(2)) && targets_ordered.size() < 4){
            return targets_ordered.get(4);
        }
        else{
            return targets_ordered.get(0);
        }
    }

    //0,0015% of probabilty of gaining a legendary light? (well times 2)
    //
    public static Pair<InnerLightType, TargetType> determineTypeAndTarget(String[] id_bits, int type_bit, int target_bit){
        int i;
        boolean notfound = false;
        for(i = 0; i<id_bits[type_bit].length()-1; i++){
            if(Character.isLetter(id_bits[type_bit].charAt(i))){
                break;
            }else if(i == 4){
                i = 3;
                notfound = true;
                break;
            }
        }

        if(notfound){
            //TODO do stuff
        }

        //HEAL
        if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[a-b]")){
            return new Pair<>(InnerLightType.HEAL, determineTarget(id_bits, target_bit, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.OTHER)));
        }
        //DEFENCE
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[c-d]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.DEFENCE, determineTarget(id_bits, target_bit, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.OTHER)));
        //STRENGTH
        }else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[e-f]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.STRENGTH, determineTarget(id_bits, target_bit, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.OTHER)));
        }
        //Blazing
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[0-1]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.BLAZING, determineTarget(id_bits, target_bit, Arrays.asList(TargetType.ENEMIES, TargetType.ALL)));
        }
        //Frost
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[2-3]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.FROST, determineTarget(id_bits, target_bit, Arrays.asList(TargetType.ENEMIES, TargetType.ALLIES, TargetType.ALL, TargetType.SELF)));
        }
        //Earthen
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[4-5]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.EARTHEN, determineTarget(id_bits, target_bit, Arrays.asList(TargetType.ENEMIES, TargetType.SELF, TargetType.ALLIES, TargetType.OTHER)));
        }
        //TODO Wind
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[6-7]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.FROST, determineAttackTarget(id_bits, target_bit));
        }
        //TODO Aqua
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[8-9]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.BLAZING, determineAttackTarget(id_bits, target_bit));
        }
        LOGGER.info("[debug] nop not matched, UUID bit: " + id_bits[type_bit]);
        return new Pair<InnerLightType, TargetType>(InnerLightType.HEAL, TargetType.SELF);
    }

    //id bits 0
    //formula: 10+10*stufffoundintheid aka minimum value 10+10*1, so 20s
    //max: 10+10*9 = 100
    public static int determineCooldown(String[] id_bits, int string_bit){
        //checks the first char digit that finds and multiplies it
        for(int i = 0; i<id_bits[string_bit].length(); i++){
            if(Character.isDigit(id_bits[string_bit].charAt(i))){
                if(Character.getNumericValue(id_bits[string_bit].charAt(i)) == 0){
                    return 93;
                }else{
                    return 10+10*Character.getNumericValue(id_bits[string_bit].charAt(i));
                }
            }

        }
        return 80;
    }

    //id bit 2
    //max 18
    public static int determineDuration(String[] id_bits, int string_bit){
        for(int i = 0; i<id_bits[string_bit].length(); i++){
            if(Character.isDigit(id_bits[string_bit].charAt(i))){
                if(Character.getNumericValue(id_bits[string_bit].charAt(i)) == 0){
                    return 12;
                }else{
                    return (Character.getNumericValue(id_bits[string_bit].charAt(i))*2);
                }
            }
        }
        return 9;
    }


    //Gets the first 2 digits ir finds and sums them up, then divides by 3, so the max is 9+9/3, so 6
    public static double determinePower(String[] id_bits, int string_bit){
        int n1 = -1;
        int n2 = 0;
        for(int i = 0; i<id_bits[string_bit].length(); i++){
            if(Character.isDigit(id_bits[string_bit].charAt(i))){
                if(n1 == -1){
                    n1 = Character.getNumericValue(id_bits[string_bit].charAt(i));
                }else{
                    n2 = Character.getNumericValue(id_bits[string_bit].charAt(i));
                    break;
                }
            }
        }
        double a = n1+n2/3;
        if(a < 1){
            a = 1;
        }
        return a;
    }

}
