package me.emafire003.dev.lightwithin.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public interface ArmorOrToolBreakEvent {
    Event<ArmorOrToolBreakEvent> EVENT = EventFactory.createArrayBacked(ArmorOrToolBreakEvent.class, (listeners) -> (ItemStack item) -> {
        for (ArmorOrToolBreakEvent listener : listeners) {
           listener.brokenItem(item);
        }
    });

    void brokenItem(ItemStack item);
}
