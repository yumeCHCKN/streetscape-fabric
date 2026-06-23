package net.zphyghtning.streetscape.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

public class DoubleTrafficConeBlock extends Block {
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    public enum Type {
        LARGE,
        TALL
    }

    private final Type type;

    private static final VoxelShape LARGE_LOWER_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
            Block.createCuboidShape(2.0, 3.0, 2.0, 14.0, 13.0, 14.0),
            Block.createCuboidShape(3.0, 13.0, 3.0, 13.0, 16.0, 13.0)
    );
    private static final VoxelShape LARGE_UPPER_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);

    private static final VoxelShape TALL_LOWER_SHAPE = VoxelShapes.union(
            Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 3.0, 12.0),
            Block.createCuboidShape(6.0, 3.0, 6.0, 10.0, 16.0, 10.0)
    );
    private static final VoxelShape TALL_UPPER_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 11.0, 10.0);

    public DoubleTrafficConeBlock(Settings settings, Type type) {
        super(settings);
        this.type = type;
        this.setDefaultState(this.stateManager.getDefaultState().with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        DoubleBlockHalf half = state.get(HALF);
        if (this.type == Type.LARGE) {
            return half == DoubleBlockHalf.LOWER ? LARGE_LOWER_SHAPE : LARGE_UPPER_SHAPE;
        } else {
            return half == DoubleBlockHalf.LOWER ? TALL_LOWER_SHAPE : TALL_UPPER_SHAPE;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getOutlineShape(state, world, pos, context);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        World world = ctx.getWorld();
        if (pos.getY() < world.getTopY() - 1 && world.getBlockState(pos.up()).canReplace(ctx)) {
            return this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.get(HALF);
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
        if (!world.isClient) {
            if (player.isCreative()) {
                onBreakInCreative(world, pos, state, player);
            } else {
                super.onBreak(world, pos, state, player);
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    protected static void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf half = state.get(HALF);
        if (half == DoubleBlockHalf.UPPER) {
            BlockPos blockPos = pos.down();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(state.getBlock()) && blockState.get(HALF) == DoubleBlockHalf.LOWER) {
                world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }
}
