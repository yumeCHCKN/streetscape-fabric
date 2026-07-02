package net.zphyghtning.streetscape.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

public class GarbageCanBlock extends HorizontalFacingBlock {
    public static final MapCodec<GarbageCanBlock> CODEC = createCodec(GarbageCanBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    @Override
    public MapCodec<? extends GarbageCanBlock> getCodec() {
        return CODEC;
    }

    public GarbageCanBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(HALF, DoubleBlockHalf.LOWER));
    }

    private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    private static final VoxelShape LOWER_NORTH = VoxelShapes.union(
            BASE_SHAPE,
            Block.createCuboidShape(0.0, 0.0, 11.0, 1.0, 5.0, 16.0),
            Block.createCuboidShape(15.0, 0.0, 11.0, 16.0, 5.0, 16.0)
    );
    private static final VoxelShape LOWER_SOUTH = VoxelShapes.union(
            BASE_SHAPE,
            Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 5.0, 5.0),
            Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 5.0, 5.0)
    );
    private static final VoxelShape LOWER_EAST = VoxelShapes.union(
            BASE_SHAPE,
            Block.createCuboidShape(0.0, 0.0, 0.0, 5.0, 5.0, 1.0),
            Block.createCuboidShape(0.0, 0.0, 15.0, 5.0, 5.0, 16.0)
    );
    private static final VoxelShape LOWER_WEST = VoxelShapes.union(
            BASE_SHAPE,
            Block.createCuboidShape(11.0, 0.0, 0.0, 16.0, 5.0, 1.0),
            Block.createCuboidShape(11.0, 0.0, 15.0, 16.0, 5.0, 16.0)
    );

    private static final VoxelShape UPPER_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 12.0, 15.0),
            Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 15.0, 16.0)
    );

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        DoubleBlockHalf half = state.get(HALF);
        if (half == DoubleBlockHalf.UPPER) {
            return UPPER_SHAPE;
        }
        return switch (state.get(FACING)) {
            case SOUTH -> LOWER_SOUTH;
            case EAST -> LOWER_EAST;
            case WEST -> LOWER_WEST;
            default -> LOWER_NORTH;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);
    }


    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (ctx.getSide() != Direction.UP) {
            return null;
        }
        BlockPos pos = ctx.getBlockPos();
        World world = ctx.getWorld();
        if (pos.getY() < world.getTopY() - 1 && world.getBlockState(pos.up()).canReplace(ctx)) {
            return this.getDefaultState()
                    .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                    .with(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        DoubleBlockHalf half = state.get(HALF);
        if (half == DoubleBlockHalf.UPPER) {
            BlockState lowerState = world.getBlockState(pos.down());
            return lowerState.isOf(this) && lowerState.get(HALF) == DoubleBlockHalf.LOWER;
        }
        BlockPos downPos = pos.down();
        BlockState downState = world.getBlockState(downPos);
        return downState.isSideSolidFullSquare(world, downPos, Direction.UP);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.get(HALF);

        if (half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }

        if (direction.getAxis() == Direction.Axis.Y) {
            if (half == DoubleBlockHalf.LOWER && direction == Direction.UP) {
                if (!neighborState.isOf(this) || neighborState.get(HALF) != DoubleBlockHalf.UPPER) {
                    return Blocks.AIR.getDefaultState();
                }
            }
            if (half == DoubleBlockHalf.UPPER && direction == Direction.DOWN) {
                if (!neighborState.isOf(this) || neighborState.get(HALF) != DoubleBlockHalf.LOWER) {
                    return Blocks.AIR.getDefaultState();
                }
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative()) {
            onBreakInCreative(world, pos, state, player);
        }
        return super.onBreak(world, pos, state, player);
    }

    protected static void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf half = state.get(HALF);
        if (half == DoubleBlockHalf.UPPER) {
            BlockPos downPos = pos.down();
            BlockState downState = world.getBlockState(downPos);
            if (downState.isOf(state.getBlock()) && downState.get(HALF) == DoubleBlockHalf.LOWER) {
                world.setBlockState(downPos, Blocks.AIR.getDefaultState(), 35);
                world.syncWorldEvent(player, 2001, downPos, Block.getRawIdFromState(downState));
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }
}
