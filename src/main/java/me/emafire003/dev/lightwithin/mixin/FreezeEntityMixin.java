package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class FreezeEntityMixin {

    //This is used by the Frost status effect, not the trigger
    
    @Inject(
            method = "isImmobile",
            at = @At("HEAD"),
            cancellable = true
    )
    private void stopMoving(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasStatusEffect(LightEffects.FROST) && !entity.hasStatusEffect(LightEffects.FREEZE_RESISTANCE)) {
            cir.setReturnValue(true);
        }
    }


    @Inject(
            method = "shouldRenderName",
            at = @At("HEAD"),
            cancellable = true
    )
    private void stopRenderName(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasStatusEffect(LightEffects.FROST) && !entity.hasStatusEffect(LightEffects.FREEZE_RESISTANCE)) {
            cir.setReturnValue(false);
        }
    }


    @Inject(
            method = "getHandSwingDuration",
            at = @At("HEAD"),
            cancellable = true
    )
    private void stopSwing(CallbackInfoReturnable<Integer> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if(entity.hasStatusEffect(LightEffects.FROST) && !entity.hasStatusEffect(LightEffects.FREEZE_RESISTANCE)){
            cir.setReturnValue(300000);
        }
    }

    @Inject(
            method = "isPushable",
            at = @At("HEAD"),
            cancellable = true
    )
    private void stopPushable(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasStatusEffect(LightEffects.FROST) && !entity.hasStatusEffect(LightEffects.FREEZE_RESISTANCE)) {
            cir.setReturnValue(false);
        }
    }

}
