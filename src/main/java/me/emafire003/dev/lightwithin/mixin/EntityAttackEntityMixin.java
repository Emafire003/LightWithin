package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.events.EntityAttackEntityEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class EntityAttackEntityMixin{

    @Inject(method = "onAttacking", at = @At("HEAD"))
    public void injectOnAttacking(Entity target, CallbackInfo ci) {
        if (target instanceof LivingEntity) {
            EntityAttackEntityEvent.EVENT.invoker().attack(((LivingEntity)(Object)this), (LivingEntity) target);
        }
    }
}
