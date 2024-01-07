package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.events.AllyDeathEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class AllyDeathMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void injectOnAttacking(DamageSource damageSource, CallbackInfo ci) {
        AllyDeathEvent.EVENT.invoker().dead(((LivingEntity) (Object) this), damageSource);
    }

}
