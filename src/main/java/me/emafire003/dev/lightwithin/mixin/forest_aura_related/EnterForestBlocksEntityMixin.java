package me.emafire003.dev.lightwithin.mixin.forest_aura_related;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.emafire003.dev.lightwithin.lights.ForestAuraLight;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class EnterForestBlocksEntityMixin {

    @ModifyExpressionValue(
            method = "applyMovementInput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PowderSnowBlock;canWalkOnPowderSnow(Lnet/minecraft/entity/Entity;)Z")
    )
    public boolean injectForestAuraJumpCanWalk(boolean original){
        if(((LivingEntity) (Object) this).hasStatusEffect(LightEffects.FOREST_AURA)){
            return true;
        }
        return original;
    }


    @ModifyExpressionValue(
            method = "applyMovementInput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z")
    )
    public boolean injectForestAuraJumpIsBlock(boolean original){
        if(((LivingEntity) (Object) this).getBlockStateAtPos().isIn(ForestAuraLight.FOREST_AURA_BLOCKS)){
            return true;
        }
        return original;
    }
}
