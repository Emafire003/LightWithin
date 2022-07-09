package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class FreezeEntityMixin {
    @Inject(
            method = "isImmobile",
            at = @At("HEAD"),
            cancellable = true
    )
    private void stopMoving(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasStatusEffect(LightEffects.FROST)) {
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
        if (entity.hasStatusEffect(LightEffects.FROST)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "collides",
            at = @At("HEAD"),
            cancellable = true
    )
    private void stopColliding(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasStatusEffect(LightEffects.FROST)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "isPushable",
            at = @At("HEAD"),
            cancellable = true
    )
    private void stopPushable(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasStatusEffect(LightEffects.FROST)) {
            cir.setReturnValue(false);
        }
    }

}
