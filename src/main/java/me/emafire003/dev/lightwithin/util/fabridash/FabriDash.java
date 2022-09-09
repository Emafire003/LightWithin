package me.emafire003.dev.lightwithin.util.fabridash;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class FabriDash {
    
    public static void dash(LivingEntity entity, float power, boolean reversed){
        float f = entity.getYaw();
        float g = entity.getPitch();
        float h = -MathHelper.sin(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
        float k = -MathHelper.sin(g * 0.017453292F);
        float l = MathHelper.cos(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
        float m = MathHelper.sqrt(h * h + k * k + l * l);
        float n = 3.0F * ((1.0F + power) / 4.0F);
        h *= n / m;
        k *= n / m;
        l *= n / m;
        if(reversed){
            entity.addVelocity(-h, k, -l);
        }else{
            entity.addVelocity(h, k, l);
        }
        if(entity instanceof ServerPlayerEntity){
            sendVelocityPacket((ServerPlayerEntity) entity, entity.getVelocity());
        }
    }

    public static void sendVelocityPacket(ServerPlayerEntity player, Vec3d vel){
        LOGGER.info("Sending the packet, with this vel: "+ vel);
        try{
            ServerPlayNetworking.send(player, WindLightVelocityPacketS2C.ID, new WindLightVelocityPacketS2C(vel));
        }catch(Exception e){
            LOGGER.error("FAILED to send data packets to the client!");
            e.printStackTrace();
        }
    }
}
