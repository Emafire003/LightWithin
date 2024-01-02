package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class FreezeEntityLookMixin {

    @Inject(
            method = "changeLookDirection",
            at = @At("HEAD"),
            cancellable = true
    )
    private void stopMoving(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if(entity instanceof LivingEntity){
            if(((LivingEntity) entity).hasStatusEffect(LightEffects.FROST) && !((LivingEntity) entity).hasStatusEffect(LightEffects.FREEZE_RESISTANCE)){
                ci.cancel();
            }

        }

    }

}
