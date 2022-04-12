package me.emafire003.dev.lightwithin.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerJoinEvent {
    Event<PlayerJoinEvent> EVENT = EventFactory.createArrayBacked(PlayerJoinEvent.class, (listeners) -> (player, server) -> {
        for (PlayerJoinEvent listener : listeners) {
            listener.joinServer(player, server);
        }
    });

    void joinServer(ServerPlayerEntity player, MinecraftServer server);
}
