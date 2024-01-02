package me.emafire003.dev.lightwithin.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;

public interface EntityBurningEvent {
    Event<EntityBurningEvent> EVENT = EventFactory.createArrayBacked(EntityBurningEvent.class, (listeners) -> (burningEntity) -> {
        for (EntityBurningEvent listener : listeners) {
           listener.burning(burningEntity);
        }
    });

    void burning(Entity burningEntity);
}
