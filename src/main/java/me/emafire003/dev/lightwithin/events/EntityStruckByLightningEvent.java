package me.emafire003.dev.lightwithin.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;

public interface EntityStruckByLightningEvent {
    Event<EntityStruckByLightningEvent> EVENT = EventFactory.createArrayBacked(EntityStruckByLightningEvent.class, (listeners) -> (entity) -> {
        for (EntityStruckByLightningEvent listener : listeners) {
           listener.lightningBolted(entity);
        }
    });

    void lightningBolted(Entity entity);
}
