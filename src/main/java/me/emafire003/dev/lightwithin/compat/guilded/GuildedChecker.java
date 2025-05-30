package me.emafire003.dev.lightwithin.compat.guilded;

import keno.guildedparties.api.data.guilds.Guild;
import keno.guildedparties.api.utils.GuildApi;
import net.minecraft.server.network.ServerPlayerEntity;

//TODO maybe test it out?
public class GuildedChecker {


    public static String getModId(){
        return "guildedparties";
    }

    /**
     * Checks if two players share a Guilded guild together.
     * If one of the two is not in a guild, it will return false.
     *
     * @param player The first player
     * @param player1 The second player*/
    public static boolean areInSameGuild(ServerPlayerEntity player, ServerPlayerEntity player1){

        Guild guild = GuildApi.getGuild(player).get();
        Guild guild1 = GuildApi.getGuild(player1).get();

        if(guild == null || guild1 == null){
            return false;
        }
        return guild.equals(guild1);
    }

}
