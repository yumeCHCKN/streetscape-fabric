package net.zphyghtning.streetscape;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.zphyghtning.streetscape.block.ModBlocks;
import net.zphyghtning.streetscape.screen.ModScreenHandlers;
import net.zphyghtning.streetscape.client.gui.screen.ingame.RoadworksScreen;

public class StreetscapeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WHITE_ARROW_MARKING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.YELLOW_ARROW_MARKING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WHITE_SINGLE_LINE_MARKING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.YELLOW_SINGLE_LINE_MARKING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TRAFFIC_CONE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LARGE_TRAFFIC_CONE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TALL_TRAFFIC_CONE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIGN_POLE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.RED_OCTAGON_SIGN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GARBAGE_CAN, RenderLayer.getCutout());

        HandledScreens.register(ModScreenHandlers.ROADWORKS_SCREEN_HANDLER, RoadworksScreen::new);

        net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer.register((matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
            matrices.push();
            contextModel.head.rotate(matrices);
            matrices.translate(0.0F, -0.25F, 0.0F);
            matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            matrices.scale(0.625F, -0.625F, -0.625F);

            net.minecraft.block.BlockState defaultState = ModBlocks.TRAFFIC_CONE.getDefaultState();
            net.minecraft.client.render.model.BakedModel blockModel = net.minecraft.client.MinecraftClient.getInstance()
                    .getBlockRenderManager()
                    .getModel(defaultState);

            net.minecraft.client.MinecraftClient.getInstance().getItemRenderer().renderItem(
                    stack,
                    net.minecraft.client.render.model.json.ModelTransformationMode.HEAD,
                    false,
                    matrices,
                    vertexConsumers,
                    light,
                    net.minecraft.client.render.OverlayTexture.DEFAULT_UV,
                    blockModel
            );
            matrices.pop();
        }, ModBlocks.TRAFFIC_CONE);
    }
}
