package me.emafire003.dev.lightwithin.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class FrozenMobTopBlock extends Block {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public FrozenMobTopBlock(Settings settings) {
        super(settings);
    }

    private static final VoxelShape SHAPE_N = Stream.of(
            Block.createCuboidShape(12, 4.5, 0.5, 16, 8.5, 8.5),
            Block.createCuboidShape(4, 0, 6, 12, 8, 10),
            Block.createCuboidShape(4, 8, 4, 12, 16, 12),
            Block.createCuboidShape(0, 4.5, -0.5, 4, 8.5, 7.5)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape SHAPE_E = Stream.of(
            Block.createCuboidShape(7.5, 4.5, 12, 15.5, 8.5, 16),
            Block.createCuboidShape(6, 0, 4, 10, 8, 12),
            Block.createCuboidShape(4, 8, 4, 12, 16, 12),
            Block.createCuboidShape(8.5, 4.5, 0, 16.5, 8.5, 4)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape SHAPE_S = Stream.of(
            Block.createCuboidShape(0, 4.5, 7.5, 4, 8.5, 15.5),
            Block.createCuboidShape(4, 0, 6, 12, 8, 10),
            Block.createCuboidShape(4, 8, 4, 12, 16, 12),
            Block.createCuboidShape(12, 4.5, 8.5, 16, 8.5, 16.5)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    private static final VoxelShape SHAPE_W = Stream.of(
            Block.createCuboidShape(0.5, 4.5, 0, 8.4, 8.5, 4),
            Block.createCuboidShape(6, 0, 4, 10, 8, 12),
            Block.createCuboidShape(4, 8, 4, 12, 16, 12),
            Block.createCuboidShape(-0.5, 4.5, 12, 7.5, 8.5, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if(ctx.getPlayer() == null){
            return this.getDefaultState();
        }
        return this.getDefaultState().with(FACING, ctx.getPlayer().getHorizontalFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case NORTH:
                return SHAPE_N;
            case SOUTH:
                return SHAPE_S;
            case WEST:
                return SHAPE_W;
            case EAST:
                return SHAPE_E;
            default:
                return SHAPE_N;
        }
    }
}
