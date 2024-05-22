package me.emafire003.dev.lightwithin.networking;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.encoding.VarInts;


import java.util.HashMap;
import java.util.Map;

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
}
