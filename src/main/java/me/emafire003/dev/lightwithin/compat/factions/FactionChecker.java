package me.emafire003.dev.lightwithin.compat.factions;

import io.icker.factions.api.persistents.Claim;
import io.icker.factions.api.persistents.Faction;
import io.icker.factions.api.persistents.Relationship;
import io.icker.factions.api.persistents.User;
import me.emafire003.dev.lightwithin.config.Config;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FactionChecker {


    public static String getModId(){
        return "factions";
    }
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
            return member.getFaction().equals(member1.getFaction());
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
            return member.getFaction().equals(member1.getFaction());
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
            List<Relationship> enemies = Objects.requireNonNull(member.getFaction()).getEnemiesWith();
            UUID faction1 = member1.getFaction().getID();
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
            if(enemies == null){
                return false;
            }
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


    private static int getRankLevel(User.Rank rank) {
        return switch (rank) {
            case OWNER -> 3;
            case LEADER -> 2;
            case COMMANDER -> 1;
            case MEMBER -> 0;
            case GUEST -> -1;
        };
    }

    public static boolean canActivateHere(PlayerEntity player, BlockPos pos){
        if(Config.LIGHT_USABLE_IN_FACTION.equals(Config.UsableInFactionOptions.EVERYONE.toString())){
            return true;
        }
        User member = User.get(player.getUuid());
        ChunkPos chunkPosition = player.getWorld().getChunk(pos).getPos();
        Claim claim = Claim.get(chunkPosition.x, chunkPosition.z, player.getWorld().getRegistryKey().getValue().toString());
        if(claim == null){
            return true;
        }

        if(!member.isInFaction()){
            return false;
        }

        Faction claimFaction = claim.getFaction();
        Faction memberFaction = member.getFaction();
        boolean sameFaction = claimFaction.equals(memberFaction);
        User.Rank rank = member.rank;

        if(Config.LIGHT_USABLE_IN_FACTION.equals(Config.UsableInFactionOptions.MEMBER.toString())){
            if(getRankLevel(rank) >= getRankLevel(User.Rank.MEMBER)){
                return sameFaction;
            }
            return false;
        }

        if(Config.LIGHT_USABLE_IN_FACTION.equals(Config.UsableInFactionOptions.GUEST.toString())){
            if(getRankLevel(rank) >= getRankLevel(User.Rank.GUEST)){
                return sameFaction;
            }
            return false;
        }

        if(Config.LIGHT_USABLE_IN_FACTION.equals(Config.UsableInFactionOptions.COMMANDER.toString())){
            if(getRankLevel(rank) >= getRankLevel(User.Rank.COMMANDER)){
                return sameFaction;
            }
            return false;
        }

        if(Config.LIGHT_USABLE_IN_FACTION.equals(Config.UsableInFactionOptions.LEADER.toString())){
            if(getRankLevel(rank) >= getRankLevel(User.Rank.LEADER)){
                return sameFaction;
            }
            return false;
        }

        if(Config.LIGHT_USABLE_IN_FACTION.equals(Config.UsableInFactionOptions.OWNER.toString())){
            if(getRankLevel(rank) >= getRankLevel(User.Rank.OWNER)){
                return sameFaction;
            }
            return false;
        }

        if( (sameFaction || claimFaction.isMutualAllies(memberFaction.getID()) )
                && Config.LIGHT_USABLE_IN_FACTION.equals(Config.UsableInFactionOptions.ALLIES.toString())){
            return true;
        }
        if(Config.LIGHT_USABLE_IN_FACTION.equals(Config.UsableInFactionOptions.ENEMIES.toString())){
            List<Relationship> claimEnemies = claimFaction.getEnemiesWith();
            List<Relationship> claimEnemiesOf = claimFaction.getEnemiesOf();

            boolean cEnemies = false;
            for(Relationship relationship : claimEnemies){
                if(relationship.status.equals(Relationship.Status.ENEMY) && relationship.target.equals(memberFaction.getID())){
                    cEnemies = true;
                    break;
                }
            }

            for(Relationship relationship : claimEnemiesOf){
                if(relationship.status.equals(Relationship.Status.ENEMY) && relationship.target.equals(memberFaction.getID())){
                    return cEnemies;
                }
            }
        }

        return false;
    }

}
