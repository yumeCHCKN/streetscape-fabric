package net.zphyghtning.streetscape.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;

public class DirectionalMarkingBlock extends HorizontalFacingBlock {
    public static final MapCodec<DirectionalMarkingBlock> CODEC = createCodec(DirectionalMarkingBlock::new);
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    @Override
    public MapCodec<? extends DirectionalMarkingBlock> getCodec() {
        return CODEC;
    }

    public DirectionalMarkingBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
