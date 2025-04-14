package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class LuxcognitaDreamNoNudgingMixin {

    @Inject(
            method = "collidesWith",
            at = @At("HEAD"),
            cancellable = true)
    private void damageTaken(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if(((Entity) (Object) this) instanceof PlayerEntity){
            if(((PlayerEntity) (Object) this).hasStatusEffect(LightEffects.LUXCOGNITA_DREAM)){
                cir.setReturnValue(false);
            }
        }
    }
}
