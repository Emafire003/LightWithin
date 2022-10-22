package me.emafire003.dev.lightwithin.networking;

import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.NoSuchElementException;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class LightReadyPacketS2C extends PacketByteBuf {
    public static final Identifier ID = new Identifier(LightWithin.MOD_ID , "light_ready_packet");

    public LightReadyPacketS2C(boolean ready) {
        super(Unpooled.buffer());
        this.writeBoolean(ready);
    }

    public static boolean read(PacketByteBuf buf) {
        try{
            return buf.readBoolean();
        }catch (NoSuchElementException e){
            LOGGER.warn("No value in the packet while reading, probably not a big problem");
            return buf.readBoolean();
        }catch (Exception e){
            LOGGER.error("There was an error while reading the packet!");
            e.printStackTrace();
            return buf.readBoolean();
        }

    }
}
