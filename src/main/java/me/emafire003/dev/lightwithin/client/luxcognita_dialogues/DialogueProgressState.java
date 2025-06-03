package me.emafire003.dev.lightwithin.client.luxcognita_dialogues;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.util.StringIdentifiable;

//TODO add a thing to the component with the state saved. Or maybe to a local file on the client? The light should be the same anyways.
public enum DialogueProgressState implements StringIdentifiable {
    INTRO_DONE("INTRO_DONE"),
    NONE("NONE"),
    PISSED_OFF("PISSED_OFF"),
    ALCHEMY_TAUGHT("ALCHEMY_TAUGHT"),
    KNOW_CHARGE("KNOW_CHARGE"),
    LISTING_TYPES("LISTING_TYPES"),
    LISTING_TARGETS("LISTING_TARGETS");
    
    /*,
    ALREADY_MET_IN_OTHER_SAVE // if i end up using the option of saving to local progress file*/

    public static final PacketCodec<ByteBuf, DialogueProgressState> PACKET_CODEC = new PacketCodec<ByteBuf, DialogueProgressState>() {
        @Override
        public DialogueProgressState decode(ByteBuf buf) {
            return DialogueProgressState.class.getEnumConstants()[VarInts.read(buf)];
        }

        @Override
        public void encode(ByteBuf buf, DialogueProgressState value) {
            VarInts.write(buf, value.ordinal());
        }
    };

    private final String name;
    DialogueProgressState(final String name){
        this.name = name;
    }
    
    @Override
    public String asString() {
        return this.name;
    }
}
