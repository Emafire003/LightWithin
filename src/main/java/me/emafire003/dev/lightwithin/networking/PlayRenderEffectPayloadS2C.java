package me.emafire003.dev.lightwithin.networking;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.util.RenderEffect;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;


public record PlayRenderEffectPayloadS2C(RenderEffect effect, Integer targetID) implements CustomPayload {
    public static final Id<PlayRenderEffectPayloadS2C> ID = new Id<>(
            new Identifier(LightWithin.MOD_ID , "play_render_effect_packet")
    );

    public static final PacketCodec<PacketByteBuf, PlayRenderEffectPayloadS2C> PACKET_CODEC = PacketCodec.tuple(
            RenderEffect.PACKET_CODEC, PlayRenderEffectPayloadS2C::effect,
            PacketCodecs.INTEGER, PlayRenderEffectPayloadS2C::targetID,
                PlayRenderEffectPayloadS2C::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
