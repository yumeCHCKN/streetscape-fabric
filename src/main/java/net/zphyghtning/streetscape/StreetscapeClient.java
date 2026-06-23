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

        HandledScreens.register(ModScreenHandlers.ROADWORKS_SCREEN_HANDLER, RoadworksScreen::new);
    }
}
