package me.emafire003.dev.lightwithin.items;

import me.emafire003.dev.lightwithin.LightWithin;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class LightItems {

    public static final Item LUXINTUS_BERRY = registerItem("luxintus_berry",
            new LuxintusBerryItem(new FabricItemSettings().rarity(Rarity.EPIC)
                    .food(new FoodComponent.Builder().alwaysEdible().hunger(5).build()).maxCount(16)),
            ItemGroups.FOOD_AND_DRINK, Items.GLOW_BERRIES);

    public static final Item LUXCOGNITA_BERRY = registerItem("luxcognita_berry",
            new LuxcognitaBerryItem(new FabricItemSettings().rarity(Rarity.RARE)
                    .food(new FoodComponent.Builder().alwaysEdible().hunger(5).build()).maxCount(16)),
            ItemGroups.FOOD_AND_DRINK, Items.GLOW_BERRIES);

    public static final Item LUXMUTUA_BERRY = registerItem("luxmutua_berry",
            new LuxmutuaBerryItem(new FabricItemSettings().rarity(Rarity.EPIC)
                    .food(new FoodComponent.Builder().alwaysEdible().hunger(5).build()).maxCount(16)),
            ItemGroups.FOOD_AND_DRINK, Items.GLOW_BERRIES);

    /*public static final Item TEST_BERRY = registerItem("test_berry",
            new LuxmutuaTestBerryItem(new FabricItemSettings().rarity(Rarity.EPIC)
                    .food(new FoodComponent.Builder().alwaysEdible().hunger(5).build()).maxCount(16).group(ItemGroup.FOOD)));
*/

    private static Item registerItem(String name, Item item, RegistryKey<ItemGroup> group, Item add_after){
        ItemGroupEvents.modifyEntriesEvent(group).register(content -> {
            content.addAfter(Items.GLOW_BERRIES, item);
        });
        return Registry.register(Registries.ITEM, new Identifier(LightWithin.MOD_ID, name), item);
    }

    private static Item registerItemBerry(String name, Item item){

        return Registry.register(Registries.ITEM, new Identifier(LightWithin.MOD_ID, name), item);
    }

    public static void registerItems(){
        LOGGER.info("Registering items...");
    }

}
