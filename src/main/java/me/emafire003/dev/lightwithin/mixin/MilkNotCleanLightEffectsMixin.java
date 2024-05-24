package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.status_effects.LightActiveEffect;
import me.emafire003.dev.lightwithin.status_effects.LightFatigueEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ConcurrentModificationException;

import static me.emafire003.dev.lightwithin.LightWithin.LOGGER;

@Mixin(MilkBucketItem.class)
public abstract class MilkNotCleanLightEffectsMixin extends Item {

    public MilkNotCleanLightEffectsMixin(Settings settings) {
        super(settings);
    }

    //TODO this now completely prevents removing the thing, even with the command
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;clearStatusEffects()Z"), method = "finishUsing")
    public boolean clearStatusExceptLights(LivingEntity instance){
        try{
            if (instance.getWorld().isClient) {
                return false;
            } else {
                for(StatusEffectInstance status : instance.getActiveStatusEffects().values()){
                    RegistryEntry<StatusEffect> type = status.getEffectType();
                    if(!(type instanceof LightActiveEffect || type instanceof LightFatigueEffect)){
                        instance.removeStatusEffect(type);
                    }
                }
                return true;
            }

        }catch (ConcurrentModificationException e){
            LOGGER.warn("There has been an issue while removing a status effect with a milk bucket, probably nothing to worry about");
            e.printStackTrace();
            return true;
        }
    }
}
