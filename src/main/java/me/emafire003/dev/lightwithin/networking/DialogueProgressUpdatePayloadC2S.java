package me.emafire003.dev.lightwithin.networking;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.DialogueProgressState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record DialogueProgressUpdatePayloadC2S(DialogueProgressState state, boolean shouldRemove) implements CustomPayload {
    public static final Id<DialogueProgressUpdatePayloadC2S> ID = new Id<>(
            LightWithin.getIdentifier("dialogue_progress_update_packet")
    );

    public static final PacketCodec<PacketByteBuf, DialogueProgressUpdatePayloadC2S> PACKET_CODEC = PacketCodec.tuple(
            DialogueProgressState.PACKET_CODEC, DialogueProgressUpdatePayloadC2S::state,
            PacketCodecs.BOOL, DialogueProgressUpdatePayloadC2S::shouldRemove,
            DialogueProgressUpdatePayloadC2S::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
