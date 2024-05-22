package me.emafire003.dev.lightwithin.networking;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**True means make the light used, while false means
 * make the light no more used if it was. Used when bottling up light*/
public record LightReadyPayloadS2C(boolean ready) implements CustomPayload {
    public static final CustomPayload.Id<LightReadyPayloadS2C> ID = new CustomPayload.Id<>(new Identifier(LightWithin.MOD_ID , "light_ready_packet"));

    public static final PacketCodec<PacketByteBuf, LightReadyPayloadS2C> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, LightReadyPayloadS2C::ready,
            LightReadyPayloadS2C::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }


}
