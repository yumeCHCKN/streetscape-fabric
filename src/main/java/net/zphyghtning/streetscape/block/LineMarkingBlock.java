package net.zphyghtning.streetscape.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LineMarkingBlock extends Block {
    public static final MapCodec<LineMarkingBlock> CODEC = createCodec(LineMarkingBlock::new);

    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;

    @Override
    public MapCodec<? extends LineMarkingBlock> getCodec() {
        return CODEC;
    }

    public LineMarkingBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false));
    }

    private boolean shouldConnectTo(BlockState neighborState, Direction direction) {
        Block block = neighborState.getBlock();
        if (block instanceof LineMarkingBlock) {
            return true;
        }
        if (block instanceof ArrowMarkingBlock) {
            if (neighborState.contains(DirectionalMarkingBlock.FACING)) {
                return neighborState.get(DirectionalMarkingBlock.FACING) == direction;
            }
        }
        return false;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        return this.getDefaultState()
                .with(NORTH, shouldConnectTo(world.getBlockState(pos.north()), Direction.NORTH))
                .with(EAST, shouldConnectTo(world.getBlockState(pos.east()), Direction.EAST))
                .with(SOUTH, shouldConnectTo(world.getBlockState(pos.south()), Direction.SOUTH))
                .with(WEST, shouldConnectTo(world.getBlockState(pos.west()), Direction.WEST));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis().isHorizontal()) {
            boolean connect = shouldConnectTo(neighborState, direction);
            switch (direction) {
                case NORTH:
                    return state.with(NORTH, connect);
                case EAST:
                    return state.with(EAST, connect);
                case SOUTH:
                    return state.with(SOUTH, connect);
                case WEST:
                    return state.with(WEST, connect);
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }
}
