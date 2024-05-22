package me.emafire003.dev.lightwithin.networking;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Map;


public record ConfigOptionSyncPayloadS2C(Map<String, Boolean> booleanSettings) implements CustomPayload {
    public static final Id<ConfigOptionSyncPayloadS2C> ID = new Id<>(
            new Identifier(LightWithin.MOD_ID , "light_config_sync")
    );

    public static final PacketCodec<PacketByteBuf, ConfigOptionSyncPayloadS2C> PACKET_CODEC = PacketCodec.tuple(
            LightPacketCodecs.STRING_BOOLEAN_MAP, ConfigOptionSyncPayloadS2C::booleanSettings,
                ConfigOptionSyncPayloadS2C::new
    );



    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
