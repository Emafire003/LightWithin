package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.events.EntityAttackEntityEvent;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class EntityAttackEntityMixin{

    @Inject(method = "onAttacking", at = @At("HEAD"))
    public void injectOnAttacking(Entity target, CallbackInfo ci) {
        //for the freeze effect, i didn't want to modify the same method twice
        if(((LivingEntity) (Object) this).hasStatusEffect(LightEffects.FROST)){
            ci.cancel();
        }
        //for the actual event
        if (target instanceof LivingEntity) {
            EntityAttackEntityEvent.EVENT.invoker().attack(((LivingEntity)(Object)this), (LivingEntity) target);
        }
    }

    //other stuff that i need in the entity class to prevent some errors not related to the entity attack entity
    @Inject(method = "clearStatusEffects", at = @At("HEAD"))
    public void clearLightFatigueToo(CallbackInfoReturnable<Boolean> cir){
        if(((LivingEntity)(Object)this).hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
            ((LivingEntity)(Object)this).removeStatusEffect(LightEffects.LIGHT_ACTIVE);
        }
        if(((LivingEntity)(Object)this).hasStatusEffect(LightEffects.LIGHT_FATIGUE)){
            ((LivingEntity)(Object)this).removeStatusEffect(LightEffects.LIGHT_FATIGUE);
        }
    }

    //Other stuff, AKA depth strider for AQUA self/allies
    @ModifyVariable(method = "travel", at = @At("STORE"), ordinal = 2)
    public float applyWaterSpeedAqua(float h){
        if(h == 0 && ((LivingEntity)(Object)this).hasStatusEffect(LightEffects.WATER_SLIDE)){
            h = 0.7f;
        }
        return h;
    }

}
