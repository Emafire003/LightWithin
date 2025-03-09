package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerFreezeMixin extends LivingEntity {
    protected PlayerFreezeMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At(value = "HEAD"), method = "attack", cancellable = true)
    private void cancelAttackOnFreeze(Entity target, CallbackInfo ci) {
        if(this.hasStatusEffect(LightEffects.FROST) && !this.hasStatusEffect(LightEffects.FREEZE_RESISTANCE)){
            ci.cancel();
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "getBlockBreakingSpeed", cancellable = true)
    private void cancelBlockBreakOnFreeze(BlockState block, CallbackInfoReturnable<Float> cir) {
        if(this.hasStatusEffect(LightEffects.FROST) && !this.hasStatusEffect(LightEffects.FREEZE_RESISTANCE)){
            cir.setReturnValue(0.00000000000000001f);
        }
    }

}
