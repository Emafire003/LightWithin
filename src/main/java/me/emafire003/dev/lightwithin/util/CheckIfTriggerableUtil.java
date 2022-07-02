package me.emafire003.dev.lightwithin.util;

import me.emafire003.dev.lightwithin.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static me.emafire003.dev.lightwithin.LightWithin.box_expansion_amount;

public class CheckIfTriggerableUtil {

    /**Checks if an entity is surrounded by hostile entities //TODO will need to check for hostile players in factions
     *
     * @param entity The entity that could be surrounded*/
    public static boolean checkSurrounded(@NotNull LivingEntity entity){
        List<HostileEntity> entities = entity.getWorld().getEntitiesByClass(HostileEntity.class, new Box(entity.getBlockPos()).expand(Config.SURROUNDED_DISTANCE), (entity1 -> true));
        //their strength level

        //Should I check if two or more allies are surrounded or just do a bigger area search
        if(Config.CHECK_SURROUNDING_MOBS_HEALTH){
            int mobs_number = 0;
            for (HostileEntity ent : entities){
                if(ent.getHealth() <= (ent.getMaxHealth())*Config.SURROUNDING_HEALTH_THRESHOLD/100){
                    mobs_number++;
                }
            }
            return mobs_number >= Config.SURROUNDED_AMOUNT;
        }else return entities.size() >= Config.SURROUNDED_AMOUNT;
    }

    public static boolean checkSelfHealth(@NotNull PlayerEntity player, int health_percent){
        return player.getHealth() <= (player.getMaxHealth())*health_percent/100;
    }

    /**Will check for nearby allies, if they are on low health and possibly surrounded
     * it will return true
     *
     * @param player The player that could trigger their light
     * @param entity The entity that is attacking it
     * @param health_percent The percentage (15, 25, 70) below which the target is in danger (hence light activatable)
     * */
    public static boolean checkAllyHealth(@NotNull PlayerEntity player, Entity entity, int health_percent){
        List<LivingEntity> entities = player.getWorld().getEntitiesByClass(LivingEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
        int ent_number = 0;
        //I need to this to prevent a ConcurrentModificationError
        List<LivingEntity> team_entities = new ArrayList<>();
        //loops through the entities near the player, if the entities are in the same team as the player
        //and they are not the entity that has been hit then add them to the team_entities and check if their health is ok
        for(LivingEntity ent : entities){
            //Checks if the entity in the list is in the same team/faction/party or not
            if(!entity.equals(ent) && CheckAllies.checkAlly(player, ent) ){
                //if it is, check the health
                if(ent.getHealth() <= (ent.getMaxHealth())*health_percent/100){
                    ent_number++;
                }
                team_entities.add(ent);
            }//if not, try to check if it's a tamable entity. If it is checks if it belongs to player or not
            else if(ent instanceof TameableEntity){
                if(((TameableEntity) ent).getOwner().equals(player)){
                    if(ent.getHealth() <= (ent.getMaxHealth())*health_percent/100){
                        ent_number++;
                    }
                    team_entities.add(ent);
                }
            }
        }
        //If the total team targets && the number of entities of team with the right health are true then
        //return true
        if(team_entities.size() == ent_number){
            return true;
        }
        //otherwise, false
        return false;
    }

    /**Cheks if there are passive mobs nearby, if there are
     * checks the helath. If it is below the health_percent returns true
     *
     * @param player The player that could trigger their light
     * @param entity The entity that attacked the player
     * @param health_percent The percentage (15, 25, 70) below which the target is in danger (hence light activatable)
     * */
    public static boolean checkPassiveHealth(PlayerEntity player, Entity entity, int health_percent){
        List<PassiveEntity> entities = entity.getWorld().getEntitiesByClass(PassiveEntity.class, new Box(player.getBlockPos()).expand(box_expansion_amount), (entity1 -> true));
        for(PassiveEntity ent : entities){
            if(ent.getHealth() <= (ent.getMaxHealth())*health_percent/100){
                return true;
            }
        }
        return false;
    }
}
