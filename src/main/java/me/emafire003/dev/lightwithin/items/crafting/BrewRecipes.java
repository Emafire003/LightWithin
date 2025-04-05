package me.emafire003.dev.lightwithin.items.crafting;

import me.emafire003.dev.custombrewrecipes.CustomBrewRecipeRegister;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.items.components.LightItemComponents;
import me.emafire003.dev.lightwithin.lights.*;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Map;
import java.util.UUID;

import static java.util.Map.entry;

public class BrewRecipes {

    public static final String TYPE_INGREDIENT_KEY = LightWithin.MOD_ID+":typeIngredient";
    public static final String TARGET_INGREDIENT_KEY = LightWithin.MOD_ID+":targetIngredient";
    public static final String PLAYER_NBT_KEY = LightWithin.MOD_ID+":playerUUID";

    private static final ComponentMap playerUUIDComponent = ComponentMap.builder().add(LightItemComponents.BOTTLED_LIGHT_PLAYER_UUID, UUID.fromString("00000000-0000-0000-0000-000000000000")).build();

    public static void registerRecipes(){
        //This means it's artificial

        CustomBrewRecipeRegister.registerCustomRecipeWithComponents(Items.EXPERIENCE_BOTTLE, LightItems.LUXINTUS_BERRY_POWDER, LightItems.BOTTLED_LIGHT, null, null, playerUUIDComponent);
        //CustomBrewRecipeRegister.registerCustomRecipeNbt(LightItems.BOTTLED_LIGHT, AquaLight.INGREDIENT, LightItems.BOTTLED_LIGHT, nbt, null, light_nbt);


        LightWithin.INNERLIGHT_REGISTRY.forEach(innerLight -> {
            if(innerLight instanceof NoneLight){
                return;
            }
            if(innerLight instanceof FrogLight){
                for(Item frog_item : FrogLight.INGREDIENTS){
                    registerType(innerLight, frog_item);
                }
            }
            registerType(innerLight, innerLight.getIngredient());
        });

    }

    public static final Map<TargetType, Item> TARGET_ITEMS = Map.ofEntries(
            entry(TargetType.SELF, Items.APPLE),
            entry(TargetType.ALLIES, Items.GLOW_BERRIES),
            entry(TargetType.ENEMIES, Items.NETHER_WART),
            entry(TargetType.ALL, Items.ALLIUM),
            entry(TargetType.VARIANT, Items.ECHO_SHARD)
    );

    private static void registerType(InnerLight type, Item ingredient){
        ComponentMap lightTypeComponent = ComponentMap.builder().addAll(playerUUIDComponent).add(LightItemComponents.BOTTLED_LIGHT_TYPE_INGREDIENT, type.getLightId().toString()).build();
        CustomBrewRecipeRegister.registerCustomRecipeWithComponents(LightItems.BOTTLED_LIGHT, ingredient, LightItems.BOTTLED_LIGHT, playerUUIDComponent, null, lightTypeComponent);

        registerTargetsForType(type, lightTypeComponent);
    }


    public static void registerTargetsForType(InnerLight type, ComponentMap baseComponents){

        type.getPossibleTargetTypes().forEach( targetType -> {
            ComponentMap targetComponent = ComponentMap.builder().addAll(baseComponents).add(LightItemComponents.BOTTLED_LIGHT_TARGET_INGREDIENT, targetType.name()).build();
            //CustomBrewRecipeRegister.registerCustomRecipeNbtField(LightItems.BOTTLED_LIGHT, TARGET_ITEMS.get(targetType), LightItems.BOTTLED_LIGHT, TYPE_INGREDIENT_KEY, NbtString.of(type.name()), null, null, target_nbt);
            CustomBrewRecipeRegister.registerCustomRecipeWithComponentType(LightItems.BOTTLED_LIGHT, TARGET_ITEMS.get(targetType), LightItems.BOTTLED_LIGHT, LightItemComponents.BOTTLED_LIGHT_TYPE_INGREDIENT, type.getLightId().toString(), null, null, targetComponent);
        });
    }


}
