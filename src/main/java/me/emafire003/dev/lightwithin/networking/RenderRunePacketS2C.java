package me.emafire003.dev.lightwithin.networking;

import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.InnerLightType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.NoSuchElementException;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;


public class RenderRunePacketS2C extends PacketByteBuf {
    public static final Identifier ID = new Identifier(LightWithin.MOD_ID , "lightrune_render_packet");

    public RenderRunePacketS2C(InnerLightType type) {
        super(Unpooled.buffer());
        this.writeString(type.toString());
    }

    public static InnerLightType read(PacketByteBuf buf) {
        try{
            return InnerLightType.valueOf(buf.readString());
        }catch (NoSuchElementException e){
            LOGGER.warn("No value in the packet while reading, probably not a big problem");
            return InnerLightType.NONE;
        }catch (Exception e){
            LOGGER.error("There was an error while reading the packet!");
            e.printStackTrace();
            return InnerLightType.NONE;
        }

    }
}
