package me.emafire003.dev.lightwithin.blocks;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IceBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

//TODO change the way it melts, also see why it doesn't spawn the particles
//TODO maybe change it so it behaves like frosted ice
public class ClearIce extends IceBlock {

    public ClearIce(Settings settings) {
        super(settings);
    }

    public static BlockState getMeltedState() {
       //return Blocks.WATER.getStateWithProperties(WaterFluid.Flowing.STATE_IDS.get(2).getBlockState());
       //return WaterFluid.Flowing.STATE_IDS.get(7).getBlockState();
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) == 0) {
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
