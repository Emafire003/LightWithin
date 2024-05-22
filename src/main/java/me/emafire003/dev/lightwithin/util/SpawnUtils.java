package me.emafire003.dev.lightwithin.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;


import static net.minecraft.world.SpawnHelper.isClearForSpawn;

public class SpawnUtils {

    /**
     * Spawns and entity around another one in a spawnable space.
     * You will need to set all of the stuff you want to set for
     * the entity before calling this method
     *
     * @param entity The entity around which to spawn the other
     * @param min_dist The minimum distance from the entity for the new one to spawn (x,y,z)+/-
     * @param max_dist The maximum distance from the entity for the new one to spawn (x,y,z)+/-
     * @param spawnEntity The new entity that will spawn
     *
     * @return Returns true if the entity has been spawned correctly*/
    public static boolean spawnAround(LivingEntity entity, int min_dist, int max_dist, LivingEntity spawnEntity, ServerWorld serverWorld){
        int i = MathHelper.floor(entity.getX());
        int j = MathHelper.floor(entity.getY());
        int k = MathHelper.floor(entity.getZ());

        boolean success = false;
        for(int l = 0; l < 50; ++l) {
            int m = i + MathHelper.nextInt(entity.getRandom(), min_dist,  max_dist) * MathHelper.nextInt(entity.getRandom(), -1, 1);
            int n = j + MathHelper.nextInt(entity.getRandom(), min_dist,  max_dist) * MathHelper.nextInt(entity.getRandom(), -1, 1);
            int o = k + MathHelper.nextInt(entity.getRandom(), min_dist,  max_dist) * MathHelper.nextInt(entity.getRandom(), -1, 1);
            BlockPos blockPos = new BlockPos(m, n, o);
            EntityType<?> entityType = spawnEntity.getType();
            SpawnLocation location = SpawnRestriction.getLocation(entityType);
            if (canSpawn(location, entity.getWorld(), blockPos, entityType)) {
                spawnEntity.setPosition(m,n,o);
                if (entity.getWorld().doesNotIntersectEntities(spawnEntity) && entity.getWorld().isSpaceEmpty(spawnEntity) && !entity.getWorld().containsFluid(spawnEntity.getBoundingBox())) {
                    serverWorld.spawnEntityAndPassengers(spawnEntity);
                    success = true;
                    break;
                }
            }
        }
        return success;
    }

    /**
     * Spawns and entity around another one in a spawnable space.
     * You will need to set all of the stuff you want to set for
     * the entity before calling this method
     *
     * @param entity The entity around which to spawn the other
     * @param min_dist The minimum distance from the entity for the new one to spawn (x,y,z)+/-
     * @param max_dist The maximum distance from the entity for the new one to spawn (x,y,z)+/-
     * @param spawnEntity The new entity that will spawn
     * @param serverWorld The world in which the entity will spawn
     * @param location A location in which the entity can spawn
     *
     * @return Returns true if the entity has been spawned correctly*/
    public static boolean spawnAround(LivingEntity entity, int min_dist, int max_dist, LivingEntity spawnEntity, ServerWorld serverWorld, SpawnLocation location){
        int i = MathHelper.floor(entity.getX());
        int j = MathHelper.floor(entity.getY());
        int k = MathHelper.floor(entity.getZ());

        boolean success = false;
        for(int l = 0; l < 50; ++l) {
            int m = i + MathHelper.nextInt(entity.getRandom(), min_dist,  max_dist) * MathHelper.nextInt(entity.getRandom(), -1, 1);
            int n = j + MathHelper.nextInt(entity.getRandom(), min_dist,  max_dist) * MathHelper.nextInt(entity.getRandom(), -1, 1);
            int o = k + MathHelper.nextInt(entity.getRandom(), min_dist,  max_dist) * MathHelper.nextInt(entity.getRandom(), -1, 1);
            BlockPos blockPos = new BlockPos(m, n, o);
            EntityType<?> entityType = spawnEntity.getType();
            if (canSpawn(location, entity.getWorld(), blockPos, entityType)) {
                spawnEntity.setPosition(m,n,o);
                if (entity.getWorld().doesNotIntersectEntities(spawnEntity) && entity.getWorld().isSpaceEmpty(spawnEntity) && !entity.getWorld().containsFluid(spawnEntity.getBoundingBox())) {
                    serverWorld.spawnEntityAndPassengers(spawnEntity);
                    success = true;
                    break;
                }
            }
        }
        return success;
    }

    //This bypasses entityType block checks, aka zombie and sheep can spawn indipendently from the blocks
    public static boolean canSpawn(SpawnLocation location, WorldView world, BlockPos pos, @Nullable EntityType<?> entityType) {
        if (location == SpawnLocationTypes.UNRESTRICTED) {
            return true;
        } else if (entityType != null && world.getWorldBorder().contains(pos)) {
            BlockState blockState = world.getBlockState(pos);
            FluidState fluidState = world.getFluidState(pos);
            BlockPos blockPos = pos.up();
            BlockPos blockPos2 = pos.down();
            if (location.equals(SpawnLocationTypes.IN_WATER)) {
                return fluidState.isIn(FluidTags.WATER) && !world.getBlockState(blockPos).isSolidBlock(world, blockPos);
            } else if (location.equals(SpawnLocationTypes.IN_LAVA)) {
                return fluidState.isIn(FluidTags.LAVA);
            }
            return isClearForSpawn(world, pos, blockState, fluidState, entityType) && isClearForSpawn(world, blockPos, world.getBlockState(blockPos), world.getFluidState(blockPos), entityType);
        } else {
            return false;
        }
    }

}
