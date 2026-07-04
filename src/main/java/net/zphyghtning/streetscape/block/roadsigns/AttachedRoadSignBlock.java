package net.zphyghtning.streetscape.block.roadsigns;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AttachedRoadSignBlock extends BlockWithEntity {
    public static final MapCodec<AttachedRoadSignBlock> CODEC = createCodec(AttachedRoadSignBlock::new);

    @Override
    public MapCodec<? extends AttachedRoadSignBlock> getCodec() {
        return CODEC;
    }

    public AttachedRoadSignBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RoadSignAttachedBlockEntity(pos, state);
    }

    private static final VoxelShape POST_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return POST_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return POST_SHAPE;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof RoadSignAttachedBlockEntity attachedBE) {
                BlockState baseState = attachedBE.getBaseBlockState();
                if (baseState != null && !baseState.isAir()) {
                    Block.dropStack(world, pos, new ItemStack(baseState.getBlock().asItem()));
                }
                BlockState signState = attachedBE.getSignBlockState();
                if (signState != null && !signState.isAir()) {
                    Block.dropStack(world, pos, new ItemStack(signState.getBlock().asItem()));
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
