package me.emafire003.dev.lightwithin.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerFirstJoinEvent {
    Event<PlayerFirstJoinEvent> EVENT = EventFactory.createArrayBacked(PlayerFirstJoinEvent.class, (listeners) -> (player, server) -> {
        for (PlayerFirstJoinEvent listener : listeners) {
            listener.joinServerForFirstTime(player, server);
        }
    });

    void joinServerForFirstTime(ServerPlayerEntity player, MinecraftServer server);
}
