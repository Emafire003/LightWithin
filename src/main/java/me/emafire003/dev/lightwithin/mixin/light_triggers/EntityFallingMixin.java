package me.emafire003.dev.lightwithin.mixin.light_triggers;

import me.emafire003.dev.lightwithin.events.EntityFallingEvent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class EntityFallingMixin {

    @Inject(method = "fall", at = @At("HEAD"))
    public void injectEntityFalling(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo ci) {

        if(isFalling(((LivingEntity) (Object) this))){
            EntityFallingEvent.EVENT.invoker().falling(((LivingEntity)(Object)this), heightDifference, ((LivingEntity)(Object)this).fallDistance);
        }
    }

    public boolean isFalling(LivingEntity entity) {
        if(!entity.isFallFlying() && !entity.isOnGround() && !entity.isSwimming() && !entity.isClimbing() ) {
            if(entity instanceof PlayerEntity){
                if(((PlayerEntity) entity).getAbilities().flying){
                    return false;
                }
            }
            //float fallingSpeed = (float) (entity.getVelocity().lengthSquared() / 11);
            return entity.fallDistance > 3;
        }
        return false;
    }

}
