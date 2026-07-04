package net.zphyghtning.streetscape.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.zphyghtning.streetscape.block.roadsigns.RoadSignAttachedBlockEntity;

import net.minecraft.client.MinecraftClient;

public class RoadSignAttachedBlockEntityRenderer implements BlockEntityRenderer<RoadSignAttachedBlockEntity> {
    private final BlockRenderManager blockRenderManager;

    public RoadSignAttachedBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
    }

    @Override
    public void render(RoadSignAttachedBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState signState = entity.getSignBlockState();
        if (signState != null && !signState.isAir()) {
            matrices.push();
            
            matrices.translate(0.5, 0.5, 0.5);
            
            float angle = entity.getRotation() * 22.5f + 180.0f;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-angle));
            
            matrices.translate(0.0, 0.0, -0.6);
            
            matrices.translate(-0.5, -0.5, -0.5);
            
            blockRenderManager.renderBlockAsEntity(signState, matrices, vertexConsumers, light, overlay);
            
            matrices.pop();
        }
    }
}
