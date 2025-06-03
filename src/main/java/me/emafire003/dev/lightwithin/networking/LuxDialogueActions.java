package me.emafire003.dev.lightwithin.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.util.StringIdentifiable;

public enum LuxDialogueActions implements StringIdentifiable {
    //C2S
    STOP_DREAM("STOP_DREAM"),

    //S2C
    START_BGM("START_BGM"),
    STOP_BGM("STOP_BGM"),
    ATTACKED("ATTACKED");

    public static final PacketCodec<ByteBuf, LuxDialogueActions> PACKET_CODEC = new PacketCodec<ByteBuf, LuxDialogueActions>() {
        @Override
        public LuxDialogueActions decode(ByteBuf buf) {
            return LuxDialogueActions.class.getEnumConstants()[VarInts.read(buf)];
        }

        @Override
        public void encode(ByteBuf buf, LuxDialogueActions value) {
            VarInts.write(buf, value.ordinal());
        }
    };

    private final String name;
    LuxDialogueActions(final String name){
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
