package me.emafire003.dev.lightwithin.util;

import me.emafire003.dev.lightwithin.networking.LightReadyPacketS2C;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import static me.emafire003.dev.lightwithin.LightWithin.*;

public class LightTriggerChecks {
    //If the sum of the things happening to player is greater than this the light activates
    /// this is updatet in LightWithin class, when the server starts.
    public static double MIN_TRIGGER = 5;
    
    public static double getMinTrigger(){
        return MIN_TRIGGER;
    }

    public static void sendReadyPacket(ServerPlayerEntity player, boolean b){
        try{
            ServerPlayNetworking.send(player, LightReadyPacketS2C.ID, new LightReadyPacketS2C(b));
            addToReadyList(player);
        }catch(Exception e){
            LOGGER.error("FAILED to send data packets to the client!");
            e.printStackTrace();
        }
    }
    
    /**Send the ready packet and sets the light as naturally triggered*/
    public static void sendLightTriggered(ServerPlayerEntity player){
        if(!LIGHT_COMPONENT.get(player).hasTriggeredNaturally()){
            LIGHT_COMPONENT.get(player).setTriggeredNaturally(true);
        }
        sendReadyPacket(player, true);
    }
}
