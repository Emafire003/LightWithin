package me.emafire003.dev.lightwithin.events;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.networking.LightReadyPacketS2C;
import me.emafire003.dev.lightwithin.util.CacheSystem;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;

import java.util.*;

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

            //=======================HEAL LIGHT=======================
            /*
            sends the ready packet only if the player has less than 75% health and is heal.self
            if all of the allies have less tha 50% and heal.allies
            if at least one passive mob has 50% or less health and heal.other
             */
            if(component.getType().equals(InnerLightType.HEAL)){
                if(component.getTargets().equals(TargetType.SELF) && player.getHealth() <= (player.getMaxHealth())*25/100){
                    CacheSystem.healLightSelf.add(player.getUuid());
                    sendReadyPacket((ServerPlayerEntity) player, true);
                }else if(component.getTargets().equals(TargetType.ALLIES)){
                    //TODO set dimensions configable
                    Box box = new Box(player.getBlockPos());
                    box = box.expand(6);
                    List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, box, (entity1 -> true));
                    int ent_number = 0;
                    for(LivingEntity ent : entities){
                        //TODO integration with other mods that implement allies stuff
                        if(!entity.equals(ent) && ent.getScoreboardTeam() != null && ent.getScoreboardTeam().isEqual(player.getScoreboardTeam()) ){
                            if(ent.getHealth() <= (ent.getMaxHealth())*50/100){
                                ent_number++;
                            }
                        }else{
                            entities.remove(ent);
                        }
                    }
                    if(entities.size() == ent_number){
                        sendReadyPacket((ServerPlayerEntity) player, true);
                    }
                }else if(component.getTargets().equals(TargetType.OTHER)){
                    Box box = new Box(player.getBlockPos());
                    box = box.expand(6);
                    List<PassiveEntity> entities = world.getEntitiesByClass(PassiveEntity.class, box, (entity1 -> true));
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
            component.setType(type_and_target.getFirst());
            //Target
            component.setTargets(type_and_target.getSecond());

            //this saves the player data onto the cache so it will be easier to get it later
            cache.put(player.getUuid(), type_and_target);

            LOGGER.info("Welp, type and target and UUID: " + cache);

            //Cooldown Bit
            //The max cooldown is 100
            component.setCooldown(determineCooldown(id_bits, 0));

            //Duration bit
            component.setDuration(determineDuration(id_bits, 2));

            //Power bit
            component.setPowerMultiplier(determinePower(id_bits, 4));

            component.setRainbow(true);
            return ActionResult.PASS;
        });
    }

    //id bit 1
    public static Pair<InnerLightType, TargetType> determineTypeAndTarget(String[] id_bits, int type_bit, int target_bit){
        if(String.valueOf(id_bits[type_bit].charAt(0)).matches("[a-h]") && Character.isDigit(id_bits[type_bit].charAt(1))){
            //component.setType(InnerLightType.HEAL);
            if(String.valueOf(id_bits[target_bit].charAt(2)).matches("[f-s]") && Character.isDigit(id_bits[type_bit].charAt(3))){
                return new Pair<InnerLightType, TargetType>(InnerLightType.HEAL, TargetType.SELF);
            }else if(String.valueOf(id_bits[target_bit].charAt(2)).matches("[n-p]") && Character.isLetter(id_bits[type_bit].charAt(3))){
                return new Pair<InnerLightType, TargetType>(InnerLightType.HEAL, TargetType.ALLIES);
            }else if(String.valueOf(id_bits[target_bit].charAt(2)).matches("[3-6]") && String.valueOf(id_bits[target_bit].charAt(2)).matches("[n-p]") && Character.isLetter(id_bits[type_bit].charAt(3))){
                return new Pair<InnerLightType, TargetType>(InnerLightType.HEAL, TargetType.OTHER);
            }
        }
        return new Pair<InnerLightType, TargetType>(InnerLightType.HEAL, TargetType.SELF);
    }

    //TODO set a cooldown multiplier option in the condif
    //id bits 0
    public static int determineCooldown(String[] id_bits, int string_bit){
        for(int i = 0; i<id_bits[string_bit].length(); i++){
            if(Character.isDigit(id_bits[string_bit].charAt(i))){
                if(Character.getNumericValue(id_bits[string_bit].charAt(i)) == 0){
                    return 93;
                }else{
                    return 10*Character.getNumericValue(id_bits[string_bit].charAt(i));
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
        int n2 = -1;
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
        return n1+n2/4;
    }

}
