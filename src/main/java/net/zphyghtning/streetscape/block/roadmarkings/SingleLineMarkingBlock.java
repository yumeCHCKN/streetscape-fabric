package net.zphyghtning.streetscape.block.roadmarkings;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class SingleLineMarkingBlock extends Block {
    public static final MapCodec<SingleLineMarkingBlock> CODEC = createCodec(SingleLineMarkingBlock::new);

    public static final EnumProperty<RoadConnection> NORTH = EnumProperty.of("north", RoadConnection.class);
    public static final EnumProperty<RoadConnection> EAST = EnumProperty.of("east", RoadConnection.class);
    public static final EnumProperty<RoadConnection> SOUTH = EnumProperty.of("south", RoadConnection.class);
    public static final EnumProperty<RoadConnection> WEST = EnumProperty.of("west", RoadConnection.class);

    @Override
    public MapCodec<? extends SingleLineMarkingBlock> getCodec() {
        return CODEC;
    }

    public SingleLineMarkingBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, RoadConnection.NONE)
                .with(EAST, RoadConnection.NONE)
                .with(SOUTH, RoadConnection.NONE)
                .with(WEST, RoadConnection.NONE));
    }

    protected boolean shouldConnectTo(BlockState neighborState, Direction direction) {
        Block block = neighborState.getBlock();
        if (block instanceof SingleLineMarkingBlock && !(block instanceof DoubleLineMarkingBlock)) {
            return true;
        }
        if (block instanceof ArrowMarkingBlock) {
            if (neighborState.contains(DirectionalMarkingBlock.FACING)) {
                return neighborState.get(DirectionalMarkingBlock.FACING) == direction;
            }
        }
        return false;
    }

    protected RoadConnection getConnection(WorldAccess world, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.offset(direction);

        BlockState flatState = world.getBlockState(neighborPos);
        if (shouldConnectTo(flatState, direction)) {
            return RoadConnection.FLAT;
        }

        BlockState upState = world.getBlockState(neighborPos.up());
        if (upState.getBlock() instanceof SingleLineMarkingBlock && !(upState.getBlock() instanceof DoubleLineMarkingBlock)) {
            return RoadConnection.UP;
        }
        if (upState.getBlock() instanceof ArrowMarkingBlock) {
            if (upState.contains(DirectionalMarkingBlock.FACING) && upState.get(DirectionalMarkingBlock.FACING) == direction) {
                return RoadConnection.UP;
            }
        }

        BlockState downState = world.getBlockState(neighborPos.down());
        if (downState.getBlock() instanceof SingleLineMarkingBlock && !(downState.getBlock() instanceof DoubleLineMarkingBlock)) {
            return RoadConnection.DOWN;
        }
        if (downState.getBlock() instanceof ArrowMarkingBlock) {
            if (downState.contains(DirectionalMarkingBlock.FACING) && downState.get(DirectionalMarkingBlock.FACING) == direction) {
                return RoadConnection.DOWN;
            }
        }

        return RoadConnection.NONE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        return this.getDefaultState()
                .with(NORTH, getConnection(world, pos, Direction.NORTH))
                .with(EAST, getConnection(world, pos, Direction.EAST))
                .with(SOUTH, getConnection(world, pos, Direction.SOUTH))
                .with(WEST, getConnection(world, pos, Direction.WEST));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis().isHorizontal()) {
            RoadConnection conn = getConnection(world, pos, direction);
            switch (direction) {
                case NORTH:
                    return state.with(NORTH, conn);
                case EAST:
                    return state.with(EAST, conn);
                case SOUTH:
                    return state.with(SOUTH, conn);
                case WEST:
                    return state.with(WEST, conn);
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, net.minecraft.entity.LivingEntity placer, net.minecraft.item.ItemStack itemStack) {
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
            updateBlockState(world, sidePos.up());
            updateBlockState(world, sidePos.down());
        }
    }

    public void updateBlockState(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof SingleLineMarkingBlock && !(state.getBlock() instanceof DoubleLineMarkingBlock)) {
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
            if (updatedState != state) {
                world.setBlockState(pos, updatedState, Block.NOTIFY_LISTENERS);
            }
        } else if (state.getBlock() instanceof ArrowMarkingBlock) {
            Direction facing = state.get(ArrowMarkingBlock.FACING);
            Direction back = facing.getOpposite();
            BlockPos targetPos = pos.offset(back).down();
            BlockState targetState = world.getBlockState(targetPos);
            boolean connected = targetState.getBlock() instanceof SingleLineMarkingBlock && !(targetState.getBlock() instanceof DoubleLineMarkingBlock);
            BlockState updatedState = state.with(ArrowMarkingBlock.CONNECTED, connected);
            if (updatedState != state) {
                world.setBlockState(pos, updatedState, Block.NOTIFY_LISTENERS);
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }
}
