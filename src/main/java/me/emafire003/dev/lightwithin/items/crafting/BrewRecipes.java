package me.emafire003.dev.lightwithin.items.crafting;

import me.emafire003.dev.lightwithin.items.LightItems;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class BrewRecipes {
    /*
    * boolean input_ok = false;
            if(recipe.input().equals(input.getItem()) && input.hasNbt()){
                CustomBrewRecipeRegister.CustomRecipe<NbtCompound> nbt_recipe = CustomBrewRecipeRegister.getCustomRecipesNBTMap().get(recipe);
                if(nbt_recipe.input().equals(input.getNbt())){
                    input_ok = true;
                }
            }
            if(input_ok && recipe.ingredient().equals(ingredient.getItem()) && ingredient.hasNbt()){
                CustomBrewRecipeRegister.CustomRecipe<NbtCompound> nbt_recipe = CustomBrewRecipeRegister.getCustomRecipesNBTMap().get(recipe);
                if(nbt_recipe.ingredient().equals(ingredient.getNbt())){
                    ItemStack out = new ItemStack(recipe.output());
                    out.setNbt(nbt_recipe.output());
                    LightWithin.LOGGER.info("Outputting: " + out);
                    cir.setReturnValue(out);
                    return;
                }
            }*/

    public static void registerRecipes(){
        //Fact is, now it's going to need nbt too.
        //BrewingRecipeRegistry.registerItemRecipe(Items.EXPERIENCE_BOTTLE, LightItems.LUXINTUS_BERRY_POWDER, LightItems.BOTTLED_LIGHT);
        CustomBrewRecipeRegister.registerCustomRecipe(Items.EXPERIENCE_BOTTLE, LightItems.LUXINTUS_BERRY_POWDER, LightItems.BOTTLED_LIGHT);
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("lightwithin:playerUUID", UUID.fromString("00000000-1111-0000-0000-000000000000"));
        CustomBrewRecipeRegister.registerCustomRecipeNbt(Items.DRAGON_BREATH, LightItems.LUXINTUS_BERRY_POWDER, LightItems.BOTTLED_LIGHT, null, null, nbt);
    }
}
