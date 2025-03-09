package me.emafire003.dev.lightwithin.mixin.light_triggers;

import me.emafire003.dev.lightwithin.events.EntityAttackEntityEvent;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class EntityAttackEntityMixin{

    @Inject(method = "onAttacking", at = @At("HEAD"), cancellable = true)
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

    //Other stuff, AKA depth strider for AQUA self/allies
    @ModifyVariable(method = "travel", at = @At("STORE"), ordinal = 2)
    public float applyWaterSpeedAqua(float h){
        //noinspection ReassignedVariable
        if(h == 0 && ((LivingEntity)(Object)this).hasStatusEffect(LightEffects.WATER_SLIDE)){
            h = 0.7f;
        }
        return h;
    }

}
