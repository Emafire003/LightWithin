package me.emafire003.dev.lightwithin.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import me.emafire003.dev.lightwithin.util.IPreventLightEffectClearEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(LivingEntity.class)
public abstract class PreventLightEffectClearLivingEntityMixin extends Entity implements IPreventLightEffectClearEntity {

    public PreventLightEffectClearLivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    boolean hasDrunkMilk = false;
    @Unique
    boolean clearNextEffect = true;

    @Unique
    @Override
    public boolean lightWithin$getHasDrunkMilk(){
        return hasDrunkMilk;
    }

    @Unique
    @Override
    public void lightWithin$setHasDrunkMilk(boolean b){
        hasDrunkMilk = b;
    }

    @WrapWithCondition(method = "clearStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onStatusEffectRemoved(Lnet/minecraft/entity/effect/StatusEffectInstance;)V"))
    public boolean shouldClearEffect(LivingEntity instance, StatusEffectInstance effect) {
        if(this.lightWithin$getHasDrunkMilk()){
            StatusEffect type = effect.getEffectType();
            if(type.equals(LightEffects.LIGHT_ACTIVE) || type.equals(LightEffects.LIGHT_FATIGUE) || type.equals(LightEffects.LUXCOGNITA_OFFENDED)){
                clearNextEffect = false;
                return false;
            }

        }
        clearNextEffect = true;
        return true;
    }


    @WrapWithCondition(method = "clearStatusEffects", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V"))
    public boolean shouldRemove(Iterator<StatusEffectInstance> instance) {
        return clearNextEffect;
    }

    @Inject(method = "clearStatusEffects", at = @At("RETURN"))
    public void reset(CallbackInfoReturnable<Boolean> cir) {
        clearNextEffect = true;
        this.lightWithin$setHasDrunkMilk(false);
    }

}
