package me.emafire003.dev.lightwithin.items.crafting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomBrewRecipeRegister {

    private static final List<CustomRecipe<Item>> CUSTOM_RECIPES = new ArrayList<>();
    private static final HashMap<CustomRecipe<Item>, CustomRecipe<NbtCompound>> CUSTOM_RECIPES_NBT_MAP = new HashMap<>();


    /**Use this method to register new recipes using custom items!
     *
     * @param input The input item, the "base" item like a water_bottle for normal recipes
     * @param ingredient The ingredient item, like spider's eye, glowstone dust ecc
     * @param output The output item, the one that will result from this recipe
     * */
    public static void registerCustomRecipe(Item input, Item ingredient, Item output) {
        CUSTOM_RECIPES.add(new CustomRecipe<>(input, ingredient, output));
    }

    /**Use this method to register new recipes using custom items.
     * This also supports nbt!
     *
     * @param input The input item, the "base" item like a water_bottle for normal recipes
     * @param ingredient The ingredient item, like spider's eye, glowstone dust ecc
     * @param output The output item, the one that will result from this recipe
     * @param input_nbt An NBT compound that will be attached to the input item. Use null if you don't want to add NBT to this item
     * @param ingredient_nbt An NBT compound that will be attached to the ingredient item. Use null if you don't want to add NBT to this item
     * @param output_nbt An NBT compound that will be attached to the output item. Use null if you don't want to add NBT to this item
     * */
    public static void registerCustomRecipeNbt(Item input, Item ingredient, Item output, @Nullable NbtCompound input_nbt, @Nullable NbtCompound ingredient_nbt, @Nullable NbtCompound output_nbt) {
        CUSTOM_RECIPES_NBT_MAP.put(new CustomRecipe<>(input, ingredient, output), new CustomRecipe<>(input_nbt, ingredient_nbt, output_nbt));
    }

    /**Use this method to register new recipes using custom items!
     * This also supports nbt!
     *
     * @param input The input itemstack, the "base" item like a water_bottle for normal recipes. This should already have NBT set by you.
     * @param ingredient The ingredient itemstack, like spider's eye, glowstone dust ecc. This should already have NBT set by you.
     * @param output The output itemstack, the one that will result from this recipe. This should already have NBT set by you.
     * */
    public static void registerCustomRecipeNbt(ItemStack input, ItemStack ingredient, ItemStack output) {
        CUSTOM_RECIPES_NBT_MAP.put(new CustomRecipe<>(input.getItem(), ingredient.getItem(), output.getItem()), new CustomRecipe<>(input.getNbt(), ingredient.getNbt(), output.getNbt()));
    }

    /**Used to check if an item is a valid input/base, like
     * a bottle or an akward potion.
     * Also checks for nbt if necessary.
     *
     * @param item The item to test as a valid input
     * @returns true if the item is a valid input*/
    public static boolean isValidCustomInput(ItemStack item){
        for(CustomRecipe<Item> recipe : CUSTOM_RECIPES){
            if(recipe.input.equals(item.getItem())){
                return true;
            }
        }
        for(CustomRecipe<Item> recipe : CUSTOM_RECIPES_NBT_MAP.keySet()){
            if(equalsNbt(item, recipe.input, CUSTOM_RECIPES_NBT_MAP.get(recipe).input)){
                return true;
            }
        }
        return false;
    }

    /**Checks if two itemstacks have the same NBT. Or not, if they shouldn't have any NBT data*/
    public static boolean equalsNbt(ItemStack recipe_item, ItemStack item){

        if(!recipe_item.hasNbt() && !item.hasNbt()){
            return recipe_item.isOf(item.getItem());
        }

        return recipe_item.isOf(item.getItem()) && recipe_item.hasNbt() && item.hasNbt() && recipe_item.getNbt().equals(item.getNbt());
    }

    /**Used to check if an itemstack and an item share the same NBT data.
     * This also applies to not having nbt data.*/
    public static boolean equalsNbt(ItemStack item, Item recipe_item, @Nullable NbtCompound nbt){

        if(!item.hasNbt() && nbt == null){
            return item.isOf(recipe_item);
        }
        if(nbt == null){
            return false;
        }

        if(nbt.equals(item.getNbt())){
            return item.isOf(recipe_item);
        }
        return false;
    }

    public static List<CustomRecipe<Item>> getCustomRecipes(){
        return CUSTOM_RECIPES;
    }

    public static HashMap<CustomRecipe<Item>, CustomRecipe<NbtCompound>> getCustomRecipesNBTMap(){
        return CUSTOM_RECIPES_NBT_MAP;
    }

    public record CustomRecipe<T>(T input, T ingredient, T output) {
    }
}
