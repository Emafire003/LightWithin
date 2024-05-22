package me.emafire003.dev.lightwithin.networking;

import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.NoSuchElementException;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

@Deprecated
public class ConfigOptionsSyncPacketS2C extends PacketByteBuf {
    public static final Identifier ID = new Identifier(LightWithin.MOD_ID , "light_config_sync");

    /**Currently used only for the auto light activation*/
    public ConfigOptionsSyncPacketS2C(Map<String, Boolean> bool_settings) {
        super(Unpooled.buffer());
        this.writeMap(bool_settings, PacketByteBuf::writeString, PacketByteBuf::writeBoolean);
    }


    public static Map<String, Boolean> readBooleans(PacketByteBuf buf) {
        try{
            return buf.readMap(PacketByteBuf::readString, PacketByteBuf::readBoolean);
        }catch (NoSuchElementException e){
            LOGGER.warn("No value in the packet while reading, probably not a big problem");
            return buf.readMap(PacketByteBuf::readString, PacketByteBuf::readBoolean);
        }catch (Exception e){
            LOGGER.error("There was an error while reading the packet!");
            e.printStackTrace();
            return buf.readMap(PacketByteBuf::readString, PacketByteBuf::readBoolean);
        }
    }

    public static boolean read(PacketByteBuf buf, String option) {
        try{
            return buf.readMap(PacketByteBuf::readString, PacketByteBuf::readBoolean).get(option);
        }catch (NoSuchElementException e){
            LOGGER.warn("No value in the packet while reading, probably not a big problem");
            return false;
        }catch (Exception e){
            LOGGER.error("There was an error while reading the packet!");
            e.printStackTrace();
            return false;
        }
    }

}
