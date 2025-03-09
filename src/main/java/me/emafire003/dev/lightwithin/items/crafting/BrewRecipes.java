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

    public static final String TYPE_INGREDIENT_KEY = "lightwithin:typeIngredient";
    public static final String TARGET_INGREDIENT_KEY = "lightwithin:targetIngredient";
    public static final String PLAYER_NBT_KEY = "lightwithin:playerUUID";

    public static void registerRecipes(){
        //Fact is, now it's going to need nbt too.
        //BrewingRecipeRegistry.registerItemRecipe(Items.EXPERIENCE_BOTTLE, LightItems.LUXINTUS_BERRY_POWDER, LightItems.BOTTLED_LIGHT);
        NbtCompound nbt = new NbtCompound();
        //This means it's artificial
        nbt.putUuid(PLAYER_NBT_KEY, UUID.fromString("00000000-0000-0000-0000-000000000000"));
        CustomBrewRecipeRegister.registerCustomRecipeNbt(Items.EXPERIENCE_BOTTLE, LightItems.LUXINTUS_BERRY_POWDER, LightItems.BOTTLED_LIGHT, null, null, nbt);

        NbtCompound light_nbt = nbt.copy();
        light_nbt.putString(TYPE_INGREDIENT_KEY, InnerLightType.AQUA.name());
        CustomBrewRecipeRegister.registerCustomRecipeNbt(LightItems.BOTTLED_LIGHT, AquaLight.INGREDIENT, LightItems.BOTTLED_LIGHT, nbt, null, light_nbt);

        registerTargetsForType(InnerLightType.AQUA, light_nbt);

        light_nbt = nbt.copy();
        light_nbt.putString(TYPE_INGREDIENT_KEY, InnerLightType.BLAZING.name());
        CustomBrewRecipeRegister.registerCustomRecipeNbt(LightItems.BOTTLED_LIGHT, BlazingLight.INGREDIENT, LightItems.BOTTLED_LIGHT, nbt, null, light_nbt);

        registerTargetsForType(InnerLightType.BLAZING, light_nbt);

        light_nbt = nbt.copy();
        light_nbt.putString(TYPE_INGREDIENT_KEY, InnerLightType.FROST.name());
        CustomBrewRecipeRegister.registerCustomRecipeNbt(LightItems.BOTTLED_LIGHT, FrostLight.INGREDIENT, LightItems.BOTTLED_LIGHT, nbt, null, light_nbt);

        registerTargetsForType(InnerLightType.FROST, light_nbt);

        light_nbt = nbt.copy();
        light_nbt.putString(TYPE_INGREDIENT_KEY, InnerLightType.DEFENCE.name());
        CustomBrewRecipeRegister.registerCustomRecipeNbt(LightItems.BOTTLED_LIGHT, DefenseLight.INGREDIENT, LightItems.BOTTLED_LIGHT, nbt, null, light_nbt);

        registerTargetsForType(InnerLightType.DEFENCE, light_nbt);

        light_nbt = nbt.copy();
        light_nbt.putString(TYPE_INGREDIENT_KEY, InnerLightType.EARTHEN.name());
        CustomBrewRecipeRegister.registerCustomRecipeNbt(LightItems.BOTTLED_LIGHT, EarthenLight.INGREDIENT, LightItems.BOTTLED_LIGHT, nbt, null, light_nbt);

        registerTargetsForType(InnerLightType.EARTHEN, light_nbt);

        light_nbt = nbt.copy();
        light_nbt.putString(TYPE_INGREDIENT_KEY, InnerLightType.FOREST_AURA.name());
        CustomBrewRecipeRegister.registerCustomRecipeNbt(LightItems.BOTTLED_LIGHT, ForestAuraLight.INGREDIENT, LightItems.BOTTLED_LIGHT, nbt, null, light_nbt);
        registerTargetsForType(InnerLightType.FOREST_AURA, light_nbt);

        light_nbt = nbt.copy();
        light_nbt.putString(TYPE_INGREDIENT_KEY, InnerLightType.THUNDER_AURA.name());
        CustomBrewRecipeRegister.registerCustomRecipeNbt(LightItems.BOTTLED_LIGHT, ThunderAuraLight.INGREDIENT, LightItems.BOTTLED_LIGHT, nbt, null, light_nbt);
        registerTargetsForType(InnerLightType.THUNDER_AURA, light_nbt);


        for(Item frog_item : FrogLight.INGREDIENTS){
            light_nbt = nbt.copy();
            light_nbt.putString(TYPE_INGREDIENT_KEY, InnerLightType.FROG.name());
            CustomBrewRecipeRegister.registerCustomRecipeNbt(LightItems.BOTTLED_LIGHT, frog_item, LightItems.BOTTLED_LIGHT, nbt, null, light_nbt);

            registerTargetsForType(InnerLightType.FROG, light_nbt);
        }

        light_nbt = nbt.copy();
        light_nbt.putString(TYPE_INGREDIENT_KEY, InnerLightType.HEAL.name());
        CustomBrewRecipeRegister.registerCustomRecipeNbt(LightItems.BOTTLED_LIGHT, HealLight.INGREDIENT, LightItems.BOTTLED_LIGHT, nbt, null, light_nbt);

        registerTargetsForType(InnerLightType.HEAL, light_nbt);

        light_nbt = nbt.copy();
        light_nbt.putString(TYPE_INGREDIENT_KEY, InnerLightType.STRENGTH.name());
        CustomBrewRecipeRegister.registerCustomRecipeNbt(LightItems.BOTTLED_LIGHT, StrengthLight.INGREDIENT, LightItems.BOTTLED_LIGHT, nbt, null, light_nbt);

        registerTargetsForType(InnerLightType.STRENGTH, light_nbt);

        light_nbt = nbt.copy();
        light_nbt.putString(TYPE_INGREDIENT_KEY, InnerLightType.WIND.name());
        CustomBrewRecipeRegister.registerCustomRecipeNbt(LightItems.BOTTLED_LIGHT, WindLight.INGREDIENT, LightItems.BOTTLED_LIGHT, nbt, null, light_nbt);

        registerTargetsForType(InnerLightType.WIND, light_nbt);
    }

    public static final Map<TargetType, Item> TARGET_ITEMS = Map.ofEntries(
            entry(TargetType.SELF, Items.APPLE),
            entry(TargetType.ALLIES, Items.GLOW_BERRIES),
            entry(TargetType.ENEMIES, Items.NETHER_WART),
            entry(TargetType.ALL, Items.ALLIUM),
            entry(TargetType.VARIANT, Items.ECHO_SHARD)
    );


    public static void registerTargetsForType(InnerLightType type, NbtCompound base_nbt){
        LightWithin.POSSIBLE_TARGETS.get(type).forEach(targetType -> {
            NbtCompound target_nbt = base_nbt.copy();
            target_nbt.putString(TARGET_INGREDIENT_KEY, targetType.name());
            CustomBrewRecipeRegister.registerCustomRecipeNbtField(LightItems.BOTTLED_LIGHT, TARGET_ITEMS.get(targetType), LightItems.BOTTLED_LIGHT, TYPE_INGREDIENT_KEY, NbtString.of(type.name()), null, null, target_nbt);
        });
    }


}
