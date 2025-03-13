package me.emafire003.dev.lightwithin.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.util.StringIdentifiable;

public enum RenderEffect implements StringIdentifiable {
    RUNES("runes"),
    LIGHT_RAYS("light_rays"),
    LUXCOGNITA_SCREEN("luxcognita_screen"),
    FORCED_LIGHT_RAYS("forced_light_rays");

    public static final Codec<RenderEffect> CODEC = StringIdentifiable.createCodec(RenderEffect::values);

    /*public static <T extends Enum<T>> T readEnumConstant(Class<T> enumClass, ByteBuf buf) {
        return (T)enumClass.getEnumConstants()[VarInts.read(buf)];
    }

    public PacketByteBuf writeEnumConstant(Enum<?> instance) {
        return this.writeVarInt(instance.ordinal());
    }

    public PacketByteBuf writeVarInt(int value) {
        VarInts.write(this.parent, value);
        return this;
    }*/
    public static final PacketCodec<ByteBuf, RenderEffect> PACKET_CODEC = new PacketCodec<ByteBuf, RenderEffect>() {
        @Override
        public RenderEffect decode(ByteBuf buf) {
            return RenderEffect.class.getEnumConstants()[VarInts.read(buf)];
        }

        @Override
        public void encode(ByteBuf buf, RenderEffect value) {
            VarInts.write(buf, value.ordinal());
        }
    };

    private final String name;
    RenderEffect(final String name){
        this.name = name;
    }
    @Override
    public String asString() {
        return this.name;
    }
}
