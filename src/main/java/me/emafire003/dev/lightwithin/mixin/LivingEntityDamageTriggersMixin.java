package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.events.EntityAttackEntityEvent;
import me.emafire003.dev.lightwithin.events.EntityBurningEvent;
import me.emafire003.dev.lightwithin.events.EntityDrowningEvent;
import me.emafire003.dev.lightwithin.events.EntityFreezingEvent;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageTriggersMixin {

    @Inject(
            method = "damage",
            at = @At("HEAD")
    )
    private void damageTriggers(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (source.getName().equalsIgnoreCase("freeze") && !entity.hasStatusEffect(LightEffects.FREEZE_RESISTANCE)) {
            EntityFreezingEvent.EVENT.invoker().freezing(entity);
        }else if(source.isFire()){
            EntityBurningEvent.EVENT.invoker().burning(entity);
        }else if(source.isExplosive()) {
            Entity attacker = source.getAttacker();
            if (attacker instanceof LivingEntity) {
                EntityAttackEntityEvent.EVENT.invoker().attack((LivingEntity) attacker, entity);
            }
        }else if(source.isFallingBlock()){
            //trigger anvil damage
        }else if(source.isMagic()){
            //trigger magical damage
        }else if(source.isProjectile()) {
                //trigger projectile damage
        }else if(source.equals(DamageSource.DROWN)){
            EntityDrowningEvent.EVENT.invoker().drowning(entity);
        }
    }
}
