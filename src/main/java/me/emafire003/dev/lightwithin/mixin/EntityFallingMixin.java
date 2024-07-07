package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.events.EntityFallingEvent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class EntityFallingMixin extends Entity {

    public EntityFallingMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "fall", at = @At("HEAD"))
    public void injectEntityFalling(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo ci) {

        LivingEntity entity = ((LivingEntity) (Object) this);
        if(isFalling(entity)){
            EntityFallingEvent.EVENT.invoker().falling(entity, heightDifference, this.fallDistance);
        }
    }

    @Unique
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
