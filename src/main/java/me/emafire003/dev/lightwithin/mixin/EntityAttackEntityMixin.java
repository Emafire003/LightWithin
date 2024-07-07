package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.events.EntityAttackEntityEvent;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class EntityAttackEntityMixin extends Entity{

    public EntityAttackEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onAttacking", at = @At("HEAD"), cancellable = true)
    public void injectOnAttacking(Entity target, CallbackInfo ci) {
        //for the freeze effect, I didn't want to modify the same method twice
        if(((LivingEntity) (Object) this).hasStatusEffect(LightEffects.FROST)){
            ci.cancel();
        }
        //for the actual event
        if (target instanceof LivingEntity) {
            EntityAttackEntityEvent.EVENT.invoker().attack(((LivingEntity)(Object)this), (LivingEntity) target);
        }
    }

    //Other stuff, AKA depth strider for AQUA self/allies
    @SuppressWarnings("all")
    @ModifyVariable(method = "travel", at = @At("STORE"), ordinal = 2)
    public float applyWaterSpeedAqua(float h){
        if(h == 0 && ((LivingEntity)(Object)this).hasStatusEffect(LightEffects.WATER_SLIDE)){
            h = 0.7f;
        }
        return h;
    }

}
