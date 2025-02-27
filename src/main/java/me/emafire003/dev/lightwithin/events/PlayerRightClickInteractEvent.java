package me.emafire003.dev.lightwithin.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerRightClickInteractEvent {
    Event<PlayerRightClickInteractEvent> EVENT = EventFactory.createArrayBacked(PlayerRightClickInteractEvent.class, (listeners) -> (player) -> {
        for (PlayerRightClickInteractEvent listener : listeners) {
            listener.interact(player);
        }
    });

    void interact(ServerPlayerEntity player);
}
