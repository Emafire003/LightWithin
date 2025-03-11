package me.emafire003.dev.lightwithin.networking;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.util.ForestAuraRelation;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.Map;
import java.util.UUID;


/**
 * As for the integer, it represents the realation:
 * 0 means neutral (glow green)
 * 1 means enemy (glow red)
 * 2 means ally (glow blue)
 * */
public record GlowEntitiesPayloadS2C(Map<UUID, ForestAuraRelation> entitiesGlowing, boolean shouldClear) implements CustomPayload {
    public static final Id<GlowEntitiesPayloadS2C> ID = new Id<>(
            LightWithin.getIdentifier("glow_entities")
    );

    public static final PacketCodec<PacketByteBuf, GlowEntitiesPayloadS2C> PACKET_CODEC = PacketCodec.tuple(
            LightPacketCodecs.UUID_FOREST_RELATION_MAP, GlowEntitiesPayloadS2C::entitiesGlowing,
            PacketCodecs.BOOL, GlowEntitiesPayloadS2C::shouldClear,
            GlowEntitiesPayloadS2C::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
