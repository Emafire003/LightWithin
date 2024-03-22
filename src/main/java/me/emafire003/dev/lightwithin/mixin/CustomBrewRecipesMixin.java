package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.items.crafting.CustomBrewRecipeRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export=true)
@Mixin(BrewingRecipeRegistry.class)
public abstract class CustomBrewRecipesMixin{
    @Inject(method = "craft", at = @At(value = "TAIL"), cancellable = true)
    private static void customCraftInject(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir){
        if(input == null || ingredient == null){
            return;
        }

        for(CustomBrewRecipeRegister.CustomRecipe<Item> recipe : CustomBrewRecipeRegister.getCustomRecipes()) {
            if (recipe.input().equals(input.getItem()) && recipe.ingredient().equals(ingredient.getItem())) {
                cir.setReturnValue(new ItemStack(recipe.output()));
                return;
            }
        }

        for(CustomBrewRecipeRegister.CustomRecipe<Item> recipe : CustomBrewRecipeRegister.getCustomRecipesNBTMap().keySet()){
            CustomBrewRecipeRegister.CustomRecipe<NbtCompound> nbt_recipe = CustomBrewRecipeRegister.getCustomRecipesNBTMap().get(recipe);
            if(CustomBrewRecipeRegister.equalsNbt(ingredient, recipe.ingredient(), nbt_recipe.ingredient())
                    && CustomBrewRecipeRegister.equalsNbt(input, recipe.input(), nbt_recipe.input())
            ){
                ItemStack out = new ItemStack(recipe.output());
                out.setNbt(nbt_recipe.output());
                cir.setReturnValue(out);
                return;
            }
        }
    }

    @Inject(method = "hasRecipe", at = @At(value = "HEAD"), cancellable = true)
    private static void injectHasRecipeCustom(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir){
        if(hasCustomRecipe(input, ingredient)){
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isValidIngredient", at = @At(value = "HEAD"), cancellable = true)
    private static void injectIsValidIngredientCustom(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if(isCustomRecipeIngredient(stack)){
            cir.setReturnValue(true);
        }
    }

    @Unique
    private static boolean hasCustomRecipe(ItemStack input, ItemStack ingredient) {
        for(CustomBrewRecipeRegister.CustomRecipe<Item> recipe : CustomBrewRecipeRegister.getCustomRecipes()) {
            if (recipe.input().equals(input.getItem()) && recipe.ingredient().equals(ingredient.getItem())) {
                return true;
            }
        }
        for(CustomBrewRecipeRegister.CustomRecipe<Item> recipe : CustomBrewRecipeRegister.getCustomRecipesNBTMap().keySet()){
            if(CustomBrewRecipeRegister.equalsNbt(ingredient, recipe.ingredient(), CustomBrewRecipeRegister.getCustomRecipesNBTMap().get(recipe).ingredient())
                    && CustomBrewRecipeRegister.equalsNbt(input, recipe.input(), CustomBrewRecipeRegister.getCustomRecipesNBTMap().get(recipe).input())
            ){
                return true;
            }
        }

        return false;
    }

    @Unique
    private static boolean isCustomRecipeIngredient(ItemStack stack) {
        for(CustomBrewRecipeRegister.CustomRecipe<Item> recipe : CustomBrewRecipeRegister.getCustomRecipes()) {
            if (recipe.ingredient() == stack.getItem()) {
                return true;
            }
        }
        for(CustomBrewRecipeRegister.CustomRecipe<Item> recipe : CustomBrewRecipeRegister.getCustomRecipesNBTMap().keySet()){
            if(CustomBrewRecipeRegister.equalsNbt(stack, recipe.ingredient(), CustomBrewRecipeRegister.getCustomRecipesNBTMap().get(recipe).ingredient())
            ){
                return true;
            }
        }
        return false;
    }


}
