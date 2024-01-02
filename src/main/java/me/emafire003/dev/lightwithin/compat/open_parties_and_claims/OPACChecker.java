package me.emafire003.dev.lightwithin.compat.open_parties_and_claims;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import xaero.pac.common.parties.party.ally.api.IPartyAllyAPI;
import xaero.pac.common.parties.party.api.IPartyPlayerInfoAPI;
import xaero.pac.common.parties.party.member.api.IPartyMemberAPI;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.parties.party.api.IPartyManagerAPI;
import xaero.pac.common.server.parties.party.api.IServerPartyAPI;

import java.util.UUID;

public class OPACChecker {

    /**
     * Checks if two players share a OPAC party together.
     * If one of the two is not in a party, it will return false.
     *
     * @param player The first player
     * @param player1 The second player*/
    public static boolean areInSameParty(PlayerEntity player, PlayerEntity player1){
        IPartyManagerAPI<IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI>> party_manager = OpenPACServerAPI.get(player.getServer()).getPartyManager();
        IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI> party = party_manager.getPartyByMember(player.getUuid());
        IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI> party1 = party_manager.getPartyByMember(player1.getUuid());
        if(party == null || party1 == null){
            return false;
        }

        //TODO remove
        LightWithin.LOGGER.debug("[DEBUG] Checking xareos party...");
        LightWithin.LOGGER.debug("XParty 1: " + party.getDefaultName()+ "ID: " + party.getId());
        LightWithin.LOGGER.debug("XParty 2: " + party1.getDefaultName() + "ID: " + party1.getId());
        if (party.getId().equals(party.getId())) {
            LightWithin.LOGGER.debug("Yep, the same");
            return true;
        }
        return false;
    }

    /**
     * Checks if two players share an OPAC party together.
     * If one of the two is not in a party, it will return false.
     *
     * @param uuid The first player's UUID
     * @param uuid1 The second player' UUID*/
    public static boolean areInSameParty(UUID uuid, UUID uuid1, MinecraftServer server){
        IPartyManagerAPI<IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI>> party_manager = OpenPACServerAPI.get(server).getPartyManager();
        IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI> party = party_manager.getPartyByMember(uuid);
        IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI> party1 = party_manager.getPartyByMember(uuid1);
        if(party == null || party1 == null){
            return false;
        }

        //TODO remove
        LightWithin.LOGGER.info("[DEBUG] Checking xareos party...");
        LightWithin.LOGGER.info("XParty 1: " + party.getDefaultName()+ "ID: " + party.getId());
        LightWithin.LOGGER.info("XParty 2: " + party1.getDefaultName() + "ID: " + party1.getId());
        if (party.getId().equals(party.getId())) {
            LightWithin.LOGGER.info("Yep, the same");
            return true;
        }
        return false;
    }


    /**
     * Checks if two players are in allied OPAC parties.
     * If one of the two is not in a party, it will return false.
     *
     * @param player The first player
     * @param player1 The second player*/
    public static boolean areInAlliedParties(PlayerEntity player, PlayerEntity player1){
        IPartyManagerAPI<IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI>> party_manager = OpenPACServerAPI.get(player.getServer()).getPartyManager();
        IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI> party = party_manager.getPartyByMember(player.getUuid());
        IServerPartyAPI<IPartyMemberAPI, IPartyPlayerInfoAPI, IPartyAllyAPI> party1 = party_manager.getPartyByMember(player1.getUuid());
        if(party == null || party1 == null){
            return false;
        }

        //TODO remove
        LightWithin.LOGGER.info("[DEBUG] Checking xareos party...");
        LightWithin.LOGGER.info("XParty 1: " + party.getDefaultName()+ "ID: " + party.getId());
        LightWithin.LOGGER.info("XParty 2: " + party1.getDefaultName() + "ID: " + party1.getId());
        if (party.isAlly(party1.getId())) {
            LightWithin.LOGGER.info("Yep, allied");
            return true;
        }
        return false;
    }
}

