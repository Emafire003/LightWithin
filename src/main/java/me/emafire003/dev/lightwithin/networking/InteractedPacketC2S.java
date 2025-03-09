package me.emafire003.dev.lightwithin.networking;

import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;


/**Sent when a player interacts with something, aka right clicks on something*/
public class InteractedPacketC2S extends PacketByteBuf {
    public static final Identifier ID = new Identifier(LightWithin.MOD_ID , "interacted_packet");

    public InteractedPacketC2S() {
        super(Unpooled.buffer());
    }

    public static boolean read(PacketByteBuf buf) {
        return true;
    }
}
