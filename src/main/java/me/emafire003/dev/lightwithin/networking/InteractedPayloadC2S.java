package me.emafire003.dev.lightwithin.networking;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record InteractedPayloadC2S(boolean used) implements CustomPayload {
    public static final Id<InteractedPayloadC2S> ID = new Id<>(
            LightWithin.getIdentifier("interacted_packet")
    );


    public static final PacketCodec<PacketByteBuf, InteractedPayloadC2S> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, InteractedPayloadC2S::used,
                InteractedPayloadC2S::new
    );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }


}
