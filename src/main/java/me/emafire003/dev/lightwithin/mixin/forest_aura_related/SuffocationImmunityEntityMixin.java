package me.emafire003.dev.lightwithin.mixin.forest_aura_related;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class SuffocationImmunityEntityMixin {

    @ModifyExpressionValue(
            method = "isInvulnerableTo",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isRemoved()Z")
    )
    private boolean hasForestAuraAndIsSuffocating(boolean original, DamageSource damageSource) {
        boolean isSuffocatingWithForestAura = false;
        if( ((Entity) (Object) this) instanceof LivingEntity ){
            isSuffocatingWithForestAura = ( ((LivingEntity) (Object) this).hasStatusEffect(LightEffects.FOREST_AURA) && damageSource.isOf(DamageTypes.IN_WALL));
        }
        return original || isSuffocatingWithForestAura;
    }

}
