package me.emafire003.dev.lightwithin.networking;

import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;


public class LightChargeConsumedPacketC2S extends PacketByteBuf {
    public static final Identifier ID = new Identifier(LightWithin.MOD_ID , "light_charge_consumed_packet");

    public LightChargeConsumedPacketC2S(boolean used) {
        super(Unpooled.buffer());
        this.writeBoolean(used);
    }

    public static Context read1(PacketByteBuf buf) {
        boolean used = buf.readBoolean();
        return new Context(used);
    }

    public static boolean read(PacketByteBuf buf) {
        return buf.readBoolean();
    }

    public record Context(boolean used) {
    }
}
