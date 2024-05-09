package me.emafire003.dev.lightwithin.compat.ftb_teams;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.PartyTeam;
import dev.ftb.mods.ftbteams.data.Team;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class FTBTeamsChecker {

    public static String getModId(){
        return "ftbteams";
    }

    /**
     * Checks if two players share an FTB Teams together.
     *
     * @param player The first player
     * @param player1 The second player*/
    public static boolean areInSameParty(PlayerEntity player, PlayerEntity player1){
        return FTBTeamsAPI.arePlayersInSameTeam(player.getUuid(), player1.getUuid());
    }

    /**
     * Checks if two players are in FTB teams that are allied.
     *
     * @param player The first player
     * @param player1 The second player*/
    public static boolean areInAlliedPartis(ServerPlayerEntity player, ServerPlayerEntity player1){
        Team team = FTBTeamsAPI.getPlayerTeam(player);

        if(team.isAlly(player1.getUuid())){
            return true;
        }
        Team team1 = FTBTeamsAPI.getPlayerTeam(player1);

        if(team1.isAlly(player.getUuid())){
            return true;
        }
        return false;
    }

    /**
     * Checks if two players share an FTB Teams together.
     *
     * @param uuid The first player's UUID
     * @param uuid1 The second player' UUID*/
    public static boolean areInSameParty(UUID uuid, UUID uuid1){
        return FTBTeamsAPI.arePlayersInSameTeam(uuid, uuid1);
    }

}

