package net.zphyghtning.streetscape.block.roadmarkings;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class DoubleLineMarkingBlock extends SingleLineMarkingBlock {
    public static final MapCodec<DoubleLineMarkingBlock> CODEC = createCodec(DoubleLineMarkingBlock::new);
    public static final EnumProperty<DoubleLineShape> SHAPE = EnumProperty.of("shape", DoubleLineShape.class);

    @Override
    public MapCodec<? extends DoubleLineMarkingBlock> getCodec() {
        return CODEC;
    }

    public DoubleLineMarkingBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(SHAPE, DoubleLineShape.NONE));
    }

    @Override
    protected boolean shouldConnectTo(BlockState neighborState, Direction direction) {
        return neighborState.getBlock() instanceof DoubleLineMarkingBlock;
    }

    @Override
    protected RoadConnection getConnection(WorldAccess world, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.offset(direction);

        BlockState flatState = world.getBlockState(neighborPos);
        if (shouldConnectTo(flatState, direction)) {
            return RoadConnection.FLAT;
        }

        BlockState upState = world.getBlockState(neighborPos.up());
        if (upState.getBlock() instanceof DoubleLineMarkingBlock) {
            return RoadConnection.UP;
        }

        BlockState downState = world.getBlockState(neighborPos.down());
        if (downState.getBlock() instanceof DoubleLineMarkingBlock) {
            return RoadConnection.DOWN;
        }

        return RoadConnection.NONE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();

        RoadConnection northConn = getConnection(world, pos, Direction.NORTH);
        RoadConnection southConn = getConnection(world, pos, Direction.SOUTH);
        RoadConnection eastConn = getConnection(world, pos, Direction.EAST);
        RoadConnection westConn = getConnection(world, pos, Direction.WEST);

        BlockState state = this.getDefaultState()
                .with(NORTH, northConn)
                .with(EAST, eastConn)
                .with(SOUTH, southConn)
                .with(WEST, westConn);

        return state.with(SHAPE, getShapeFromState(state));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis().isHorizontal()) {
            RoadConnection conn = getConnection(world, pos, direction);
            BlockState updated = state;
            switch (direction) {
                case NORTH -> updated = state.with(NORTH, conn);
                case EAST -> updated = state.with(EAST, conn);
                case SOUTH -> updated = state.with(SOUTH, conn);
                case WEST -> updated = state.with(WEST, conn);
            }
            return updated.with(SHAPE, getShapeFromState(updated));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private DoubleLineShape getShapeFromState(BlockState state) {
        boolean n = state.get(NORTH) != RoadConnection.NONE;
        boolean e = state.get(EAST) != RoadConnection.NONE;
        boolean s = state.get(SOUTH) != RoadConnection.NONE;
        boolean w = state.get(WEST) != RoadConnection.NONE;

        if (n && e && s && w) return DoubleLineShape.ALL;

        if (n && e && s) return DoubleLineShape.NORTH_EAST_SOUTH;
        if (e && s && w) return DoubleLineShape.EAST_SOUTH_WEST;
        if (s && w && n) return DoubleLineShape.SOUTH_WEST_NORTH;
        if (w && n && e) return DoubleLineShape.WEST_NORTH_EAST;

        if (n && e) return DoubleLineShape.NORTH_EAST;
        if (e && s) return DoubleLineShape.EAST_SOUTH;
        if (s && w) return DoubleLineShape.SOUTH_WEST;
        if (w && n) return DoubleLineShape.WEST_NORTH;

        if (n && s) return DoubleLineShape.NORTH_SOUTH;
        if (e && w) return DoubleLineShape.EAST_WEST;

        if (n || s) return DoubleLineShape.NORTH_SOUTH;
        if (e || w) return DoubleLineShape.EAST_WEST;

        return DoubleLineShape.NONE;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, net.minecraft.entity.LivingEntity placer, net.minecraft.item.ItemStack itemStack) {
        // Bypass SingleLineMarkingBlock's diagonal update which excludes double lines
        super.onPlaced(world, pos, state, placer, itemStack);
        updateDiagonalNeighbors(world, pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            updateDiagonalNeighbors(world, pos);
        }
    }

    private void updateDiagonalNeighbors(World world, BlockPos pos) {
        for (Direction dir : Direction.Type.HORIZONTAL) {
            BlockPos sidePos = pos.offset(dir);
            updateDoubleBlockState(world, sidePos.up());
            updateDoubleBlockState(world, sidePos.down());
        }
    }

    private void updateDoubleBlockState(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof DoubleLineMarkingBlock) {
            BlockState updatedState = state;
            for (Direction dir : Direction.Type.HORIZONTAL) {
                RoadConnection conn = getConnection(world, pos, dir);
                updatedState = switch (dir) {
                    case NORTH -> updatedState.with(NORTH, conn);
                    case EAST -> updatedState.with(EAST, conn);
                    case SOUTH -> updatedState.with(SOUTH, conn);
                    case WEST -> updatedState.with(WEST, conn);
                    default -> updatedState;
                };
            }
            updatedState = updatedState.with(SHAPE, getShapeFromState(updatedState));
            if (updatedState != state) {
                world.setBlockState(pos, updatedState, 2);
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(SHAPE);
    }
}
