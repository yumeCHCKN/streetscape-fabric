package net.zphyghtning.streetscape.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.zphyghtning.streetscape.block.ModBlocks;
import net.zphyghtning.streetscape.block.roadsigns.SignPoleBlock;
import net.zphyghtning.streetscape.block.roadsigns.AttachedRoadSignBlock;
import net.zphyghtning.streetscape.block.roadsigns.RoadSignAttachedBlockEntity;

public class RoadSignBlockItem extends BlockItem {
    public RoadSignBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    private BlockState getUnconnectedState(BlockState state) {
        BlockState unconnected = state;
        if (unconnected.contains(Properties.NORTH)) unconnected = unconnected.with(Properties.NORTH, false);
        if (unconnected.contains(Properties.EAST)) unconnected = unconnected.with(Properties.EAST, false);
        if (unconnected.contains(Properties.SOUTH)) unconnected = unconnected.with(Properties.SOUTH, false);
        if (unconnected.contains(Properties.WEST)) unconnected = unconnected.with(Properties.WEST, false);
        if (unconnected.contains(Properties.UP)) unconnected = unconnected.with(Properties.UP, false);
        if (unconnected.contains(Properties.DOWN)) unconnected = unconnected.with(Properties.DOWN, false);
        return unconnected;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // Check if the block is a sign pole, vanilla fence, or iron bars
        if (block instanceof SignPoleBlock || block instanceof FenceBlock || block instanceof PaneBlock) {
            PlayerEntity player = context.getPlayer();
            if (!world.isClient) {
                BlockState unconnectedBaseState = getUnconnectedState(state);
                BlockState newBlockState = ModBlocks.ATTACHED_ROAD_SIGN.getDefaultState();
                world.setBlockState(pos, newBlockState, Block.NOTIFY_ALL);

                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof RoadSignAttachedBlockEntity attachedBE) {
                    attachedBE.setBaseBlockState(unconnectedBaseState);
                    attachedBE.setSignBlockState(this.getBlock().getDefaultState());

                    // 16 rotations based on player looking yaw
                    int rotation = 0;
                    if (player != null) {
                        rotation = MathHelper.floor((double)((player.getYaw() + 180.0F) * 16.0F / 360.0F) + 0.5) & 15;
                    }
                    attachedBE.setRotation(rotation);
                    attachedBE.markDirty();
                    world.updateListeners(pos, newBlockState, newBlockState, Block.NOTIFY_ALL);
                }

                if (player != null && !player.isCreative()) {
                    context.getStack().decrement(1);
                }
            }
            return ActionResult.success(world.isClient);
        }

        // Swap sign variant on an already attached road sign
        if (block instanceof AttachedRoadSignBlock) {
            PlayerEntity player = context.getPlayer();
            if (!world.isClient) {
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof RoadSignAttachedBlockEntity attachedBE) {
                    BlockState oldSignState = attachedBE.getSignBlockState();
                    BlockState newSignState = this.getBlock().getDefaultState();

                    if (oldSignState.getBlock() != newSignState.getBlock()) {
                        attachedBE.setSignBlockState(newSignState);

                        if (player != null && !player.isCreative()) {
                            Block.dropStack(world, pos, new ItemStack(oldSignState.getBlock().asItem()));
                            context.getStack().decrement(1);
                        }

                        attachedBE.markDirty();
                        BlockState newBlockState = world.getBlockState(pos);
                        world.updateListeners(pos, newBlockState, newBlockState, Block.NOTIFY_ALL);
                    }
                }
            }
            return ActionResult.success(world.isClient);
        }

        return super.useOnBlock(context);
    }
}
