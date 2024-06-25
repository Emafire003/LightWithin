package me.emafire003.dev.lightwithin.networking;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**@param used Indicates weather or not the light has been activated by using a light charge*/
public record LightUsedPayloadC2S(boolean used) implements CustomPayload {
    public static final Id<LightUsedPayloadC2S> ID = new Id<>(
            LightWithin.getIdentifier("light_used_packet")
    );
    public static final PacketCodec<PacketByteBuf, LightUsedPayloadC2S> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, LightUsedPayloadC2S::used,
            LightUsedPayloadC2S::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }


}
