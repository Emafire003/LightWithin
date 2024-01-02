package me.emafire003.dev.lightwithin.events;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.lightwithin.component.LightComponent;
import me.emafire003.dev.lightwithin.config.Config;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.CheckUtils;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;

import java.util.*;

import static me.emafire003.dev.lightwithin.LightWithin.*;
import static me.emafire003.dev.lightwithin.events.LightTriggerChecks.*;

public class LightTriggeringAndEvents {



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
        LOGGER.info("Registering events listeners...");

        //TODO may need to re-add those return statemes on every if
        //Player (or other entity) being attacked by something else
        EntityAttackEntityEvent.EVENT.register(((attacker, target) -> {
            //Checks if someone is attacked and if they are the one getting attacked
            //If the target is the player with the light, he is also the target
            if(target instanceof PlayerEntity){
                entityAttackEntityTriggerCheck((PlayerEntity) target, attacker, (PlayerEntity) target);
            }
            //if the target is a pet of someone with a light, the pet is the target. (He is also considered an ally)
            if(target instanceof TameableEntity){
                if(((TameableEntity) target).getOwner() instanceof PlayerEntity){
                    entityAttackEntityTriggerCheck((PlayerEntity) ((TameableEntity) target).getOwner(), attacker, target);
                }
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

        //Player (or other entity) falling
        //{Don't know why I put the returns there, stopping other possible things if for example the entity was a
        // player but wasn't a wind light wielder, but one of teammate around him was and could not trigger a light}
        EntityFallingEvent.EVENT.register(((entity, diff, fallDistance) -> {

            //TODO see if this works
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
                List<PlayerEntity> entities1 = entity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(entity.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(entity)){
                        entityFallingTriggerCheck(p, entity, fallDistance);
                    }
                }
            }
            //if someone is a teammate of a player that can trigger their light by falling, this will check for it
            List<PlayerEntity> entities = entity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(entity.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, entity) && !p.equals(entity)){
                    entityFallingTriggerCheck(p, entity, fallDistance);
                }
            }
        }));

        //Triggers when someone/thing is burning
        EntityBurningEvent.EVENT.register(((burningEntity) -> {
            //TODO see if this works
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
                List<PlayerEntity> entities1 = burningEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(burningEntity.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(burningEntity)){
                        entityBlazingTriggerCheck(p, (LivingEntity) burningEntity);
                    }
                }
            }
            //if someone is a teammate of a player that can trigger their light by falling, this will check for it
            List<PlayerEntity> entities = burningEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(burningEntity.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, (LivingEntity) burningEntity) && !p.equals(burningEntity)){
                    entityBlazingTriggerCheck(p, (LivingEntity) burningEntity);
                }
            }
        }));

        //Triggers when someone/thing is freezing
        EntityFreezingEvent.EVENT.register(((freezingEntity) -> {
            //TODO see if this works
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
                List<PlayerEntity> entities1 = freezingEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(freezingEntity.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(freezingEntity)){
                        entityFreezingTriggerCheck(p, (LivingEntity) freezingEntity);
                    }
                }
            }
            //if someone is a teammate of a player that can trigger their light by falling, this will check for it
            List<PlayerEntity> entities = freezingEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(freezingEntity.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, (LivingEntity) freezingEntity) && !p.equals(freezingEntity)){
                    entityFreezingTriggerCheck(p, (LivingEntity) freezingEntity);
                }
            }
        }));

        //Triggers when someone/thing is drowining
        EntityDrowningEvent.EVENT.register(((drowningEntity) -> {
            //TODO see if this works
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
                List<PlayerEntity> entities1 = drowningEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(drowningEntity.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
                for(PlayerEntity p : entities1){
                    if(!p.equals(drowningEntity)){
                        entityDrowningTriggerCheck(p, (LivingEntity) drowningEntity);
                    }
                }
            }
            //if someone is a teammate of a player that can trigger their light by falling, this will check for it
            List<PlayerEntity> entities = drowningEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(drowningEntity.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
            for(PlayerEntity p : entities){
                if(CheckUtils.CheckAllies.checkAlly(p, (LivingEntity) drowningEntity) && !p.equals(drowningEntity)){
                    entityDrowningTriggerCheck(p, (LivingEntity) drowningEntity);
                }
            }
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
            if(component.getType().equals(InnerLightType.AQUA) && entity instanceof LivingEntity){
                checkAqua(player, component, player, (LivingEntity) entity);
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
                    if(component.getType().equals(InnerLightType.AQUA) && entity instanceof LivingEntity){
                        checkAqua(player, component, player, (LivingEntity) entity);
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

        //TODO remove DEBUG
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
        //Wind
        else if(String.valueOf(id_bits[type_bit].charAt(i)).matches("[6-7]")){
            return new Pair<InnerLightType, TargetType>(InnerLightType.WIND, determineTarget(id_bits, target_bit, Arrays.asList(TargetType.SELF, TargetType.ALLIES, TargetType.OTHER)));
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
        LOGGER.info("Id bit: " + id_bits[string_bit]);
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
