package me.emafire003.dev.lightwithin.items;

import me.emafire003.dev.lightwithin.LightWithin;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

public class LightItems {

    public static final Item LUXINTUS_BERRY = registerItem("luxintus_berry",
            new LuxintusBerryItem(new FabricItemSettings().rarity(Rarity.RARE)
                    .food(new FoodComponent.Builder().alwaysEdible().hunger(5).build()).maxCount(16).group(ItemGroup.FOOD)));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registry.ITEM, new Identifier(LightWithin.MOD_ID, name), item);
    }

    public static void registerItems(){
        LOGGER.info("Registering items...");
    }

}
