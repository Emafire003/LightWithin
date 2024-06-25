package me.emafire003.dev.lightwithin.networking;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record LightChargeConsumedPayloadC2S(boolean used) implements CustomPayload {
    public static final Id<LightChargeConsumedPayloadC2S> ID = new Id<>(
            LightWithin.getIdentifier("light_charge_consumed_packet")
    );


    public static final PacketCodec<PacketByteBuf, LightChargeConsumedPayloadC2S> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, LightChargeConsumedPayloadC2S::used,
                LightChargeConsumedPayloadC2S::new
    );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }


}
