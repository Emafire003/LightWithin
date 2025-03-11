package me.emafire003.dev.lightwithin.networking;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import me.emafire003.dev.lightwithin.util.ForestAuraRelation;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.util.Uuids;


import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LightPacketCodecs {

    public static PacketCodec<ByteBuf, Map<String, Boolean>> STRING_BOOLEAN_MAP = new PacketCodec<ByteBuf, Map<String, Boolean>>() {
        @Override
        public Map<String, Boolean> decode(ByteBuf buf) {

            int i = VarInts.read(buf);
            HashMap<String, Boolean> map = Maps.newHashMapWithExpectedSize(i);

            for(int j = 0; j < i; ++j) {
                String string = PacketCodecs.STRING.decode(buf);
                boolean b = PacketCodecs.BOOL.decode(buf);
                map.put(string, b);
            }

            return map;
        }

        @Override
        public void encode(ByteBuf buf, Map<String, Boolean> map) {
            VarInts.write(buf, map.size());
            map.forEach((key, value) -> {
                PacketCodecs.STRING.encode(buf, key);
                PacketCodecs.BOOL.encode(buf, value);
            });
        }
    };

    public static PacketCodec<ByteBuf, Map<UUID, Integer>> UUID_INT_MAP = new PacketCodec<ByteBuf, Map<UUID, Integer>>() {
        @Override
        @Nullable
        public Map<UUID, Integer> decode(ByteBuf buf) {

            int i = VarInts.read(buf);
            if(i == -1){
                return null;
            }

            HashMap<UUID, Integer> map = Maps.newHashMapWithExpectedSize(i);

            for(int j = 0; j < i; ++j) {
                UUID uuid = Uuids.PACKET_CODEC.decode(buf);
                int value = PacketCodecs.INTEGER.decode(buf);
                map.put(uuid, value);
            }
            return map;
        }

        @Override
        public void encode(ByteBuf buf, Map<UUID, Integer> map) {
            if(map == null){
                VarInts.write(buf, -1);
                return;
            }
            VarInts.write(buf, map.size());
            map.forEach((key, value) -> {
                Uuids.PACKET_CODEC.encode(buf, key);
                PacketCodecs.INTEGER.encode(buf, value);
            });
        }
    };

    public static PacketCodec<ByteBuf, Map<UUID, ForestAuraRelation>> UUID_FOREST_RELATION_MAP = new PacketCodec<ByteBuf, Map<UUID, ForestAuraRelation>>() {
        @Override
        @Nullable
        public Map<UUID, ForestAuraRelation> decode(ByteBuf buf) {
            Map<UUID, Integer> intmap = UUID_INT_MAP.decode(buf);
            if(intmap == null){
                return null;
            }
            HashMap<UUID, ForestAuraRelation> map = new HashMap<>();

            intmap.keySet().forEach(key -> {
                map.put(key, ForestAuraRelation.values()[intmap.get(key)]);
            });

            return map;
        }

        @Override
        public void encode(ByteBuf buf, Map<UUID, ForestAuraRelation> map) {
            if(map == null){
                UUID_INT_MAP.encode(buf, null);
                return;
            }
            HashMap<UUID, Integer> integer_map = new HashMap<>();

            map.keySet().forEach(key -> integer_map.put(key, map.get(key).ordinal()));
            UUID_INT_MAP.encode(buf, integer_map);
        }
    };
}
