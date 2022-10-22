package me.emafire003.dev.lightwithin.networking;

import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class LightUsedPacketC2S extends PacketByteBuf {
    public static final Identifier ID = new Identifier(LightWithin.MOD_ID , "light_used_packet");

    //TODO insert either the component or the player or the UUID if i can get the player from uuid from the server
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
