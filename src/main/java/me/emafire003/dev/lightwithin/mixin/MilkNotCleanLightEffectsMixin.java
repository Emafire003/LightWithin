package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.util.IPreventLightEffectClearEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MilkBucketItem.class)
public abstract class MilkNotCleanLightEffectsMixin extends Item {

    public MilkNotCleanLightEffectsMixin(Settings settings) {
        super(settings);
    }

    //@Inject(method = "finishUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;clearStatusEffects()Z"))
    @Inject(method = "finishUsing", at = @At(value = "HEAD"))
    public void setDrinkingMilk(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir){
        IPreventLightEffectClearEntity entity = (IPreventLightEffectClearEntity) user;
        entity.lightWithin$setHasDrunkMilk(true);
    }
}

