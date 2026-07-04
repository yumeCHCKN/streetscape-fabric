package net.zphyghtning.streetscape.client;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.zphyghtning.streetscape.api.client.model.ExtraModelData;
import net.zphyghtning.streetscape.api.client.model.IExtraModelDataProvider;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class MimicBakedModel implements BakedModel, FabricBakedModel {
    private final BakedModel baseBlockModel;

    public MimicBakedModel(BakedModel baseBlockModel) {
        this.baseBlockModel = baseBlockModel;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return baseBlockModel.isBuiltin();
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        Object be = blockView.getBlockEntity(pos);
        if (be instanceof IExtraModelDataProvider provider) {
            ExtraModelData data = provider.getExtraModelData();
            if (data != null) {
                BlockState heldState = data.getHeldState();
                if (heldState != null && !heldState.isAir()) {
                    BakedModel heldModel = MinecraftClient.getInstance().getBlockRenderManager().getModel(heldState);
                    if (heldModel != null) {
                        ((FabricBakedModel) heldModel).emitBlockQuads(blockView, heldState, pos, randomSupplier, context);
                    }
                }
            }
        }
    }

    @Override
    public void emitItemQuads(net.minecraft.item.ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        ((FabricBakedModel) baseBlockModel).emitItemQuads(stack, randomSupplier, context);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return baseBlockModel.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepth() {
        return baseBlockModel.hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return baseBlockModel.isSideLit();
    }

    @Override
    public Sprite getParticleSprite() {
        return baseBlockModel.getParticleSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return baseBlockModel.getTransformation();
    }

    @Override
    public ModelOverrideList getOverrides() {
        return baseBlockModel.getOverrides();
    }
}
