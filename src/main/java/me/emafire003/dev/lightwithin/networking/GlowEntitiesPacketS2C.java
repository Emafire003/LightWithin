package me.emafire003.dev.lightwithin.networking;

import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class GlowEntitiesPacketS2C extends PacketByteBuf {
    public static final Identifier ID = new Identifier(LightWithin.MOD_ID , "glow_entities");

    /**Currently used only for the auto light activation*/
    public GlowEntitiesPacketS2C(@Nullable List<UUID> entitiesGlowing, boolean shouldClear) {
        super(Unpooled.buffer());
        this.writeBoolean(shouldClear);
        if(entitiesGlowing != null){
            this.writeVarInt(entitiesGlowing.size());
            for(UUID uuid : entitiesGlowing){
                this.writeUuid(uuid);
            }
        }
    }

    public static List<UUID> read(PacketByteBuf buf) {
        try{
            if(buf.readBoolean()){
                return List.of(new UUID(0,0));
            }
            int size = buf.readVarInt();
            List<UUID> entitiesGlowing = new ArrayList<>(size);
            for(int i = 0; i<size; i++){
                entitiesGlowing.add(buf.readUuid());
            }
            return entitiesGlowing;
        }catch (NoSuchElementException e){
            LOGGER.warn("No value in the packet while reading, probably not a big problem");
            return null;
        }catch (Exception e){
            LOGGER.error("There was an error while reading the packet!");
            e.printStackTrace();
            return null;
        }
    }

}
