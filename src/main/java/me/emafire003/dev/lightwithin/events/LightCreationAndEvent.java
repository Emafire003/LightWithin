package me.emafire003.dev.lightwithin.events;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.lights.NoneLight;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

import java.util.List;
import java.util.UUID;

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
        //
        //Cooldown is also used to determine max light stack (the string not the value)

        if(Config.RESET_ON_JOIN){
            component.clear();
        }

        String[] id_bits = id.split("-");

        if(component.getVersion() == 1){
            //Adding the new thing
            //MaxLightStacks / MaxLightCharges however you want to call it bit
            component.setMaxLightStack(determineMaxLightCharges(id_bits, COOLDOWN_BIT));
            component.setLightCharges(0);
            component.setVersion(2);
        }if(component.getVersion() == 2){
            component.setVersion(3);
        }


        // This check is here to non-reset the light unless it became invalid (somehow)
        if(!(component.getType() instanceof NoneLight || component.getType() == null)){
            return;
            //Basically checks if it's not the first join for the player
        }else if(component.getDuration() != -1){
            LOGGER.warn("The light of " + player.getName().toString() + " has (somehow) become invalid (it now is: " + component.getType() + " ). Resetting it it!");
        }

        //Type bit & target bit
        //If the second part of the UUID starts with a letter form a to h && the second character is a digit -> Heal
        Pair<InnerLight, TargetType> type_and_target = determineTypeAndTarget(id_bits, TYPE_BIT, TARGET_BIT);
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

        //MaxLightStacks / MaxLightCharges however you want to call it bit
        component.setMaxLightStack(determineMaxLightCharges(id_bits, COOLDOWN_BIT));
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
    public static Pair<InnerLight, TargetType> determineTypeAndTarget(String[] id_bits, int type_bit, int target_bit){
        if(type_bit == 2 || type_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[target_bit] = id_bits[target_bit].substring(1)+id_bits[target_bit-1].substring(id_bits[target_bit-1].length()-1);
        }
        if(target_bit == 2 || target_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[target_bit] = id_bits[target_bit].substring(1)+id_bits[target_bit-1].substring(id_bits[target_bit-1].length()-1);
        }

        //Checks all of the lights for what they should have in the UUID in order to be selected
        /// See the {@link me.emafire003.dev.lightwithin.lights.InnerLightTypes} to see how they are determined
        for(InnerLight innerLight : LightWithin.INNERLIGHT_REGISTRY.stream().toList()){
            if(innerLight instanceof NoneLight){
                continue;
            }
            if(innerLight.getCreationRegex().isCompatible(id_bits[type_bit])){
                return new Pair<>(innerLight, determineTarget(id_bits, target_bit, innerLight.getPossibleTargetTypes()));
            }
        }

        LOGGER.error("[debug] The light type did not match any option of the UUID bits: " + id_bits[type_bit]);
        return new Pair<>(LightWithin.INNERLIGHT_REGISTRY.get(LightWithin.getIdentifier("heal")), TargetType.SELF);
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
     * <p>
     * So having a power between 5/8 should be more common. I don't really know.
     * */
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


    //As of 1.1.0 the chances are 12.5% for numbers 1-7, and 6.25 for 0 and 8
    /**Returns the max light stack number. It is determined as follows:
     * <p>
     * Checks the second element of the string bit,
     * gets the number value and adds 1 to it
     * then halves it
     *<p>
     * Uses the {@link #COOLDOWN_BIT} usually
     * */
    public static int determineMaxLightCharges(String[] id_bits, int string_bit){
        //xxxxxxxx-xxxx-Axxx-Bxxx-xxxxxxxxxxxx where B is the variant, which sometimes does not change so
        if(string_bit == 2 || string_bit == 3){
            //It also adds the last digit from the previous bit
            id_bits[string_bit] = id_bits[string_bit].substring(1)+id_bits[string_bit-1].substring(id_bits[string_bit-1].length()-1);
        }
        int max = (Character.getNumericValue(id_bits[string_bit].charAt(1))+1)/2;
        if(Config.ALLOW_MAX_CHARGE_0 && max == 0){
            max = 1;
        }
        if(Config.ALLOW_MAX_CHARGE_8 && max == 8){
            max = 7;
        }
        return max;
    }


    /** Changes the all of a light's parameters/attributes using a new UUID
     *
     * @param targetsComponent The {@link LightComponent} of the target entity, the one whose light is going to be changed to a new one
     * @param uuid The new UUID used to determine the new light parameters/attributes*/

    public static void mutateLightToUUID(LightComponent targetsComponent, UUID uuid){
        mutateLightToUUID(targetsComponent, uuid.toString().toLowerCase());
    }

    /** Changes the all of a light's parameters/attributes using a new UUID
     *
     * @param targetsComponent The {@link LightComponent} of the target entity, the one whose light is going to be changed to a new one
     * @param uuidString The string representation of the new UUID used to determine the new light parameters/attributes*/
    public static void mutateLightToUUID(LightComponent targetsComponent, String uuidString){
        String[] id_bits = uuidString.split("-");
        targetsComponent.setType(LightCreationAndEvent.determineTypeAndTarget(id_bits, LightCreationAndEvent.TYPE_BIT, LightCreationAndEvent.TARGET_BIT).getFirst());
        targetsComponent.setTargets(LightCreationAndEvent.determineTypeAndTarget(id_bits, LightCreationAndEvent.TYPE_BIT, LightCreationAndEvent.TARGET_BIT).getSecond());

        targetsComponent.setPowerMultiplier(LightCreationAndEvent.determinePower(id_bits, LightCreationAndEvent.POWER_BIT));
        targetsComponent.setDuration(LightCreationAndEvent.determineDuration(id_bits, LightCreationAndEvent.DURATION_BIT));
        targetsComponent.setMaxCooldown(LightCreationAndEvent.determineCooldown(id_bits, LightCreationAndEvent.COOLDOWN_BIT));
        targetsComponent.setMaxLightStack(LightCreationAndEvent.determineMaxLightCharges(id_bits, LightCreationAndEvent.COOLDOWN_BIT));
    }
}
