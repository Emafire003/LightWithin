package me.emafire003.dev.lightwithin.compat.ftb_teams;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class FTBTeamsChecker {

    /**
     * Checks if two players share an FTB Teams together.
     *
     * @param player The first player
     * @param player1 The second player*/
    public static boolean areInSameParty(PlayerEntity player, PlayerEntity player1){
        return FTBTeamsAPI.api().getManager().arePlayersInSameTeam(player.getUuid(), player1.getUuid());
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

