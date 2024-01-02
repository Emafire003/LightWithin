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
    static GuildApi guildApi = GuildApi.API;
    static PartyApi partyApi = PartyApi.API;


    /**
     * Checks if two players share a guild together.
     * If one of the two is not in a guild, it will return false.
     *
     * @param player The first player
     * @param player1 The second player*/
    public static boolean areInSameGuild(PlayerEntity player, PlayerEntity player1){
        Guild guild = guildApi.get(player.getServer(), player.getUuid());
        Guild guild1 = guildApi.get(player1.getServer(), player.getUuid());
        if(guild == null || guild1 == null){
            return false;
        }
        //TODO remove
        LightWithin.LOGGER.info("[DEBUG] Checking guilds...");
        LightWithin.LOGGER.info("Guild 1: " + guild.id());
        LightWithin.LOGGER.info("Guild 2: " + guild1.id());
        if (guild.id().equals(guild.id())) {
            LightWithin.LOGGER.info("Yep, the same");
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
        Guild guild = guildApi.get(server, player_uuid);
        Guild guild1 = guildApi.get(server, player1_uuid);
        if(guild == null || guild1 == null){
            return false;
        }
        //TODO remove
        LightWithin.LOGGER.info("[DEBUG] Checking guilds...");
        LightWithin.LOGGER.info("Guild 1: " + guild.id());
        LightWithin.LOGGER.info("Guild 2: " + guild1.id());
        if (guild.id().equals(guild.id())) {
            LightWithin.LOGGER.info("Yep, the same");
            return true;
        }
        return false;
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
        Party party = partyApi.getPlayerParty(player_uuid);
        Party party1 = partyApi.getPlayerParty(player1_uuid);
        if(party == null || party1 == null){
            return false;
        }
        //TODO remove
        LightWithin.LOGGER.info("[DEBUG] Checking parties...");
        LightWithin.LOGGER.info("Party 1: " + party.id());
        LightWithin.LOGGER.info("Party 2: " + party1.id());
        if (party.id().equals(party1.id())) {
            LightWithin.LOGGER.info("Yep, the same");
            return true;
        }
        return false;
    }
}
