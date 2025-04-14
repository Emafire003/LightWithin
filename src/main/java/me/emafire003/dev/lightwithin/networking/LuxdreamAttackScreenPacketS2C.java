package me.emafire003.dev.lightwithin.networking;

import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class LuxdreamAttackScreenPacketS2C extends PacketByteBuf {
    public static final Identifier ID = LightWithin.getIdentifier("luxdream_show_attacked_screen");

    public LuxdreamAttackScreenPacketS2C() {
        super(Unpooled.buffer());
    }


    public static boolean read(PacketByteBuf buf) {
        return true;
    }

}
