package me.emafire003.dev.lightwithin.networking;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.util.RenderEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.NoSuchElementException;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;


public class PlayRenderEffectPacketS2C extends PacketByteBuf {
    public static final Identifier ID = new Identifier(LightWithin.MOD_ID , "play_render_effect_packet");

    public PlayRenderEffectPacketS2C(RenderEffect effect, LivingEntity target) {
        super(Unpooled.buffer());
        this.writeEnumConstant(effect);
        this.writeInt(target.getId());
    }

    public PlayRenderEffectPacketS2C(RenderEffect effect) {
        super(Unpooled.buffer());
        this.writeEnumConstant(effect);
        this.writeInt(-1);
    }

    public static RenderEffect read(PacketByteBuf buf) {
        try{
            return buf.readEnumConstant(RenderEffect.class);
        }catch (NoSuchElementException e){
            LOGGER.warn("No value in the packet while reading, probably not a big problem");
            return null;
        }catch (Exception e){
            LOGGER.error("There was an error while reading the packet!");
            e.printStackTrace();
            return null;
        }
    }

    public static Pair<RenderEffect, Integer> readTarget(PacketByteBuf buf) {
        try{
            return new Pair<>(buf.readEnumConstant(RenderEffect.class), buf.readInt());
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
