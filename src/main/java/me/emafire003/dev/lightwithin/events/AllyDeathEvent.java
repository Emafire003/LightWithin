package me.emafire003.dev.lightwithin.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public interface AllyDeathEvent {
    Event<AllyDeathEvent> EVENT = EventFactory.createArrayBacked(AllyDeathEvent.class, (listeners) -> (entity, source) -> {
        for (AllyDeathEvent listener : listeners) {
           listener.dead(entity, source);
        }
    });

    void dead(LivingEntity entity, DamageSource source);
}
