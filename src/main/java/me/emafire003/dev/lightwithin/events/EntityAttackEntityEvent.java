package me.emafire003.dev.lightwithin.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;

public interface EntityAttackEntityEvent {
    Event<EntityAttackEntityEvent> EVENT = EventFactory.createArrayBacked(EntityAttackEntityEvent.class, (listeners) -> (attacker, target) -> {
        for (EntityAttackEntityEvent listener : listeners) {
           listener.attack(attacker, target);
        }
    });

    void attack(LivingEntity attacker, LivingEntity target);
}
