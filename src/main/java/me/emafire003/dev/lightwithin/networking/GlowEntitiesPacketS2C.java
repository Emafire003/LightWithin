package me.emafire003.dev.lightwithin.networking;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.util.ForestAuraRelation;
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

    /**
     * As for the integer, it represents the realation:
     * 0 means neutral (glow green)
     * 1 means enemy (glow red)
     * 2 means ally (glow blue)
     * */
    public GlowEntitiesPacketS2C(@Nullable List<Pair<UUID, ForestAuraRelation>> entitiesGlowing, boolean shouldClear) {
        super(Unpooled.buffer());
        this.writeBoolean(shouldClear);
        if(entitiesGlowing != null){
            this.writeVarInt(entitiesGlowing.size());
            for(Pair<UUID, ForestAuraRelation> pair : entitiesGlowing){
                this.writeUuid(pair.getFirst());
                this.writeInt(pair.getSecond().ordinal());
            }
        }
    }

    /**
     * 0 means neutral (glow green)
     * 1 means enemy (glow red)
     * 2 means ally (glow blue)
     * */
    public static List<Pair<UUID, ForestAuraRelation>> read(PacketByteBuf buf) {
        try{
            if(buf.readBoolean()){
                return List.of(new Pair<>(new UUID(0,0), ForestAuraRelation.CLEAR));
            }
            int size = buf.readVarInt();

            List<Pair<UUID, ForestAuraRelation>> entitiesGlowing = new ArrayList<>(size);
            for(int i = 0; i<size; i++){
                entitiesGlowing.add(new Pair<>(buf.readUuid(), ForestAuraRelation.values()[buf.readInt()]));
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
