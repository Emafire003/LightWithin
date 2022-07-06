package me.emafire003.dev.lightwithin.compat.factions;

import io.icker.factions.api.persistents.Relationship;
import io.icker.factions.api.persistents.User;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.UUID;

public class FactionChecker {

    /**
     * Checks if two players share a faction together.
     * If one of the two is not in a faction, it will return false.
     *
     * @param player The first player
     * @param player1 The second player*/
    public static boolean areInSameFaction(PlayerEntity player, PlayerEntity player1){
        User member = User.get(player.getUuid());
        User member1 = User.get(player1.getUuid());
        if (member.isInFaction() && member1.isInFaction()) {
            if(member.getFaction().equals(member1.getFaction())){
                return true;
            }
        }
        return false;
    }


    /**
     * Checks if two players share a faction together.
     * If one of the two is not in a faction, it will return false.
     *
     * @param player_uuid The uuid of the first player
     * @param player1_uuid The uuid of the second player*/
    public static boolean areInSameFaction(UUID player_uuid, UUID player1_uuid){
        User member = User.get(player_uuid);
        User member1 = User.get(player1_uuid);
        if (member.isInFaction() && member1.isInFaction()) {
            if(member.getFaction().equals(member1.getFaction())){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if two players are in enemy factions
     * If one of the two is not in a faction, it will return false.
     *
     * @param player The first player
     * @param player1 The second player*/
    public static boolean areEnemies(PlayerEntity player, PlayerEntity player1){
        User member = User.get(player.getUuid());
        User member1 = User.get(player1.getUuid());
        if (member.isInFaction() && member1.isInFaction()) {
            List<Relationship> enemies = member.getFaction().getEnemiesWith();
            UUID faction1 = member.getFaction().getID();
            for(Relationship en : enemies){
                if(en.status.equals(Relationship.Status.ENEMY) && en.target.equals(faction1)){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Checks if two players are in enemy factions
     * If one of the two is not in a faction, it will return false.
     *
     * @param player_uuid The uuid of the first player
     * @param player1_uuid The uuid of the second player*/
    public static boolean areEnemies(UUID player_uuid, UUID player1_uuid){
        User member = User.get(player_uuid);
        User member1 = User.get(player1_uuid);
        if (member.isInFaction() && member1.isInFaction()) {
            List<Relationship> enemies = member.getFaction().getEnemiesWith();
            UUID faction1 = member.getFaction().getID();
            for(Relationship en : enemies){
                if(en.status.equals(Relationship.Status.ENEMY) && en.target.equals(faction1)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if two players are in allied factions.
     * If one of the two is not in a faction, it will return false.
     *
     * @param player The first player
     * @param player1 The second player*/
    public static boolean areAllies(PlayerEntity player, PlayerEntity player1){
        User member = User.get(player.getUuid());
        User member1 = User.get(player1.getUuid());
        if (member.isInFaction() && member1.isInFaction()) {
            return member.getFaction().isMutualAllies(member1.getFaction().getID());
        }
        return false;
    }


    /**
     * Checks if two players are in allied factions.
     * If one of the two is not in a faction, it will return false.
     *
     * @param player_uuid The uuid of the first player
     * @param player1_uuid The uuid of the second player*/
    public static boolean areAlliesOrSameFaction(UUID player_uuid, UUID player1_uuid){
        User member = User.get(player_uuid);
        User member1 = User.get(player1_uuid);
        if (member.isInFaction() && member1.isInFaction()) {
            return member.getFaction().isMutualAllies(member1.getFaction().getID());
        }
        return false;
    }
}
