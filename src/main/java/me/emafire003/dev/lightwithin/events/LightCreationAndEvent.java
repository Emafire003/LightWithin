package me.emafire003.dev.lightwithin.events;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

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

    public static final int TYPE_BIT = 0;
    public static final int COOLDOWN_BIT = 1;
    public static final int DURATION_BIT = 2;
    public static final int POWER_BIT = 3;
    public static final int TARGET_BIT = 4;
    
    //TODO coming soon tm 
    // public static final int INCREMENT_BIT = 1; //But flipped

    public static void createUniqueLight(PlayerEntity player){
        LightComponent component = LIGHT_COMPONENT.get(player);
        String id = player.getUuidAsString().toLowerCase();

        //   0      1     2    3     4
        //d5396476-b2b7-4d3e-9ddf-56e67177c4c2
        //3eec9f18-1d0e-3f17-917c-6994e7d034d1
        // TYPE    CLDN  DUR PWR    TARGET

        if(Config.RESET_ON_JOIN){
            component.clear();
        }
        if(!component.getType().equals(InnerLightType.NONE) || component.getType() == null){
            return;
        }
        String[] id_bits = id.split("-");
        //Type bit & target bit
        //If the second part of the UUID starts with a letter form a to h && the second character is a digit -> Heal
        Pair<InnerLightType, TargetType> type_and_target = determineTypeAndTarget(id_bits, TYPE_BIT, TARGET_BIT);
        //type
        component.setType(type_and_target.getFirst());
        //Target
        component.setTargets(type_and_target.getSecond());

        //Cooldown Bit
        //The max cooldown is 100
        component.setMaxCooldown(determineCooldown(id_bits, COOLDOWN_BIT));

        //Duration bit
        component.setDuration(determineDuration(id_bits, DURATION_BIT));

        //Power bit
        component.setPowerMultiplier(determinePower(id_bits, POWER_BIT));

        component.setRainbow(true);
    }


    /**
     * Returns a TargetType based on a list of more likely targets.
     * The distribution is roughly as it follows:
     * 5 targets: 39% 21% 16% 14% 8%
     * 4 targets: 48% 21% 16% 14%
     * 3 targets: 48% 35.5% 16%
     * 2 targets: 64% 35.6%
     * And well, 100% for 1 target
     *
     * @param targets_ordered A list of targets. The first object on the list will be most likely and so on.*/
    public static TargetType determineTarget(String[] id_bits, int target_bit, List<TargetType> targets_ordered){
        if(target_bit == 2 || target_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[target_bit] = id_bits[target_bit].substring(1)+id_bits[target_bit-1].substring(id_bits[target_bit-1].length()-1);
        }
        String trg_str = id_bits[target_bit];
        char char1 = trg_str.charAt(0);
        char char2 = trg_str.charAt(1);
        //If the first two characters are a letter (a-f) returns the FOURTH most likely. And if there is a FOURTH target
        if(Character.isLetter(char1) && Character.isLetter(char2) && targets_ordered.size() > 3){
            return targets_ordered.get(3);
        }
        //If the first two characters summed have a value above, returns the SECOND most likely one. (if there is one)
        if(Character.getNumericValue(char1)+Character.getNumericValue(char2) > 17 && targets_ordered.size() > 1){
            return targets_ordered.get(1);
        }
        //If the first char is between 3-5, retrun the THIRD most likely target (if there is one)
        if(trg_str.matches("^[3-5].*") && targets_ordered.size() > 2){
            return targets_ordered.get(2);
        }
        //If the last char is between 1-3, retrunr the fifth most likely target (if there is one)
        if(trg_str.matches(".*[1-3]$") && targets_ordered.size() > 4){
            return targets_ordered.get(4);
        }

        //Returns the most likely if nothing has been found. Which is quite likely.
        return targets_ordered.get(0);
    }

    //0,0015% of probabilty of gaining a legendary light? (well times 2)
    public static Pair<InnerLightType, TargetType> determineTypeAndTarget(String[] id_bits, int type_bit, int target_bit){
        if(type_bit == 2 || type_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[target_bit] = id_bits[target_bit].substring(1)+id_bits[target_bit-1].substring(id_bits[target_bit-1].length()-1);
        }
        if(target_bit == 2 || target_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[target_bit] = id_bits[target_bit].substring(1)+id_bits[target_bit-1].substring(id_bits[target_bit-1].length()-1);
        }
        //The second character of the thing
        int i = 1;

        //All of these realease 1.0.0 have roughly a 12.5% chance of appearing. Except the frog that tends very much to 0.
        //HEAL
        if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[a-b]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.HEAL, determineTarget(id_bits, target_bit, LightWithin.POSSIBLE_TARGETS.get(InnerLightType.HEAL)));
        }
        //DEFENCE
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[c-d]")){
            return new Pair<>(InnerLightType.DEFENCE, determineTarget(id_bits, target_bit, LightWithin.POSSIBLE_TARGETS.get(InnerLightType.DEFENCE)));
            //STRENGTH
        }else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[e-f]")){
            return new Pair<>(InnerLightType.STRENGTH, determineTarget(id_bits, target_bit, LightWithin.POSSIBLE_TARGETS.get(InnerLightType.STRENGTH)));
        }
        //Blazing
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[0-1]")){
            return new Pair<>(InnerLightType.BLAZING, determineTarget(id_bits, target_bit, LightWithin.POSSIBLE_TARGETS.get(InnerLightType.BLAZING)));
        }
        //Frost
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[2-3]")){
            return new Pair<>(InnerLightType.FROST, determineTarget(id_bits, target_bit, LightWithin.POSSIBLE_TARGETS.get(InnerLightType.FROST)));
        }
        //Earthen
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[4-5]")){
            return new Pair<>(InnerLightType.EARTHEN, determineTarget(id_bits, target_bit, LightWithin.POSSIBLE_TARGETS.get(InnerLightType.EARTHEN)));
        }
        //Wind
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[6-7]")){
            return new Pair<>(InnerLightType.WIND, determineTarget(id_bits, target_bit, LightWithin.POSSIBLE_TARGETS.get(InnerLightType.WIND)));
        }
        //Aqua
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[8-9]")){
            return new Pair<>(InnerLightType.AQUA, determineTarget(id_bits, target_bit, LightWithin.POSSIBLE_TARGETS.get(InnerLightType.AQUA)));
        }
        //Frog? aka f = 6 r = 18 = F+2 o = 15 = E g = 7
        //If there is "frog" spelled as numbers of the alphabet, then your light is frog. Happy?
        else if(String.valueOf(id_bits[type_bit]).contains("6f2e7")){
            return new Pair<>(InnerLightType.FROG, determineTarget(id_bits, target_bit, List.of(TargetType.ALL)));
        }
        LOGGER.info("[debug] nop not matched, UUID bit: " + id_bits[type_bit]);
        return new Pair<>(InnerLightType.HEAL, TargetType.SELF);
    }

    //id bits 0
    //formula: 10+10*stufffoundintheid aka minimum value 10+10*value, so 10s-170s
    //max: 10+10*16 = 170
    public static int determineCooldown(String[] id_bits, int string_bit){
        //xxxxxxxx-xxxx-Axxx-Bxxx-xxxxxxxxxxxx where B is the variant, which sometimes does not change so
        if(string_bit == 2 || string_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[string_bit] = id_bits[string_bit].substring(1)+id_bits[string_bit-1].substring(id_bits[string_bit-1].length()-1);
        }
        return 10+10*Character.getNumericValue(id_bits[string_bit].charAt(0));
    }

    //id bit 2
    //max 18
    public static int determineDuration(String[] id_bits, int string_bit){
        //The UUID stores constat bits in these parts here, which are the version and the variant.
        if(string_bit == 2 || string_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[string_bit] = id_bits[string_bit].substring(1)+id_bits[string_bit-1].substring(id_bits[string_bit-1].length()-1);
        }
        int i = 0;
        if(Config.ADJUST_FOR_LOW_DURATION && Character.getNumericValue(id_bits[string_bit].charAt(i)) <= Config.ADJUST_DUR_THRESHOLD){
            return (int) ((Character.getNumericValue(id_bits[string_bit].charAt(i))*Config.ADJUST_DUR_AMOUNT*Config.DURATION_MULTIPLIER));
        }else if(Character.getNumericValue(id_bits[string_bit].charAt(i)) == 0){
            return (int) ((Config.ADJUST_DUR_AMOUNT*Config.DURATION_MULTIPLIER));
        }
        else{
            return (int) (Character.getNumericValue(id_bits[string_bit].charAt(i))*Config.DURATION_MULTIPLIER);
        }

    }


    /**If it finds a digit (0-9) that's going to be the power +1 (so 1-10),
     * If not, it will get the Numeric value (0-16) and divied by 2 so (10-16/2, aka 5-8)
     *
     * So having a power between 5/8 should be more common. I don't really know.*/
    public static int determinePower(String[] id_bits, int string_bit){
        //The UUID stores constat bits in these parts here, which are the version and the variant.
        if(string_bit == 2 || string_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[string_bit] = id_bits[string_bit].substring(1)+id_bits[string_bit-1].substring(id_bits[string_bit-1].length()-1);
        }

        for(int i = 0; i<id_bits[string_bit].length(); i++){
            if(Character.isDigit(id_bits[string_bit].charAt(i))){
                return Character.getNumericValue(id_bits[string_bit].charAt(i))+1;
            }else if(i == id_bits[string_bit].length()-1){
                return Character.getNumericValue(id_bits[string_bit].charAt(i))/2;
            }
        }
        return Character.getNumericValue(id_bits[string_bit].charAt(0))/2;
    }
}
