package net.zphyghtning.streetscape.block.entity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.zphyghtning.streetscape.Streetscape;
import net.zphyghtning.streetscape.block.ModBlocks;
import net.zphyghtning.streetscape.block.roadsigns.RoadSignAttachedBlockEntity;

public class ModBlockEntities {
    public static final BlockEntityType<RoadSignAttachedBlockEntity> ROAD_SIGN_ATTACHED_BE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Streetscape.MOD_ID, "road_sign_attached_be"),
            BlockEntityType.Builder.create(RoadSignAttachedBlockEntity::new, ModBlocks.ATTACHED_ROAD_SIGN).build(null)
    );

    public static void registerBlockEntities() {
        Streetscape.LOGGER.info("Registering Block Entities for " + Streetscape.MOD_ID);
    }
}
