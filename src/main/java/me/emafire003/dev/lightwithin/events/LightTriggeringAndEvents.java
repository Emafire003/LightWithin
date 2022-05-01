package me.emafire003.dev.lightwithin.events;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.networking.LightReadyPacketS2C;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.emafire003.dev.lightwithin.LightWithin.*;

public class LightTriggeringAndEvents {

    public static HashMap<UUID, Pair<InnerLightType, TargetType>> cache = new HashMap<UUID, Pair<InnerLightType, TargetType>>();

    public static void sendReadyPacket(ServerPlayerEntity player, boolean b){
        try{
            ServerPlayNetworking.send(player, LightReadyPacketS2C.ID, new LightReadyPacketS2C(b));
        }catch(Exception e){
            LOGGER.error("FAILED to send data packets to the client!");
            e.printStackTrace();
            return;
        }
    }

    public static void registerListeners(){
        LOGGER.info("Registering events listeners");
        //From nbt gets type, then gets the variables need for the type. Aka
        //if type == Heal
        //get cool down, get thing ecc
        //Also, set nbt boolean "LightReady" that will also show up in thw HUD

        //this works
        //TODO lights could be levelled up maybe
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->{
            if(world.isClient){
                return ActionResult.PASS;
            }
            LightComponent component = LIGHT_COMPONENT.get(player);
            if(component.getType().equals(InnerLightType.NONE)){
                return ActionResult.PASS;
            }
            if(player.hasStatusEffect(LightEffects.LIGHT_FATIGUE)){
                return ActionResult.PASS;
            }

            //=======================HEAL LIGHT=======================
            /*
            sends the ready packet only if the player has less than 75% health and is heal.self
            if all of the allies have less tha 50% and heal.allies
            if at least one passive mob has 50% or less health and heal.other
             */
            if(component.getType().equals(InnerLightType.HEAL)){
                if(component.getTargets().equals(TargetType.SELF) && player.getHealth() <= (player.getMaxHealth())*25/100){
                    sendReadyPacket((ServerPlayerEntity) player, true);
                }else if(component.getTargets().equals(TargetType.ALLIES)){
                    //TODO set dimensions configable
                    List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amout), (entity1 -> true));
                    int ent_number = 0;
                    //I need to this to prevent a ConcurrentModificationError
                    List<LivingEntity> team_entities = new ArrayList<>();
                    //loops through the entities near the player, if the entities are in the same team as the player
                    //and they are not the entity that has been hit then add them to the team_entities and check if their health is ok
                    for(LivingEntity ent : entities){
                        //TODO integration with other mods that implement allies stuff
                        if(!entity.equals(ent) && ent.getScoreboardTeam() != null && ent.getScoreboardTeam().isEqual(player.getScoreboardTeam()) ){
                            if(ent.getHealth() <= (ent.getMaxHealth())*50/100){
                                ent_number++;
                            }
                            team_entities.add(ent);
                        }
                    }
                    //If the total team targets && the number of entities of team with the right health are true then
                    //send the ready packet
                    if(team_entities.size() == ent_number){
                        sendReadyPacket((ServerPlayerEntity) player, true);
                    }
                }else if(component.getTargets().equals(TargetType.OTHER)){
                    List<PassiveEntity> entities = world.getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amout), (entity1 -> true));
                    for(PassiveEntity ent : entities){
                        if(ent.getHealth() <= (ent.getMaxHealth())*50/100){
                            sendReadyPacket((ServerPlayerEntity) player, true);
                            break;
                        }
                    }
                }
            }
            if(component.getType().equals(InnerLightType.DEFENCE)){
                if(component.getTargets().equals(TargetType.SELF) && player.getHealth() <= (player.getMaxHealth())*25/100){
                    //CacheSystem.healLightSelf.add(player.getUuid());
                    sendReadyPacket((ServerPlayerEntity) player, true);
                }else if(component.getTargets().equals(TargetType.ALLIES)){
                    //TODO set dimensions configable
                    List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amout), (entity1 -> true));
                    int ent_number = 0;
                    //I need to this to prevent a ConcurrentModificationError
                    List<LivingEntity> team_entities = new ArrayList<>();
                    //loops through the entities near the player, if the entities are in the same team as the player
                    //and they are not the entity that has been hit then add them to the team_entities and check if their health is ok
                    for(LivingEntity ent : entities){
                        //TODO integration with other mods that implement allies stuff
                        if(!entity.equals(ent) && ent.getScoreboardTeam() != null && ent.getScoreboardTeam().isEqual(player.getScoreboardTeam()) ){
                            if(ent.getHealth() <= (ent.getMaxHealth())*50/100){
                                ent_number++;
                            }
                            team_entities.add(ent);
                        }
                    }
                    //If the total team targets && the number of entities of team with the right health are true then
                    //send the ready packet
                    if(team_entities.size() == ent_number){
                        sendReadyPacket((ServerPlayerEntity) player, true);
                    }
                }else if(component.getTargets().equals(TargetType.OTHER)){
                    List<PassiveEntity> entities = world.getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amout), (entity1 -> true));
                    for(PassiveEntity ent : entities){
                        if(ent.getHealth() <= (ent.getMaxHealth())*50/100){
                            sendReadyPacket((ServerPlayerEntity) player, true);
                            break;
                        }
                    }
                }
            }
            if(component.getType().equals(InnerLightType.STRENGTH)){
                if(component.getTargets().equals(TargetType.SELF) && player.getHealth() <= (player.getMaxHealth())*25/100){
                    //CacheSystem.healLightSelf.add(player.getUuid());
                    sendReadyPacket((ServerPlayerEntity) player, true);
                }else if(component.getTargets().equals(TargetType.ALLIES)){
                    //TODO set dimensions configable
                    List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amout), (entity1 -> true));
                    int ent_number = 0;
                    //I need to this to prevent a ConcurrentModificationError
                    List<LivingEntity> team_entities = new ArrayList<>();
                    //loops through the entities near the player, if the entities are in the same team as the player
                    //and they are not the entity that has been hit then add them to the team_entities and check if their health is ok
                    for(LivingEntity ent : entities){
                        //TODO integration with other mods that implement allies stuff
                        if(!entity.equals(ent) && ent.getScoreboardTeam() != null && ent.getScoreboardTeam().isEqual(player.getScoreboardTeam()) ){
                            if(ent.getHealth() <= (ent.getMaxHealth())*50/100){
                                ent_number++;
                            }
                            team_entities.add(ent);
                        }
                    }
                    //If the total team targets && the number of entities of team with the right health are true then
                    //send the ready packet
                    if(team_entities.size() == ent_number){
                        sendReadyPacket((ServerPlayerEntity) player, true);
                    }
                }else if(component.getTargets().equals(TargetType.OTHER)){
                    List<PassiveEntity> entities = world.getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amout), (entity1 -> true));
                    for(PassiveEntity ent : entities){
                        if(ent.getHealth() <= (ent.getMaxHealth())*50/100){
                            sendReadyPacket((ServerPlayerEntity) player, true);
                            break;
                        }
                    }
                }
            }
            return ActionResult.PASS;
        } );

        PlayerJoinEvent.EVENT.register((player, server) -> {
            if(player.getWorld().isClient){
                return ActionResult.PASS;
            }
            //if it's already in the cache noworries NEVERMIND MINECRAFT SHOULD AUTOSAVE THESE
            /*if(CacheSystem.player_components.containsKey(player.getUuid())){
                return ActionResult.PASS;
            }*/
            LightComponent component = LIGHT_COMPONENT.get(player);
            String id = player.getUuidAsString().toLowerCase();
            //3eec9f18-1d0e-3f17-917c-6994e7d034d1
            //TODO remove
            component.clear();
            if(!component.getType().equals(InnerLightType.NONE) || component.getType() == null){
                return ActionResult.PASS;
            }
            String[] id_bits = id.split("-");

            //Type bit & target bit
            //If the second part of the UUID starts with a letter form a to h && the second character is a digit -> Heal
            Pair<InnerLightType, TargetType> type_and_target = determineTypeAndTarget(id_bits, 1, 3);
            //type
            //component.setType(type_and_target.getFirst());
            component.setType(InnerLightType.DEFENCE);
            //Target
            component.setTargets(type_and_target.getSecond());

            //this saves the player data onto the cache so it will be easier to get it later
            cache.put(player.getUuid(), type_and_target);

            LOGGER.info("Welp, type and target and UUID: " + cache);

            //Cooldown Bit
            //The max cooldown is 100
            component.setMaxCooldown(determineCooldown(id_bits, 0));

            //Duration bit
            component.setDuration(determineDuration(id_bits, 2));

            //Power bit
            component.setPowerMultiplier(determinePower(id_bits, 4));

            component.setRainbow(true);
            return ActionResult.PASS;
        });
    }

    //id bit 1
    @Deprecated
    public static Pair<InnerLightType, TargetType> determineTypeAndTargetOLD(String[] id_bits, int type_bit, int target_bit){
        if((String.valueOf(id_bits[type_bit].charAt(0)).matches("[a-h]") && Character.isDigit(id_bits[type_bit].charAt(1))
        || (String.valueOf(id_bits[type_bit].charAt(1)).matches("[a-h]") && Character.isDigit(id_bits[type_bit].charAt(2))))){
            //component.setType(InnerLightType.HEAL);
            if(String.valueOf(id_bits[target_bit].charAt(2)).matches("[f-s]") && Character.isDigit(id_bits[type_bit].charAt(3))){
                return new Pair<InnerLightType, TargetType>(InnerLightType.HEAL, TargetType.SELF);
            }else if(String.valueOf(id_bits[target_bit].charAt(2)).matches("[n-p]") && Character.isLetter(id_bits[type_bit].charAt(3))){
                return new Pair<InnerLightType, TargetType>(InnerLightType.HEAL, TargetType.ALLIES);
            }else if(String.valueOf(id_bits[target_bit].charAt(2)).matches("[3-6]") && String.valueOf(id_bits[target_bit].charAt(2)).matches("[n-p]") && Character.isLetter(id_bits[type_bit].charAt(3))){
                return new Pair<InnerLightType, TargetType>(InnerLightType.HEAL, TargetType.OTHER);
            }else{
                LOGGER.info("Forced self, id+" + id_bits[target_bit]);
                return new Pair<InnerLightType, TargetType>(InnerLightType.HEAL, TargetType.SELF);
            }
        }
        LOGGER.info("nop not matched, UUID bit: " + id_bits[type_bit]);
        return new Pair<InnerLightType, TargetType>(InnerLightType.HEAL, TargetType.SELF);
    }

    public static TargetType determineBuffTarget(String[] id_bits, int target_bit){
        //If it's all letters or numbers, or if there is at least one number from 5-9 or e/f then allies
        //if the char at the position 2 is abc && the nextone is a digit then it's other
        //in the other cases it's self
        boolean allies_cond1 = id_bits[target_bit].matches("[0-9]+") || id_bits[target_bit].matches("[a-f]+");
        boolean allies_cond2 = false;
        LOGGER.info("Ally cond1 and 2: " + allies_cond1 + " " + allies_cond2);
        for(int i = 0; i<id_bits[target_bit].length()-1; i++){
            if(allies_cond1){
                break;
            }
            char a = id_bits[target_bit].charAt(i);
            if(String.valueOf(a).matches("[5-9]")){
                allies_cond2 = true;
                break;
            }
        }
        if(allies_cond1 || allies_cond2){
            return TargetType.ALLIES;
        }else if(String.valueOf(id_bits[target_bit].charAt(2)).matches("[a-c]") && Character.isDigit(id_bits[target_bit].charAt(3))){
            return TargetType.OTHER;
        }else{
            return TargetType.SELF;
        }
    }

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
            return new Pair<>(InnerLightType.HEAL, determineBuffTarget(id_bits, target_bit));
        }
        //DEFENCE
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[c-d]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.DEFENCE, determineBuffTarget(id_bits, target_bit));
        //STRENGTH
        }else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[e-f]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.STRENGTH, determineBuffTarget(id_bits, target_bit));
        }
        LOGGER.info("nop not matched, UUID bit: " + id_bits[type_bit]);
        return new Pair<InnerLightType, TargetType>(InnerLightType.HEAL, TargetType.SELF);
    }

    //TODO set a cooldown multiplier option in the condif
    //id bits 0
    //formula: 10+10*stufffoundintheid aka minimum value 10+10*1, so 20s
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
        return 90;
    }

    //TODO set a cooldown multiplier option in the condif
    //id bit 2
    public static int determineDuration(String[] id_bits, int string_bit){
        for(int i = 0; i<id_bits[string_bit].length(); i++){
            if(Character.isDigit(id_bits[string_bit].charAt(i))){
                if(Character.getNumericValue(id_bits[string_bit].charAt(i)) == 0){
                    return 3;
                }else{
                    return Character.getNumericValue(id_bits[string_bit].charAt(i));
                }
            }
        }
        return 5;
    }


    //Gets the first 2 digits ir finds and sums them up, then divides by ten, so the max is 9+9/4, so 4,5
    public static double determinePower(String[] id_bits, int string_bit){
        int n1 = -1;
        int n2 = 0;
        double power;
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
        power = n1+n2;
        //TODO maybe i should allow them to be 0,5 or similar
        if(power <= 1){
            power = 1;
        }
        double a = n1+n2/4;
        if(a > 4.5){
            a = 4.5;
        }
        return a;
    }

}
