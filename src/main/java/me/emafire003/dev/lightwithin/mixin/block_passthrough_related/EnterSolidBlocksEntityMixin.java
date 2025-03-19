package me.emafire003.dev.lightwithin.mixin.block_passthrough_related;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.emafire003.dev.lightwithin.lights.ForestAuraLight;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class EnterSolidBlocksEntityMixin {

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @ModifyExpressionValue(
            method = "applyMovementInput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PowderSnowBlock;canWalkOnPowderSnow(Lnet/minecraft/entity/Entity;)Z")
    )
    public boolean injectJumpCanWalk(boolean original){
        if(this.hasStatusEffect(LightEffects.FOREST_AURA) || this.hasStatusEffect(LightEffects.WIND_WALKING)){
            return true;
        }
        return original;
    }

    //TODO inject into InGameOverlayRenderer renderOverlays to do the noclip thing/make blocks visible like in spectator

    @ModifyExpressionValue(
            method = "applyMovementInput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z")
    )
    public boolean injectJumpIsBlock(boolean original){
        if(((LivingEntity) (Object) this).getBlockStateAtPos().isIn(ForestAuraLight.FOREST_AURA_BLOCKS)){
            return true;
        }else if(((LivingEntity) (Object) this).hasStatusEffect(LightEffects.WIND_WALKING) && ((LivingEntity) (Object) this).getBlockStateAtPos().isOf(Blocks.AIR)){
            return true;
        }
        return original;
    }
}
