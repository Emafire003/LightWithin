package me.emafire003.dev.lightwithin.networking;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record LuxdreamServerPayloadS2C(LuxDialogueActions action) implements CustomPayload {
    public static final Id<LuxdreamServerPayloadS2C> ID = new Id<>(
            LightWithin.getIdentifier("luxdream_s2c_packet")
    );

    public static final PacketCodec<PacketByteBuf, LuxdreamServerPayloadS2C> PACKET_CODEC = PacketCodec.tuple(
            LuxDialogueActions.PACKET_CODEC, LuxdreamServerPayloadS2C::action,
            LuxdreamServerPayloadS2C::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

