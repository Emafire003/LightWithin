package me.emafire003.dev.lightwithin.items.components;

import com.mojang.serialization.Codec;
import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Uuids;

import java.util.UUID;

public class LightItemComponents {

    public static void registerComponents(){
        LightWithin.LOGGER.debug("Registering custom item components...");
    }
// in the initializer
    public static final ComponentType<UUID> BOTTLED_LIGHT_PLAYER_UUID = registerComponent("player_uuid",
        ComponentType.<UUID>builder().codec(Uuids.CODEC).packetCodec(Uuids.PACKET_CODEC).build());

    public static final ComponentType<String> BOTTLED_LIGHT_TYPE_INGREDIENT = registerComponent("type_ingredient",
            ComponentType.<String>builder().codec(Codec.STRING).packetCodec(PacketCodecs.STRING).build());

    public static final ComponentType<String> BOTTLED_LIGHT_TARGET_INGREDIENT = registerComponent("target_ingredient",
            ComponentType.<String>builder().codec(Codec.STRING).packetCodec(PacketCodecs.STRING).build());
    private static <T> ComponentType<T> registerComponent(String name, ComponentType<T> componentType){
        return Registry.register(Registries.DATA_COMPONENT_TYPE, LightWithin.getIdentifier(name), componentType);
    }

}
