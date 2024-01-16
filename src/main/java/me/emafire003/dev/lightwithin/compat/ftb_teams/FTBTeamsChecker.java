package me.emafire003.dev.lightwithin.compat.ftb_teams;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.data.PartyTeam;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;
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
        return FTBTeamsAPI.api().getManager().arePlayersInSameTeam(player.getUuid(), player1.getUuid());
    }

    /**
     * Checks if two players are in FTB teams that are allied.
     *
     * @param player The first player
     * @param player1 The second player*/
    public static boolean areInAlliedPartis(ServerPlayerEntity player, ServerPlayerEntity player1){
        Optional<Team> team = FTBTeamsAPI.api().getManager().getTeamForPlayer(player);
        if(team.isPresent() && team.get().isPartyTeam()){
            PartyTeam pteam = (PartyTeam) team.get();
            return pteam.isAllyOrBetter(player1.getUuid());
        }
        Optional<Team> team1 = FTBTeamsAPI.api().getManager().getTeamForPlayer(player1);
        if(team1.isPresent() && team1.get().isPartyTeam()){
            PartyTeam pteam1 = (PartyTeam) team1.get();
            return pteam1.isAllyOrBetter(player.getUuid());
        }
        return false;
    }

    /**
     * Checks if two players share an FTB Teams together.
     *
     * @param uuid The first player's UUID
     * @param uuid1 The second player' UUID*/
    public static boolean areInSameParty(UUID uuid, UUID uuid1){
        return FTBTeamsAPI.api().getManager().arePlayersInSameTeam(uuid, uuid1);
    }

}

