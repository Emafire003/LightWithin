package me.emafire003.dev.lightwithin.networking;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;


public record WindLightVelocityPayloadS2C(double vx, double vy, double vz) implements CustomPayload {
    public static final Id<WindLightVelocityPayloadS2C> ID = new Id<>(
            LightWithin.getIdentifier("wind_light_velocity_packet")
    );

    public static final PacketCodec<PacketByteBuf, WindLightVelocityPayloadS2C> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, WindLightVelocityPayloadS2C::vx,
            PacketCodecs.DOUBLE, WindLightVelocityPayloadS2C::vy,
            PacketCodecs.DOUBLE, WindLightVelocityPayloadS2C::vz,
                WindLightVelocityPayloadS2C::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
