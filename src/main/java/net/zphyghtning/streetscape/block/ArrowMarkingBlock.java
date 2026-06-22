package net.zphyghtning.streetscape.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ArrowMarkingBlock extends DirectionalMarkingBlock {
    public static final MapCodec<ArrowMarkingBlock> CODEC = createCodec(ArrowMarkingBlock::new);

    public static final BooleanProperty CONNECTED = BooleanProperty.of("connected");

    @Override
    public MapCodec<? extends ArrowMarkingBlock> getCodec() {
        return CODEC;
    }

    public ArrowMarkingBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(CONNECTED, false));
    }

    private boolean checkConnection(WorldAccess world, BlockPos pos, Direction facing) {
        Direction back = facing.getOpposite();
        BlockPos targetPos = pos.offset(back).down();
        BlockState targetState = world.getBlockState(targetPos);
        return targetState.getBlock() instanceof LineMarkingBlock;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        if (state == null) return null;
        Direction facing = state.get(FACING);
        return state.with(CONNECTED, checkConnection(ctx.getWorld(), ctx.getBlockPos(), facing));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.get(FACING);
        return state.with(CONNECTED, checkConnection(world, pos, facing));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, net.minecraft.entity.LivingEntity placer, net.minecraft.item.ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        updateDiagonalNeighbor(world, pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            updateDiagonalNeighbor(world, pos, state);
        }
    }

    private void updateDiagonalNeighbor(World world, BlockPos pos, BlockState state) {
        Direction facing = state.get(FACING);
        Direction back = facing.getOpposite();
        updateLineBlock(world, pos.offset(back).down());
        updateLineBlock(world, pos.offset(back).up());
    }

    private void updateLineBlock(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof LineMarkingBlock) {
            ((LineMarkingBlock) state.getBlock()).updateBlockState(world, pos);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(CONNECTED);
    }
}
