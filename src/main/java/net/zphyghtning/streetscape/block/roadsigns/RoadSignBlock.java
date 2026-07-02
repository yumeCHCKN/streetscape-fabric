package net.zphyghtning.streetscape.block.roadsigns;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class RoadSignBlock extends HorizontalFacingBlock {
    public static final MapCodec<RoadSignBlock> CODEC = createCodec(RoadSignBlock::new);

    protected static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(-2.0, -2.0, 0.0, 18.0, 18.0, 2.0);
    protected static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(-2.0, -2.0, 14.0, 18.0, 18.0, 16.0);
    protected static final VoxelShape SHAPE_EAST = Block.createCuboidShape(14.0, -2.0, -2.0, 16.0, 18.0, 18.0);
    protected static final VoxelShape SHAPE_WEST = Block.createCuboidShape(0.0, -2.0, -2.0, 2.0, 18.0, 18.0);

    @Override
    public MapCodec<? extends RoadSignBlock> getCodec() {
        return CODEC;
    }

    public RoadSignBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING).getOpposite()) {
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean canPlaceAt(BlockState state, net.minecraft.world.WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos attachedPos = pos.offset(direction.getOpposite());
        BlockState attachedState = world.getBlockState(attachedPos);
        return !attachedState.isAir();
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, net.minecraft.world.WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == state.get(FACING).getOpposite() && !state.canPlaceAt(world, pos)) {
            return net.minecraft.block.Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        if (direction.getAxis().isHorizontal()) {
            BlockPos attachedPos = ctx.getBlockPos().offset(direction.getOpposite());
            BlockState attachedState = ctx.getWorld().getBlockState(attachedPos);
            if (!attachedState.isAir()) {
                return this.getDefaultState().with(FACING, direction);
            }
        }
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
