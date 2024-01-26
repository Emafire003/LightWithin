package me.emafire003.dev.lightwithin.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public interface EntityDeathEvent {
    Event<EntityDeathEvent> EVENT = EventFactory.createArrayBacked(EntityDeathEvent.class, (listeners) -> (entity, source) -> {
        for (EntityDeathEvent listener : listeners) {
           listener.dead(entity, source);
        }
    });

    void dead(LivingEntity entity, DamageSource source);
}
