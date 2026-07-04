package net.zphyghtning.streetscape.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.zphyghtning.streetscape.Streetscape;
import net.zphyghtning.streetscape.block.roadmarkings.ArrowMarkingBlock;
import net.zphyghtning.streetscape.block.roadmarkings.LineMarkingBlock;
import net.zphyghtning.streetscape.block.trafficcones.DoubleTrafficConeBlock;
import net.zphyghtning.streetscape.block.trafficcones.SmallTrafficConeBlock;
import net.zphyghtning.streetscape.block.roadsigns.RoadSignBlock;
import net.zphyghtning.streetscape.block.roadsigns.SignPoleBlock;
import net.zphyghtning.streetscape.block.roadsigns.AttachedRoadSignBlock;
import net.zphyghtning.streetscape.item.RoadSignBlockItem;

public class ModBlocks {

    public static final Block WHITE_DIAGONAL_STRIPE_MARKING = registerBlock("white_diagonal_stripe_marking",
            new Block(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)));

    public static final Block YELLOW_DIAGONAL_STRIPE_MARKING = registerBlock("yellow_diagonal_stripe_marking",
            new Block(AbstractBlock.Settings.copy(Blocks.YELLOW_CONCRETE)));

    public static final Block WHITE_ARROW_MARKING = registerBlock("white_arrow_marking",
            new ArrowMarkingBlock(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)));

    public static final Block YELLOW_ARROW_MARKING = registerBlock("yellow_arrow_marking",
            new ArrowMarkingBlock(AbstractBlock.Settings.copy(Blocks.YELLOW_CONCRETE)));

    public static final Block WHITE_SINGLE_LINE_MARKING = registerBlock("white_single_line_marking",
            new LineMarkingBlock(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)));

    public static final Block YELLOW_SINGLE_LINE_MARKING = registerBlock("yellow_single_line_marking",
            new LineMarkingBlock(AbstractBlock.Settings.copy(Blocks.YELLOW_CONCRETE)));

    public static final Block ASPHALT = registerBlock("asphalt",
            new Block(AbstractBlock.Settings.copy(Blocks.BLACK_CONCRETE)));

    public static final Block ROADWORKS_TABLE = registerBlock("roadworks_table",
            new RoadworksTableBlock(AbstractBlock.Settings.copy(Blocks.STONE)));

    public static final Block ASPHALT_MIX = registerBlock("asphalt_mix",
            new Block(AbstractBlock.Settings.copy(Blocks.MUD)));

    public static final Block TRAFFIC_CONE = registerBlock("traffic_cone",
            new SmallTrafficConeBlock(AbstractBlock.Settings.create()
                    .nonOpaque()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.SCAFFOLDING)
            ));

    public static final Block LARGE_TRAFFIC_CONE = registerBlock("large_traffic_cone",
            new DoubleTrafficConeBlock(AbstractBlock.Settings.create()
                    .nonOpaque()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.SCAFFOLDING),
                    DoubleTrafficConeBlock.Type.LARGE
            ));

    public static final Block TALL_TRAFFIC_CONE = registerBlock("tall_traffic_cone",
            new DoubleTrafficConeBlock(AbstractBlock.Settings.create()
                    .nonOpaque()
                    .breakInstantly()
                    .sounds(net.minecraft.sound.BlockSoundGroup.SCAFFOLDING),
                    DoubleTrafficConeBlock.Type.TALL
            ));

    public static final Block SIGN_POLE = registerBlock("sign_pole",
            new SignPoleBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).nonOpaque()));

    public static final Block RED_OCTAGON_SIGN = registerBlock("red_octagon_sign",
            new RoadSignBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).nonOpaque()));

    public static final Block GARBAGE_CAN = registerBlock("garbage_can",
            new GarbageCanBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).nonOpaque()));

    public static final Block ATTACHED_ROAD_SIGN = Registry.register(Registries.BLOCK,
            Identifier.of(Streetscape.MOD_ID, "attached_road_sign"),
            new AttachedRoadSignBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).nonOpaque()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Streetscape.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Item item;
        if (block instanceof RoadSignBlock) {
            item = new RoadSignBlockItem(block, new Item.Settings());
        } else {
            item = new BlockItem(block, new Item.Settings());
        }
        Registry.register(Registries.ITEM, Identifier.of(Streetscape.MOD_ID, name), item);
    }

    public static void registerModBlocks() {
        Streetscape.LOGGER.info("Registering Mod Blocks for " + Streetscape.MOD_ID);
    }
}
