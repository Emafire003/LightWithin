package me.emafire003.dev.lightwithin.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;


public interface EntityFallingEvent {
    Event<EntityFallingEvent> EVENT = EventFactory.createArrayBacked(EntityFallingEvent.class, (listeners) -> (fallingEntity, heightDifference, fallDistance) -> {
        for (EntityFallingEvent listener : listeners) {
           listener.falling(fallingEntity, heightDifference, fallDistance);
        }
    });

    void falling(LivingEntity fallingEntity, double heightDifference, float fallDistance);
}
