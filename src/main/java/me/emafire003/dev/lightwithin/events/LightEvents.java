package me.emafire003.dev.lightwithin.events;

import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.lights.HealLight;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.lights.InnerLightTypes;
import me.emafire003.dev.lightwithin.util.CacheSystem;
import me.emafire003.dev.lightwithin.util.TargetTypes;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;
import static me.emafire003.dev.lightwithin.LightWithin.LIGHT_COMPONENT;

public class LightEvents {

    public static void registerListeners(){
        LOGGER.info("Registering events listeners");
        //From nbt gets type, then gets the variables need for the type. Aka
        //if type == Heal
        //get cool down, get thing ecc
        //Also, set nbt boolean "LightReady" that will also show up in thw HUD

        //this works
        //TODO lights could be levelled up maybe
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->{
            LightComponent component = LIGHT_COMPONENT.get(player);
            if(component.getType().equals(InnerLightTypes.NONE)){
                return ActionResult.PASS;
            }

            //=======================HEAL LIGHT=======================
            if(component.getType().equals(InnerLightTypes.HEAL)){
                List<LivingEntity> targets = new ArrayList<>();
                //TODO add config option for setting the amout before it triggers
                if(component.getTargets().equals(TargetTypes.SELF) && player.getHealth() <= (player.getMaxHealth())*25/100){
                    targets.add(player);
                    CacheSystem.healLightSelf.add(player.getUuid());
                }else if(component.getTargets().equals(TargetTypes.ALLIES)){
                    //TODO set dimensions configable
                    LOGGER.info("HEALING ALLIES");
                    Box box = new Box(player.getBlockPos());
                    box = box.expand(6);
                    List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, box, (entity1 -> true));
                    for(LivingEntity ent : entities){
                        //TODO integration with other mods that implement allies stuff
                        if(ent.getScoreboardTeam() != null && ent.getScoreboardTeam().isEqual(player.getScoreboardTeam()) && ent.getHealth() <= (ent.getMaxHealth())*50/100){
                            targets.add(ent);
                        }
                    }
                    LOGGER.info("targets: " + targets);
                    LOGGER.info("entities: " + entities);
                }else if(component.getTargets().equals(TargetTypes.OTHER)){
                    Box box = new Box(player.getBlockPos());
                    box = box.expand(6);
                    if(player.getHealth() <= (player.getMaxHealth())*50/100){
                        targets.add(player);
                    }
                    List<PassiveEntity> entities = world.getEntitiesByClass(PassiveEntity.class, box, (entity1 -> true));
                    for(PassiveEntity ent : entities){
                        if(ent.getHealth() <= (ent.getMaxHealth())*50/100){
                            targets.addAll(entities);
                            break;
                        }
                    }
                }
                InnerLight light = new HealLight(targets, component.getCooldown(), component.getPowerMultiplier(), component.getDuration(), player);
                light.execute();
            }

            return ActionResult.PASS;
        } );

        PlayerJoinEvent.EVENT.register((player, server) -> {
            LightComponent component = LIGHT_COMPONENT.get(player);
            String id = player.getUuidAsString();
            LOGGER.info("Player's uuid:" + id);
            //3eec9f18-1d0e-3f17-917c-6994e7d034d1
            //TODO remove
            component.clear();
            if(component.getType().equals(InnerLightTypes.NONE) || component.getType() == null){
                component.setType(InnerLightTypes.HEAL);
                component.setCooldown(10);
                component.setDuration(0);
                component.setTargets(TargetTypes.ALLIES);
                component.setPowerMultiplier(1.5);
                component.setRainbow(true);
            }
            return ActionResult.PASS;
        });
    }

}
