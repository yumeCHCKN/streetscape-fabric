package net.zphyghtning.streetscape.block;

import com.mojang.serialization.MapCodec;

public class ArrowMarkingBlock extends DirectionalMarkingBlock {
    public static final MapCodec<ArrowMarkingBlock> CODEC = createCodec(ArrowMarkingBlock::new);

    @Override
    public MapCodec<? extends ArrowMarkingBlock> getCodec() {
        return CODEC;
    }

    public ArrowMarkingBlock(Settings settings) {
        super(settings);
    }
}
