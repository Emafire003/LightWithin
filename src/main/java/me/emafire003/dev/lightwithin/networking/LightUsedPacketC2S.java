package me.emafire003.dev.lightwithin.networking;

import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;


public class LightUsedPacketC2S extends PacketByteBuf {
    public static final Identifier ID = new Identifier(LightWithin.MOD_ID , "light_used_packet");

    /**@param used Indicates weather or not the light has been activated by using a light charge*/
    public LightUsedPacketC2S(boolean used) {
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
