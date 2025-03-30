package me.emafire003.dev.lightwithin.items.crafting;

import me.emafire003.dev.custombrewrecipes.CustomBrewRecipeRegister;
import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.items.LightItems;
import me.emafire003.dev.lightwithin.lights.*;
import me.emafire003.dev.lightwithin.util.TargetType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;

import java.util.*;

import static java.util.Map.entry;

public class BrewRecipes {

    public static final String TYPE_INGREDIENT_KEY = LightWithin.MOD_ID+":typeIngredient";
    public static final String TARGET_INGREDIENT_KEY = LightWithin.MOD_ID+":targetIngredient";
    public static final String PLAYER_NBT_KEY = LightWithin.MOD_ID+":playerUUID";

    public static void registerRecipes(){
        NbtCompound nbt = new NbtCompound();
        /// This means it's artificial
        nbt.putUuid(PLAYER_NBT_KEY, UUID.fromString("00000000-0000-0000-0000-000000000000"));
        CustomBrewRecipeRegister.registerCustomRecipeNbt(Items.EXPERIENCE_BOTTLE, LightItems.LUXINTUS_BERRY_POWDER, LightItems.BOTTLED_LIGHT, null, null, nbt);

        LightWithin.INNERLIGHT_REGISTRY.stream().forEach(innerLight -> {
            if(innerLight instanceof NoneLight){
                return;
            }
            NbtCompound light_nbt = nbt.copy();

            if(innerLight instanceof FrogLight){
                for(Item frog_item : FrogLight.INGREDIENTS){
                    light_nbt = nbt.copy();
                    light_nbt.putString(TYPE_INGREDIENT_KEY, InnerLightTypes.FROG.getLightId().toString());
                    CustomBrewRecipeRegister.registerCustomRecipeNbt(LightItems.BOTTLED_LIGHT, frog_item, LightItems.BOTTLED_LIGHT, nbt, null, light_nbt);

                    registerTargetsForType(innerLight, light_nbt);
                }
            }

            light_nbt.putString(TYPE_INGREDIENT_KEY, innerLight.getLightId().toString());
            CustomBrewRecipeRegister.registerCustomRecipeNbt(LightItems.BOTTLED_LIGHT, innerLight.INGREDIENT, LightItems.BOTTLED_LIGHT, nbt, null, light_nbt);
            registerTargetsForType(innerLight, light_nbt);
        });
    }

    public static final Map<TargetType, Item> TARGET_ITEMS = Map.ofEntries(
            entry(TargetType.SELF, Items.APPLE),
            entry(TargetType.ALLIES, Items.GLOW_BERRIES),
            entry(TargetType.ENEMIES, Items.NETHER_WART),
            entry(TargetType.ALL, Items.ALLIUM),
            entry(TargetType.VARIANT, Items.ECHO_SHARD)
    );

    public static void registerTargetsForType(InnerLight type, NbtCompound base_nbt){
        type.getPossibleTargetTypes().forEach(targetType -> {
            NbtCompound target_nbt = base_nbt.copy();
            target_nbt.putString(TARGET_INGREDIENT_KEY, targetType.name());
            CustomBrewRecipeRegister.registerCustomRecipeNbtField(LightItems.BOTTLED_LIGHT, TARGET_ITEMS.get(targetType), LightItems.BOTTLED_LIGHT, TYPE_INGREDIENT_KEY, NbtString.of(type.getLightId().toString()), null, null, target_nbt);
        });
    }


}
