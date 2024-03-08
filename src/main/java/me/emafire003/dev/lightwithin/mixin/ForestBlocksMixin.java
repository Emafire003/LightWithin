package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class ForestBlocksMixin implements ToggleableFeature {

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("HEAD"), cancellable = true)
    public void modifyCollisionShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if(canWalkInto(world.getBlockState(pos), context, pos)){
            if(!context.isDescending() && context.isAbove(VoxelShapes.fullCube(), pos, false)){
                cir.setReturnValue(VoxelShapes.fullCube());
                return;
            }
            cir.setReturnValue(VoxelShapes.empty());
        }
    }


    @Inject(method = "getCameraCollisionShape", at = @At("HEAD"), cancellable = true)
    public void modifyCameraCollisionShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if(canWalkInto(world.getBlockState(pos), context, pos)){
            cir.setReturnValue(VoxelShapes.empty());
        }
    }

    /**Checks if the block can be walked into by an entity
     * Returns true if an entity can get through it
     * False otherwise*/
    @Unique
    public boolean canWalkInto(BlockState state, ShapeContext context, BlockPos pos){
        if(state.isIn(LightWithin.FOREST_AURA_BLOCKS)){
            Entity entity;
            if (context instanceof EntityShapeContext && (entity = ((EntityShapeContext)context).getEntity()) != null) {
                return (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(LightEffects.FOREST_AURA));
            }
        }
        return false;
    }

    @Inject(method = "isSideInvisible", at = @At("HEAD"), cancellable = true)
    public void modifyIsSideInvisible(BlockState state, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if(state.isIn(LightWithin.FOREST_AURA_BLOCKS)){
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getCullingShape", at = @At("HEAD"), cancellable = true)
    public void modifyGetCullingShape(BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if(world.getBlockState(pos).isIn(LightWithin.FOREST_AURA_BLOCKS)){
            cir.setReturnValue(VoxelShapes.empty());
        }
    }



}
