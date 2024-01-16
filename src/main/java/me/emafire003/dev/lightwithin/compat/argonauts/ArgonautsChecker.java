package me.emafire003.dev.lightwithin.compat.argonauts;

import earth.terrarium.argonauts.api.guild.Guild;
import earth.terrarium.argonauts.api.guild.GuildApi;
import earth.terrarium.argonauts.api.party.Party;
import earth.terrarium.argonauts.api.party.PartyApi;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public class ArgonautsChecker {

    public static String getModId(){
        return "argonauts";
    }

    /**
     * Checks if two players share a guild together.
     * If one of the two is not in a guild, it will return false.
     *
     * @param player The first player
     * @param player1 The second player*/
    public static boolean areInSameGuild(PlayerEntity player, PlayerEntity player1){
        GuildApi guildApi = GuildApi.API;

        if(guildApi == null){
            LightWithin.LOGGER.debug("[DEBUG] The guild api is null...");
            return false;
        }
        Guild guild = guildApi.getPlayerGuild(player.getServer(), player.getUuid());
        Guild guild1 = guildApi.getPlayerGuild(player.getServer(), player1.getUuid());
        if(guild == null || guild1 == null){
            return false;
        }
        if (guild.id().equals(guild1.id())) {
            return true;
        }
        return false;
    }


    /**
     * Checks if two players share a guild together.
     * If one of the two is not in a guild, it will return false.
     *
     * @param player_uuid The uuid of the first player
     * @param player1_uuid The uuid of the second player*/
    public static boolean areInSameGuild(UUID player_uuid, UUID player1_uuid, MinecraftServer server){
        try{
            GuildApi guildApi = GuildApi.API;
            Guild guild = guildApi.getPlayerGuild(server, player_uuid);
            Guild guild1 = guildApi.getPlayerGuild(server, player1_uuid);
            if(guild == null || guild1 == null){
                return false;
            }
            if (guild.id().equals(guild1.id())) {
                return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Checks if two players share a Argonauts party  together.
     * If one of the two is not in a party, it will return false.
     *
     * @param player The first player
     * @param player1 The second player*/
    public static boolean areInSameParty(PlayerEntity player, PlayerEntity player1){
        UUID uuid = player.getUuid();
        UUID uuid1 = player1.getUuid();
        return areInSameParty(uuid, uuid1);
    }


    /**
     * Checks if two players share an Argonauts party together.
     * If one of the two is not in a party, it will return false.
     *
     * @param player_uuid The uuid of the first player
     * @param player1_uuid The uuid of the second player*/
    public static boolean areInSameParty(UUID player_uuid, UUID player1_uuid){
        PartyApi partyApi = PartyApi.API;
        Party party = partyApi.getPlayerParty(player_uuid);
        Party party1 = partyApi.getPlayerParty(player1_uuid);
        if(party == null || party1 == null){
            return false;
        }
        if (party.id().equals(party1.id())) {
            return true;
        }
        return false;
    }
}
