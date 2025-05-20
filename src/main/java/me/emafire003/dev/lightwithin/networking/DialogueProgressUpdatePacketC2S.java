package me.emafire003.dev.lightwithin.networking;

import io.netty.buffer.Unpooled;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.client.luxcognita_dialogues.DialogueProgressState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.NoSuchElementException;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;


public class DialogueProgressUpdatePacketC2S extends PacketByteBuf {
    public static final Identifier ID = LightWithin.getIdentifier("dialogue_progress_update_packet");

    public DialogueProgressUpdatePacketC2S(DialogueProgressState state, boolean remove) {
        super(Unpooled.buffer());
        this.writeEnumConstant(state);
        this.writeBoolean(remove);
    }


    public static Pair<DialogueProgressState, Boolean> read(PacketByteBuf buf) {
        try{
            return new Pair<>(buf.readEnumConstant(DialogueProgressState.class), buf.readBoolean());
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
