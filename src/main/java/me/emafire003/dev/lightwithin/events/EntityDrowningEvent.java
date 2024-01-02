package me.emafire003.dev.lightwithin.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;

public interface EntityDrowningEvent {
    Event<EntityDrowningEvent> EVENT = EventFactory.createArrayBacked(EntityDrowningEvent.class, (listeners) -> (drowningEntity) -> {
        for (EntityDrowningEvent listener : listeners) {
           listener.drowning(drowningEntity);
        }
    });

    void drowning(Entity drowningEntity);
}
