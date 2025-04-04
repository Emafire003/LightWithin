package me.emafire003.dev.lightwithin.events;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.BalanceConfig;
import me.emafire003.dev.lightwithin.lights.InnerLight;
import me.emafire003.dev.lightwithin.lights.NoneLight;
import me.emafire003.dev.lightwithin.lights.ThunderAuraLight;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import me.emafire003.dev.lightwithin.util.TriggerChecks;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;

import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.*;

//TODO maybe add a generic "damaged" event
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
        if(component.getType() instanceof NoneLight) {
            return false;
        }
        if(component.getLocked()){
            return false;
        }
        return !LightWithin.isPlayerInCooldown(player);
    }


    /**
     * Triggers the lights that activates when an allied entity is attacked
     * */
    public static void entityAttackAllyEntityTriggerCheck(PlayerEntity player, LivingEntity attacker, LivingEntity target){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);
        InnerLight light = component.getType();

        if(light.getTriggerChecks().contains(TriggerChecks.ALLY_ATTACKED)){
            light.triggerCheck(player, component, attacker, target);
        }
    }

    /** Triggers the lights that require the caster (or similar entity) to attack another entity
     *
     * Requires the {@link TriggerChecks#ENTITY_ATTACKS_ENTITY}*/
    public static void entityAttacksEntityTriggerCheck(PlayerEntity player, LivingEntity attacker, LivingEntity target){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);
        InnerLight light = component.getType();
        if(light.getTriggerChecks().contains(TriggerChecks.ENTITY_ATTACKS_ENTITY)){
            light.triggerCheck(player, component, attacker, target);
        }
    }

    /**Triggers the light that require the player to attack and entity
     * or the player to be attacked
     * <p>
     * The lights need to have {@link TriggerChecks#ENTITY_ATTACKED}
     * */
    public static void entityAttackedTriggerCheck(PlayerEntity player, LivingEntity attacker, LivingEntity target){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);

        InnerLight light = component.getType();
        if(light.getTriggerChecks().contains(TriggerChecks.ENTITY_ATTACKED)){
            light.triggerCheck(player, component, attacker, target);
        }
    }

    /**Triggers the lights that relay on falling aka Wind for example*/
    public static void entityFallingTriggerCheck(PlayerEntity player, LivingEntity falling_entity, float fallHeight){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);

        InnerLight light = component.getType();
        if(light.getTriggerChecks().contains(TriggerChecks.ENTITY_FALLING)){
            light.triggerCheck(player, component, null, falling_entity);
        }


    }

    /**Triggers that lighs on fire damage taken by the player*/
    public static void entityBlazingTriggerCheck(PlayerEntity player, LivingEntity target){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);
        INNERLIGHT_REGISTRY.forEach(light -> {
            if(light.getTriggerChecks().contains(TriggerChecks.ENTITY_BURNING)){
                light.triggerCheck(player, component, null, target);
            }
        });

    }

    /**Triggers that lighs on Freezing damage taken by the player*/
    public static void entityFreezingTriggerCheck(PlayerEntity player, LivingEntity target){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);
        InnerLight light = component.getType();
        if(light.getTriggerChecks().contains(TriggerChecks.ENTITY_FREEZING)){
            light.triggerCheck(player, component, null, target);
        }

    }

    /**Triggers the ligth on Drowning damage taken by the player*/
    public static void entityDrowningTriggerCheck(PlayerEntity player, LivingEntity target){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);
        InnerLight light = component.getType();

        if(light.getTriggerChecks().contains(TriggerChecks.ENTITY_DROWNING)){
            light.triggerCheck(player, component, null, target);
        }
    }

    /**Triggers that light on Lightning damage taken by the player*/
    public static void entityStruckByLightningTriggerCheck(PlayerEntity player, LivingEntity target){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);
        InnerLight light = component.getType();
        if(light.getTriggerChecks().contains(TriggerChecks.ENTITY_ATTACKED)){
            light.triggerCheck(player, component, null, target);
        }

    }

    /**Triggers that light when a piece of equipment breaks for the player */
    public static void entityEquipmentBrokenTriggerCheck(PlayerEntity player, ItemStack brokenItem){
        if(!isTriggerable(player)){
            return;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);

        player.sendMessage(Text.literal("The componenty target is: " + component.getTargets()));
        InnerLight light = component.getType();
        if(light.getTriggerChecks().contains(TriggerChecks.ARMOR_OR_TOOL_BREAKS)){
            light.triggerCheck(player, component, null, null);
        }
    }

    public static void allyDeadTriggerCheck(PlayerEntity player, LivingEntity target, DamageSource source){
        if(!isTriggerable(player)){
            return;
        }
        //needs to default to something sooo
        LivingEntity attacker;
        if(source.getSource() != null && source.getSource() instanceof LivingEntity){
            attacker = (LivingEntity) source.getSource();
        } else {
            attacker = null;
        }
        LightComponent component = LIGHT_COMPONENT.get(player);
        InnerLight light = component.getType();
        if(light.getTriggerChecks().contains(TriggerChecks.ALLY_DIES)){
            light.triggerCheck(player, component, attacker, target);
        }

    }

    private static void registerFallingListener(){
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
                List<PlayerEntity> entities1 = entity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(entity.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(entity)){
                        entityFallingTriggerCheck(p, entity, fallDistance);
                    }
                }
            }
            //if someone is a teammate of a player that can trigger their light by falling, this will check for it
            List<PlayerEntity> entities = entity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(entity.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, entity) && !p.equals(entity)){
                    entityFallingTriggerCheck(p, entity, fallDistance);
                }
            }
        }));
    }

    private static void registerBurningListener(){
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
                List<PlayerEntity> entities1 = burningEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(burningEntity.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(burningEntity)){
                        entityBlazingTriggerCheck(p, (LivingEntity) burningEntity);
                    }
                }
            }
            //if someone is a teammate of a player that can trigger their light by falling, this will check for it
            List<PlayerEntity> entities = burningEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(burningEntity.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, (LivingEntity) burningEntity) && !p.equals(burningEntity)){
                    entityBlazingTriggerCheck(p, (LivingEntity) burningEntity);
                }
            }
        }));
    }

    private static void registerFreezingListener(){
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
                List<PlayerEntity> entities1 = freezingEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(freezingEntity.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(freezingEntity)){
                        entityFreezingTriggerCheck(p, (LivingEntity) freezingEntity);
                    }
                }
            }
            //if someone is a teammate of a player that can trigger their light by falling, this will check for it
            List<PlayerEntity> entities = freezingEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(freezingEntity.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
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
                List<PlayerEntity> entities1 = drowningEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(drowningEntity.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(drowningEntity)){
                        entityDrowningTriggerCheck(p, (LivingEntity) drowningEntity);
                    }
                }
            }
            //if someone is a teammate of a player that can trigger their light by falling, this will check for it
            List<PlayerEntity> entities = drowningEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(drowningEntity.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, (LivingEntity) drowningEntity) && !p.equals(drowningEntity)){
                    entityDrowningTriggerCheck(p, (LivingEntity) drowningEntity);
                }
            }
        }));
    }

    private static void registerStruckByLightningListener(){
        //Triggers when someone/thing is struck by lightning
        EntityStruckByLightningEvent.EVENT.register(((boltedEntity) -> {
            if(boltedEntity instanceof PlayerEntity){
                entityStruckByLightningTriggerCheck((PlayerEntity) boltedEntity, (PlayerEntity) boltedEntity);
            }
            //if the target is a pet of someone with a light, the pet is the target. (He is also considered an ally)
            if(boltedEntity instanceof TameableEntity){
                if(((TameableEntity) boltedEntity).getOwner() instanceof PlayerEntity){
                    entityStruckByLightningTriggerCheck((PlayerEntity) ((TameableEntity) boltedEntity).getOwner(), (LivingEntity) boltedEntity);
                }
            }

            //TODO most likely remove this bit here, for AQUA light too
            //if the one getting attacked is a passive entity, the entity is the target
            //while the player who triggers the light is the one nearby
            if(boltedEntity instanceof PassiveEntity){
                List<PlayerEntity> entities1 = boltedEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(boltedEntity.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    //Note: the player can't be the passive entity so no need for the check
                    entityStruckByLightningTriggerCheck(p, (LivingEntity) boltedEntity);
                }
            }

            //if someone is a teammate of a player that can trigger their light by being lighting bolted in the face, this will check for it
            List<PlayerEntity> entities = boltedEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(boltedEntity.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, (LivingEntity) boltedEntity) && !p.equals(boltedEntity)){
                    entityStruckByLightningTriggerCheck(p, (LivingEntity) boltedEntity);
                }
            }

        }));
    }

    private static void registerEquipmentBreakListener(){
        //Triggers when someone/thing is struck by lightning
        ArmorOrToolBreakEvent.EVENT.register( (entity, brokenItem) -> {
            if(entity instanceof PlayerEntity){
                entityEquipmentBrokenTriggerCheck((PlayerEntity) entity, brokenItem);
            }
            //if the target is a pet of someone with a light, the pet is the target. (He is also considered an ally)
            if(entity instanceof TameableEntity){
                if(((TameableEntity) entity).getOwner() instanceof PlayerEntity){
                    entityEquipmentBrokenTriggerCheck((PlayerEntity) ((TameableEntity) entity).getOwner(), brokenItem);
                }
            }

            //TODO most likely remove this bit here, for AQUA light too
            //if the one getting attacked is a passive entity, the entity is the target
            //while the player who triggers the light is the one nearby
            if(entity instanceof PassiveEntity){
                List<PlayerEntity> entities1 = entity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(entity.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    //Note: the player can't be the passive entity so no need for the check
                    entityEquipmentBrokenTriggerCheck(p, brokenItem);
                }
            }

            //if someone is a teammate of a player that can trigger their light by being lighting bolted in the face, this will check for it
            List<PlayerEntity> entities = entity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(entity.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, entity) && !p.equals(entity)){
                    entityEquipmentBrokenTriggerCheck(p, brokenItem);
                }
            }
        });
    }

    public static void registerListeners(){
        LOGGER.debug("Registering events listeners...");

        //Elemental listeners
        registerFallingListener();
        registerBurningListener();
        registerFreezingListener();
        registerStruckByLightningListener();
        registerEquipmentBreakListener();

        //may need to re-add those return statemes on every if
        //Player (or other entity) being attacked by something else
        EntityAttackEntityEvent.EVENT.register(((attacker, target) -> {
            //Checks if someone is attacked and if they are the one getting attacked
            //If the target is the player with the light, he is also the target
            if(target instanceof PlayerEntity){
                entityAttackedTriggerCheck((PlayerEntity) target, attacker, target);
            }
            //if the target is a pet of someone with a light, the pet is the target. (He is also considered an ally)
            if(target instanceof TameableEntity){
                if(((TameableEntity) target).getOwner() instanceof PlayerEntity){
                    entityAttackedTriggerCheck((PlayerEntity) ((TameableEntity) target).getOwner(), attacker, target);
                }
            }

            //TODO see what's going on here - future me to future past me: please specify better
            //if the one getting attacked is a passive entity, the entity is the target
            //while the player who triggers the light is the one nearby
            if(target instanceof PassiveEntity){
                List<PlayerEntity> entities1 = target.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(target.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(target)){
                        entityAttackedTriggerCheck(p, attacker, target);
                    }
                }
            }

            //if someone/something gets attaccked and is an ally of a player nearby the target is the one getting attacked,
            //while the player who triggers the light is the one present nearby
            //if(target.getScoreboardTeam() != null){
            List<PlayerEntity> entities = target.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(target.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, target) && !p.equals(target)){
                    entityAttackedTriggerCheck(p, attacker, target);
                    entityAttackAllyEntityTriggerCheck(p, attacker, target);
                }
            }
            //}

        }));

        ///  Entity Attack Entity trigger check
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) ->{
            if(entity instanceof LivingEntity){
                entityAttacksEntityTriggerCheck(player, player, (LivingEntity) entity);
            }else{
                entityAttacksEntityTriggerCheck(player, player, null);
            }
            return ActionResult.PASS;
        } );

        EntityDeathEvent.EVENT.register(((entity, source) -> {
            List<PlayerEntity> players = entity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(entity.getBlockPos()).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));

            for(PlayerEntity player : players){
                if(CheckUtils.CheckAllies.checkAlly(entity, player)){
                    ///Start to check for potential lights from here
                    allyDeadTriggerCheck(player, entity, source);
                    ///End
                }
            }

        }));

        //Used for the ThunderAura's ALL effect
        registerThunderAuraAllEffect();
    }

    /**Used for the ThunderAura's ALL effect*/
    private static void registerThunderAuraAllEffect(){
        PlayerRightClickInteractEvent.EVENT.register((player) -> {
            if(player.getWorld().isClient()){
                return;
            }
            //Checks if the player has an active light, and if its Thunder aura with ALL as a target.
            // It will also check how many times they have already summoned a lightning bolt, and if it is above their power multiplier it won't allow to spawn new ones
            if(player.hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
                LightComponent component = LIGHT_COMPONENT.get(player);
                if(component.getType() instanceof ThunderAuraLight && component.getTargets().equals(TargetType.ALL)
                        && ThunderAuraLight.LIGHTNING_USES_LEFT.getOrDefault(player.getUuid(), 0) < component.getPowerMultiplier()*BalanceConfig.THUNDER_AURA_ALL_LIGHTNINGS_PER_LEVEL
                ){

                    HitResult result = player.raycast(40, 1.0f, true);
                    LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, player.getWorld());
                    lightning.setPosition(result.getPos());
                    lightning.setChanneler(player);
                    player.getWorld().spawnEntity(lightning);
                    //Adds another spent use/addes the first use
                    ThunderAuraLight.LIGHTNING_USES_LEFT.put(player.getUuid(), 1+ThunderAuraLight.LIGHTNING_USES_LEFT.getOrDefault(player.getUuid(), 0));

                }

            }
        });
    }


}
