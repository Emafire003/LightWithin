package me.emafire003.dev.lightwithin.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;

public interface EntityFreezingEvent {
    Event<EntityFreezingEvent> EVENT = EventFactory.createArrayBacked(EntityFreezingEvent.class, (listeners) -> (freezingEntity) -> {
        for (EntityFreezingEvent listener : listeners) {
           listener.freezing(freezingEntity);
        }
    });

    void freezing(Entity freezingEntity);
}
