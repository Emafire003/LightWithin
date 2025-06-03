package me.emafire003.dev.lightwithin.networking;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record LuxdreamClientPayloadC2S(LuxDialogueActions action) implements CustomPayload {
    public static final Id<LuxdreamClientPayloadC2S> ID = new Id<>(
            LightWithin.getIdentifier("luxdream_c2s_packet")
    );

    public static final PacketCodec<PacketByteBuf, LuxdreamClientPayloadC2S> PACKET_CODEC = PacketCodec.tuple(
            LuxDialogueActions.PACKET_CODEC, LuxdreamClientPayloadC2S::action,
            LuxdreamClientPayloadC2S::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

