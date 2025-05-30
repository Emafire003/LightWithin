package me.emafire003.dev.lightwithin.networking;

import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class LuxdreamServerPacketS2C extends PacketByteBuf {
    public static final Identifier ID = LightWithin.getIdentifier("luxdream_s2c_packet");

    public LuxdreamServerPacketS2C(LuxDialogueActions action) {
        super(Unpooled.buffer());
        this.writeEnumConstant(action);
    }


    public static LuxDialogueActions read(PacketByteBuf buf) {
        return buf.readEnumConstant(LuxDialogueActions.class);
    }

}
