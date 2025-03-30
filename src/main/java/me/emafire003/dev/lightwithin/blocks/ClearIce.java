package me.emafire003.dev.lightwithin.blocks;


import me.emafire003.dev.lightwithin.LightWithin;
import me.emafire003.dev.lightwithin.lights.FrostLight;
import me.emafire003.dev.lightwithin.status_effects.LightEffects;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClearIce extends FrostedIceBlock {

    public ClearIce(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(AGE, 0));

    }

    public static BlockState getMeltedState() {
        //return Blocks.WATER.getStateWithProperties(WaterFluid.Flowing.STATE_IDS.get(2).getBlockState());
        //return WaterFluid.Flowing.STATE_IDS.get(7).getBlockState();
        return Blocks.AIR.getDefaultState();
    }

    public static final IntProperty AGE = Properties.AGE_3;

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return new ItemStack(LightBlocks.CLEAR_ICE, 1);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        List<PlayerEntity> entities = world.getEntitiesByClass(PlayerEntity.class, new Box(pos).expand(LightWithin.getBoxExpansionAmount()), (entity1 -> true));
        boolean run = true;

        if(!entities.isEmpty()){
            for(PlayerEntity p : entities){
                if(LightWithin.LIGHT_COMPONENT.get(p).getType() instanceof FrostLight
                        && p.hasStatusEffect(LightEffects.LIGHT_ACTIVE)){
                    run = false;
                    break;
                }
            }
        }

        if (run && (random.nextInt(3) == 0 || this.canMelt(world, pos, 2)) && world.getLightLevel(pos) > 11 - state.get(AGE) - state.getOpacity(world, pos) && this.increaseAge(state, world, pos)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            Direction[] var6 = Direction.values();

            for (Direction direction : var6) {
                mutable.set(pos, direction);
                BlockState blockState = world.getBlockState(mutable);
                if (blockState.isOf(this) && !this.increaseAge(blockState, world, mutable)) {
                    world.scheduleBlockTick(mutable, this, MathHelper.nextInt(random, 20, 40));
                }
            }

        } else {
            world.scheduleBlockTick(pos, this, MathHelper.nextInt(random, 20, 40));
        }
    }

    private boolean increaseAge(BlockState state, World world, BlockPos pos) {
        int i = state.get(AGE);
        if (i < 3) {
            world.setBlockState(pos, state.with(AGE, i + 1), 2);
            return false;
        } else {
            this.melt(state, world, pos);
            return true;
        }
    }

    private boolean canMelt(BlockView world, BlockPos pos, int maxNeighbors) {
        int i = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Direction[] var6 = Direction.values();

        for (Direction direction : var6) {
            mutable.set(pos, direction);
            if (world.getBlockState(mutable).isOf(this)) {
                ++i;
                if (i >= maxNeighbors) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        //if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) == 0) {
        if (!EnchantmentHelper.hasAnyEnchantmentsIn(tool, EnchantmentTags.PREVENTS_ICE_MELTING)) {
            if (world.getDimension().ultrawarm()) {
                world.removeBlock(pos, false);
                return;
            }

            BlockState blockState = world.getBlockState(pos.down());
            if (blockState.blocksMovement() || blockState.isLiquid()) {
                world.setBlockState(pos, getMeltedState());
            }
        }
        if(!world.isClient()){
            ((ServerWorld) world).spawnParticles(ParticleTypes.SNOWFLAKE,
                    pos.toCenterPos().getX(), pos.toCenterPos().getY()+0.5, pos.toCenterPos().getZ(),
                    150, 0.00001, 0.00001, 0.00001, 0.015);
        }
    }

    @Override
    protected void melt(BlockState state, World world, BlockPos pos) {
        if (world.getDimension().ultrawarm()) {
            world.removeBlock(pos, false);
        } else {
            world.setBlockState(pos, getMeltedState());
            world.updateNeighbor(pos, getMeltedState().getBlock(), pos);
            if(!world.isClient()){
                ((ServerWorld) world).spawnParticles(ParticleTypes.SNOWFLAKE,
                        pos.toCenterPos().getX(), pos.toCenterPos().getY()+0.5, pos.toCenterPos().getZ(),
                        150, 0.00001, 0.00001, 0.00001, 0.015);
            }
        }
    }

}
