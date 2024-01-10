package me.emafire003.dev.lightwithin.util;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.events.PlayerJoinEvent;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

import java.util.Arrays;
import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;
import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class LightCreationAndEvent {

    /**Registers the event of the light creation upon a player joining the world/server*/
    public static void registerCreationListener(){
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
        if(target_bit == 2 || target_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[target_bit] = id_bits[target_bit].substring(1)+id_bits[target_bit-1].substring(id_bits[target_bit-1].length()-1);
        }
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
        if(target_bit == 2 || target_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[target_bit] = id_bits[target_bit].substring(1)+id_bits[target_bit-1].substring(id_bits[target_bit-1].length()-1);
        }
        //If it's all letters or numbers, or if there is at least one number from 5-9 or e/f then all
        //NOPE --if the char at the position 2 is abc && the nextone is a digit then it's other-- NOPE
        //in the other cases it's enemies
        boolean cond1 = id_bits[target_bit].matches("[0-7]+") || id_bits[target_bit].matches("[d-f]+");
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
        }else if(String.valueOf(id_bits[target_bit].charAt(3)).matches("[e-f]") && Character.isDigit(id_bits[target_bit].charAt(2)) && targets_ordered.size() < 4){
            return targets_ordered.get(4);
        }
        else{
            return targets_ordered.get(0);
        }
    }

    //0,0015% of probabilty of gaining a legendary light? (well times 2)
    //
    public static Pair<InnerLightType, TargetType> determineTypeAndTarget(String[] id_bits, int type_bit, int target_bit){
        if(type_bit == 2 || type_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[target_bit] = id_bits[target_bit].substring(1)+id_bits[target_bit-1].substring(id_bits[target_bit-1].length()-1);
        }
        if(target_bit == 2 || target_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[target_bit] = id_bits[target_bit].substring(1)+id_bits[target_bit-1].substring(id_bits[target_bit-1].length()-1);
        }
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

        //TODO most likely for the other light i will need to
        //10.01.2024 I don't have any idea what comes after the "i will need to"
        //HEAL
        if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[a-b]")){
            return new Pair<>(InnerLightType.HEAL, determineTarget(id_bits, target_bit, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.VARIANT)));
        }
        //DEFENCE
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[c-d]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.DEFENCE, determineTarget(id_bits, target_bit, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.VARIANT)));
            //STRENGTH
        }else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[e-f]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.STRENGTH, determineTarget(id_bits, target_bit, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.VARIANT)));
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
            return new Pair<InnerLightType, TargetType>(InnerLightType.EARTHEN, determineTarget(id_bits, target_bit, Arrays.asList(TargetType.ENEMIES, TargetType.SELF, TargetType.ALLIES, TargetType.VARIANT)));
        }
        //Wind
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[6-7]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.WIND, determineTarget(id_bits, target_bit, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.VARIANT)));
        }
        //Aqua
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[8-9]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.AQUA, determineAttackTarget(id_bits, target_bit));
        }
        //Frog? aka f = 6 r = 18 = F+2 o = 15 = E g = 7
        else if(String.valueOf(id_bits[type_bit]).matches("6fe7")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.FROG, determineTarget(id_bits, target_bit, List.of(TargetType.ALL)));
        }
        LOGGER.info("[debug] nop not matched, UUID bit: " + id_bits[type_bit]);
        return new Pair<InnerLightType, TargetType>(InnerLightType.HEAL, TargetType.SELF);
    }

    //id bits 0
    //formula: 10+10*stufffoundintheid aka minimum value 10+10*1, so 20s
    //max: 10+10*9 = 100
    public static int determineCooldown(String[] id_bits, int string_bit){
        if(string_bit == 2 || string_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[string_bit] = id_bits[string_bit].substring(1)+id_bits[string_bit-1].substring(id_bits[string_bit-1].length()-1);
        }
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
        //The UUID stores constat bits in these parts here, which are the version and the variant.
        if(string_bit == 2 || string_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[string_bit] = id_bits[string_bit].substring(1)+id_bits[string_bit-1].substring(id_bits[string_bit-1].length()-1);
        }
        for(int i = 0; i<id_bits[string_bit].length(); i++){
            //if(Character.isDigit(id_bits[string_bit].charAt(i))){
            if(Config.ADJUST_FOR_LOW_DURATION && Character.getNumericValue(id_bits[string_bit].charAt(i)) <= Config.ADJUST_DUR_THRESHOLD){
                return (int) ((Character.getNumericValue(id_bits[string_bit].charAt(i))*Config.ADJUST_DUR_AMOUNT*Config.DURATION_MULTIPLIER));
            }else if(Character.getNumericValue(id_bits[string_bit].charAt(i)) == 0){
                return (int) ((Config.ADJUST_DUR_AMOUNT*Config.DURATION_MULTIPLIER));
            }
            else{
                return (int) (Character.getNumericValue(id_bits[string_bit].charAt(i))*Config.DURATION_MULTIPLIER);

            }
            //}
        }
        return (int) ((Config.ADJUST_DUR_AMOUNT*Config.DURATION_MULTIPLIER));

    }


    //Gets the first 2 digits ir finds and sums them up, then divides by 3, so the max is 9+9/3, so 6
    public static double determinePower(String[] id_bits, int string_bit){
        //The UUID stores constat bits in these parts here, which are the version and the variant.
        if(string_bit == 2 || string_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[string_bit] = id_bits[string_bit].substring(1)+id_bits[string_bit-1].substring(id_bits[string_bit-1].length()-1);
        }
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
