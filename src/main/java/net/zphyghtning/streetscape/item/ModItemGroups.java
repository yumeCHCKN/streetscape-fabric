package net.zphyghtning.streetscape.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zphyghtning.streetscape.Streetscape;
import net.zphyghtning.streetscape.block.ModBlocks;

public class ModItemGroups {

    public static final ItemGroup STREETSCAPE_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Streetscape.MOD_ID, "streetscape"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModBlocks.WHITE_DIAGONAL_STRIPE_MARKING))
                    .displayName(Text.translatable("itemGroup.streetscape.streetscape"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.ASPHALT_MIX);
                        entries.add(ModBlocks.ASPHALT);
                        entries.add(ModBlocks.ROADWORKS_TABLE);
                        entries.add(ModBlocks.WHITE_DIAGONAL_STRIPE_MARKING);
                        entries.add(ModBlocks.YELLOW_DIAGONAL_STRIPE_MARKING);
                        entries.add(ModBlocks.WHITE_ARROW_MARKING);
                        entries.add(ModBlocks.YELLOW_ARROW_MARKING);
                        entries.add(ModBlocks.WHITE_SINGLE_LINE_MARKING);
                        entries.add(ModBlocks.YELLOW_SINGLE_LINE_MARKING);
                        entries.add(ModBlocks.TRAFFIC_CONE);
                        entries.add(ModBlocks.LARGE_TRAFFIC_CONE);
                        entries.add(ModBlocks.TALL_TRAFFIC_CONE);
                        entries.add(ModBlocks.SIGN_POLE);
                        entries.add(ModBlocks.GARBAGE_CAN);
                    })
                    .build());

    public static final ItemGroup STREETSCAPE_SIGNS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Streetscape.MOD_ID, "streetscape_signs"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModBlocks.RED_OCTAGON_SIGN))
                    .displayName(Text.translatable("itemGroup.streetscape.streetscape_signs"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.RED_OCTAGON_SIGN);
                    })
                    .build());

    public static void registerItemGroups() {
        Streetscape.LOGGER.info("Registering Item Groups for " + Streetscape.MOD_ID);
    }
}
