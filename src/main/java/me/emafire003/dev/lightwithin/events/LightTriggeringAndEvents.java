package me.emafire003.dev.lightwithin.events;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;

import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.*;
import static me.emafire003.dev.lightwithin.util.LightTriggerChecks.*;

public class LightTriggeringAndEvents {



    /**Checks if you can trigger the light or not
     * */
    public static boolean isTriggerable(PlayerEntity player){
        if(player.getWorld().isClient){
            return false;
        }
        if(!CheckUtils.canActivateHere((ServerPlayerEntity) player)){
            return false;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);
        if(component.getType().equals(InnerLightType.NONE)){
            return false;
        }
        if(component.getLocked()){
            return false;
        }
        if(LightWithin.isPlayerInCooldown(player)){
            return false;
        }
        return true;
    }


    /**
     * Triggers the lights that activate when an allied entity is attacked
     * */
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
        if(component.getType().equals(InnerLightType.AQUA)){
            checkAqua(player, component, attacker, target);
        }
    }

    /**Triggers the light that require the player to attack and entity
     * or the player to be attacked*/
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
        if(component.getType().equals(InnerLightType.FOREST_AURA)){
            player.sendMessage(Text.literal("Implement check for forest aura!"));
        }
        if(component.getType().equals(InnerLightType.FROG)){
            checkFrog(player, component, attacker, target);
        }
    }

    /**Triggers the lights that relay on falling aka Wind for example*/
    public static void entityFallingTriggerCheck(PlayerEntity player, LivingEntity falling_entity, float fallHeight){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);
        if(component.getType().equals(InnerLightType.WIND)){
            checkWind(player, component, player, falling_entity);
        }

    }

    /**Triggers that lighs on fire damage taken by the player*/
    public static void entityBlazingTriggerCheck(PlayerEntity player, LivingEntity target){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);
        if(component.getType().equals(InnerLightType.BLAZING)){
            player.sendMessage(Text.literal("Triggering because of blazing check"));
            checkBlazing(player, component, player, target);
        }

    }

    /**Triggers that lighs on Freezing damage taken by the player*/
    public static void entityFreezingTriggerCheck(PlayerEntity player, LivingEntity target){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);
        if(component.getType().equals(InnerLightType.FROST)){
            checkFrost(player, component, player, target);
        }

    }

    /**Triggers that lighs on Freezing damage taken by the player*/
    public static void entityDrowningTriggerCheck(PlayerEntity player, LivingEntity target){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);
        if(component.getType().equals(InnerLightType.AQUA)){
            checkAqua(player, component, player, target);
        }

    }

    public static void registerListeners(){
        LOGGER.debug("Registering events listeners...");

        //may need to re-add those return statemes on every if
        //Player (or other entity) being attacked by something else
        EntityAttackEntityEvent.EVENT.register(((attacker, target) -> {
            //Checks if someone is attacked and if they are the one getting attacked
            //If the target is the player with the light, he is also the target
            if(target instanceof PlayerEntity){
                entityAttackEntityTriggerCheck((PlayerEntity) target, attacker, target);
            }
            //if the target is a pet of someone with a light, the pet is the target. (He is also considered an ally)
            if(target instanceof TameableEntity){
                if(((TameableEntity) target).getOwner() instanceof PlayerEntity){
                    entityAttackEntityTriggerCheck((PlayerEntity) ((TameableEntity) target).getOwner(), attacker, target);
                }
            }

            //TODO see what's going on here
            //if the one getting attacked is a passive entity, the entity is the target
            //while the player who triggers the light is the one nearby
            if(target instanceof PassiveEntity){
                List<PlayerEntity> entities1 = target.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(target.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(target)){
                        entityAttackEntityTriggerCheck(p, attacker, target);
                    }
                }
            }

            //if someone/something gets attaccked and is an ally of a player nearby the target is the one getting attacked,
            //while the player who triggers the light is the one present nearby
            //if(target.getScoreboardTeam() != null){
            List<PlayerEntity> entities = target.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(target.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, target) && !p.equals(target)){
                    entityAttackEntityTriggerCheck(p, attacker, target);
                    entityAttackAllyEntityTriggerCheck(p, attacker, target);
                }
            }
            //}

        }));

        //Player (or other entity) falling
        //{Don't know why I put the returns there, stopping other possible things if for example the entity was a
        // player but wasn't a wind light wielder, but one of teammate around him was and could not trigger a light}
        EntityFallingEvent.EVENT.register(((entity, diff, fallDistance) -> {

            if(entity instanceof PlayerEntity){
                entityFallingTriggerCheck((PlayerEntity) entity, entity, fallDistance);
            }
            //if the target is a pet of someone with a light, the pet is the target. (He is also considered an ally)
            if(entity instanceof TameableEntity){
                if(((TameableEntity) entity).getOwner() instanceof PlayerEntity){
                    entityFallingTriggerCheck((PlayerEntity) ((TameableEntity) entity).getOwner(), entity, fallDistance);
                }
            }

            //if the one getting attacked is a passive entity, the entity is the target
            //while the player who triggers the light is the one nearby
            if(entity instanceof PassiveEntity){
                List<PlayerEntity> entities1 = entity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(entity.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(entity)){
                        entityFallingTriggerCheck(p, entity, fallDistance);
                    }
                }
            }
            //if someone is a teammate of a player that can trigger their light by falling, this will check for it
            List<PlayerEntity> entities = entity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(entity.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, entity) && !p.equals(entity)){
                    entityFallingTriggerCheck(p, entity, fallDistance);
                }
            }
        }));

        //Triggers when someone/thing is burning
        EntityBurningEvent.EVENT.register(((burningEntity) -> {

            if(burningEntity instanceof PlayerEntity){
                entityBlazingTriggerCheck((PlayerEntity) burningEntity, (PlayerEntity) burningEntity);
            }
            //if the target is a pet of someone with a light, the pet is the target. (He is also considered an ally)
            if(burningEntity instanceof TameableEntity){
                if(((TameableEntity) burningEntity).getOwner() instanceof PlayerEntity){
                    entityBlazingTriggerCheck((PlayerEntity) ((TameableEntity) burningEntity).getOwner(), (LivingEntity) burningEntity);
                }
            }

            //if the one getting attacked is a passive entity, the entity is the target
            //while the player who triggers the light is the one nearby
            if(burningEntity instanceof PassiveEntity){
                List<PlayerEntity> entities1 = burningEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(burningEntity.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(burningEntity)){
                        entityBlazingTriggerCheck(p, (LivingEntity) burningEntity);
                    }
                }
            }
            //if someone is a teammate of a player that can trigger their light by falling, this will check for it
            List<PlayerEntity> entities = burningEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(burningEntity.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, (LivingEntity) burningEntity) && !p.equals(burningEntity)){
                    entityBlazingTriggerCheck(p, (LivingEntity) burningEntity);
                }
            }
        }));

        //Triggers when someone/thing is freezing
        EntityFreezingEvent.EVENT.register(((freezingEntity) -> {
            if(freezingEntity instanceof PlayerEntity){
                entityFreezingTriggerCheck((PlayerEntity) freezingEntity, (PlayerEntity) freezingEntity);
            }
            //if the target is a pet of someone with a light, the pet is the target. (He is also considered an ally)
            if(freezingEntity instanceof TameableEntity){
                if(((TameableEntity) freezingEntity).getOwner() instanceof PlayerEntity){
                    entityFreezingTriggerCheck((PlayerEntity) ((TameableEntity) freezingEntity).getOwner(), (LivingEntity) freezingEntity);
                }
            }

            //if the one getting attacked is a passive entity, the entity is the target
            //while the player who triggers the light is the one nearby
            if(freezingEntity instanceof PassiveEntity){
                List<PlayerEntity> entities1 = freezingEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(freezingEntity.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(freezingEntity)){
                        entityFreezingTriggerCheck(p, (LivingEntity) freezingEntity);
                    }
                }
            }
            //if someone is a teammate of a player that can trigger their light by falling, this will check for it
            List<PlayerEntity> entities = freezingEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(freezingEntity.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, (LivingEntity) freezingEntity) && !p.equals(freezingEntity)){
                    entityFreezingTriggerCheck(p, (LivingEntity) freezingEntity);
                }
            }
        }));

        //Triggers when someone/thing is drowining
        EntityDrowningEvent.EVENT.register(((drowningEntity) -> {
            if(drowningEntity instanceof PlayerEntity){
                entityDrowningTriggerCheck((PlayerEntity) drowningEntity, (PlayerEntity) drowningEntity);
            }
            //if the target is a pet of someone with a light, the pet is the target. (He is also considered an ally)
            if(drowningEntity instanceof TameableEntity){
                if(((TameableEntity) drowningEntity).getOwner() instanceof PlayerEntity){
                    entityDrowningTriggerCheck((PlayerEntity) ((TameableEntity) drowningEntity).getOwner(), (LivingEntity) drowningEntity);
                }
            }

            //if the one getting attacked is a passive entity, the entity is the target
            //while the player who triggers the light is the one nearby
            if(drowningEntity instanceof PassiveEntity){
                List<PlayerEntity> entities1 = drowningEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(drowningEntity.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(drowningEntity)){
                        entityDrowningTriggerCheck(p, (LivingEntity) drowningEntity);
                    }
                }
            }
            //if someone is a teammate of a player that can trigger their light by falling, this will check for it
            List<PlayerEntity> entities = drowningEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(drowningEntity.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, (LivingEntity) drowningEntity) && !p.equals(drowningEntity)){
                    entityDrowningTriggerCheck(p, (LivingEntity) drowningEntity);
                }
            }
        }));

        //Player attacking something
        //Will need the stuff that is here to the other thingy up there
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
            if(component.getType().equals(InnerLightType.AQUA) && entity instanceof LivingEntity){
                checkAqua(player, component, player, (LivingEntity) entity);
            }
            return ActionResult.PASS;
        } );

        EntityDeathEvent.EVENT.register(((entity, source) -> {
            List<PlayerEntity> players = entity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(entity.getBlockPos()).expand(BOX_EXPANSION_AMOUNT), (entity1 -> true));

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
                    if(component.getType().equals(InnerLightType.AQUA)){
                        checkAqua(player, component, player, entity);
                    }
                    /**End*/
                }
            }

        }));
    }


}
