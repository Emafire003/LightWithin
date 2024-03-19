package me.emafire003.dev.lightwithin.items;

import me.emafire003.dev.lightwithin.LightWithin;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class LightItems {

    public static final Item LUXINTUS_BERRY = registerItem("luxintus_berry",
            new LuxintusBerryItem(new Item.Settings().rarity(Rarity.EPIC)
                    .food(new FoodComponent.Builder().alwaysEdible().hunger(5).build()).maxCount(16)),
            ItemGroups.FOOD_AND_DRINK, Items.GLOW_BERRIES);

    public static final Item LUXCOGNITA_BERRY = registerItem("luxcognita_berry",
            new LuxcognitaBerryItem(new Item.Settings().rarity(Rarity.RARE)
                    .food(new FoodComponent.Builder().alwaysEdible().hunger(5).build()).maxCount(16)),
            ItemGroups.FOOD_AND_DRINK, Items.GLOW_BERRIES);

    public static final Item LUXMUTUA_BERRY = registerItem("luxmutua_berry",
            new LuxmutuaBerryItem(new Item.Settings().rarity(Rarity.EPIC)
                    .food(new FoodComponent.Builder().alwaysEdible().hunger(5).build()).maxCount(16)),
            ItemGroups.FOOD_AND_DRINK, Items.GLOW_BERRIES);

    public static final Item BOTTLED_LIGHT = registerItem("bottled_light",
            new BottledLightItem(new Item.Settings().rarity(Rarity.RARE).maxCount(1)),
            ItemGroups.FOOD_AND_DRINK, Items.GLOW_BERRIES);

    public static final Item LUXINTUS_BERRY_POWDER = registerItem("luxintus_berry_powder",
            new Item(new Item.Settings().rarity(Rarity.EPIC).maxCount(64)),
            ItemGroups.INGREDIENTS, Items.BLAZE_POWDER);

    private static Item registerItem(String name, Item item, RegistryKey<ItemGroup> group, Item add_after){
        ItemGroupEvents.modifyEntriesEvent(group).register(content -> {
            content.addAfter(add_after, item);
        });
        return Registry.register(Registries.ITEM, new Identifier(LightWithin.MOD_ID, name), item);
    }

    public static void registerItems(){
        LOGGER.info("Registering items...");
    }

}
