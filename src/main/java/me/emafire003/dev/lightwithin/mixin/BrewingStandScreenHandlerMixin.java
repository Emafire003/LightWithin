package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.items.crafting.CustomBrewRecipeRegister;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.BrewingStandScreenHandler$PotionSlot")
public abstract class BrewingStandScreenHandlerMixin {

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private static void injectMatchesCustom(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if(CustomBrewRecipeRegister.isValidCustomInput(stack)){
            cir.setReturnValue(true);
        }
    }
}