package me.emafire003.dev.lightwithin.networking;

import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;


/**Sent when a player interacts with something, aka right clicks on something*/
public class LuxdreamClientPacketC2S extends PacketByteBuf {
    public static final Identifier ID = new Identifier(LightWithin.MOD_ID , "luxdream_c2s_packet");

    public LuxdreamClientPacketC2S(LuxDialogueActions action) {
        super(Unpooled.buffer());
        this.writeEnumConstant(action);
    }

    public static LuxDialogueActions read(PacketByteBuf buf) {
        return buf.readEnumConstant(LuxDialogueActions.class);
    }
}
