package net.zphyghtning.streetscape.api.block;

import net.minecraft.block.BlockState;

public interface IBlockHolder {
    BlockState getHeldBlock();
    void setHeldBlock(BlockState state);
}
