package me.emafire003.dev.lightwithin.networking;

import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

@Deprecated
public class WindLightVelocityPacketS2C extends PacketByteBuf {
    public static final Identifier ID = new Identifier(LightWithin.MOD_ID , "wind_light_velocity_packet");

    public WindLightVelocityPacketS2C(Vec3d vel) {
        super(Unpooled.buffer());
        this.writeInt(3);
        this.writeDouble(vel.getX());
        this.writeDouble(vel.getY());
        this.writeDouble(vel.getZ());
    }

    public static @Nullable Vec3d read(PacketByteBuf buf) {
        try{
            List<Double> results = new LinkedList<>();
            int max = buf.readInt();
            for (int i = 0; i < max; i++) {
                results.add(buf.readDouble());
            }
            return new Vec3d(results.get(0), results.get(1), results.get(2));
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
