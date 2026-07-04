package net.zphyghtning.streetscape.api.client.model;

import net.minecraft.block.BlockState;

public class ExtraModelData {
    private final BlockState heldState;

    public ExtraModelData(BlockState heldState) {
        this.heldState = heldState;
    }

    public BlockState getHeldState() {
        return heldState;
    }

    public static class Builder {
        private BlockState heldState;

        public Builder setHeldState(BlockState state) {
            this.heldState = state;
            return this;
        }

        public ExtraModelData build() {
            return new ExtraModelData(heldState);
        }
    }
}
