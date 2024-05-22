package me.emafire003.dev.lightwithin.items.components;

import com.mojang.serialization.Codec;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.component.DataComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public class LightItemComponents {

    public static void registerComponents(){
        LightWithin.LOGGER.debug("Registering custom item components...");
    }
// in the initializer
    public static final DataComponentType<UUID> BOTTLED_LIGHT_PLAYER_UUID = registerComponent("playerUUID",
        DataComponentType.<UUID>builder().codec(Uuids.CODEC).packetCodec(Uuids.PACKET_CODEC).build());

    public static final DataComponentType<String> BOTTLED_LIGHT_TYPE_INGREDIENT = registerComponent("typeIngredient",
            DataComponentType.<String>builder().codec(Codec.STRING).packetCodec(PacketCodecs.STRING).build());

    public static final DataComponentType<String> BOTTLED_LIGHT_TARGET_INGREDIENT = registerComponent("targetIngredient",
            DataComponentType.<String>builder().codec(Codec.STRING).packetCodec(PacketCodecs.STRING).build());
    private static <T> DataComponentType<T> registerComponent(String name, DataComponentType<T> componentType){
        return Registry.register(Registries.DATA_COMPONENT_TYPE, new Identifier(LightWithin.MOD_ID, name), componentType);
    }

}
