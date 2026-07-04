package net.zphyghtning.streetscape;

import net.fabricmc.api.ModInitializer;
import net.zphyghtning.streetscape.block.ModBlocks;
import net.zphyghtning.streetscape.item.ModItemGroups;

import net.zphyghtning.streetscape.block.entity.ModBlockEntities;
import net.zphyghtning.streetscape.screen.ModScreenHandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Streetscape implements ModInitializer {
	public static final String MOD_ID = "streetscape";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModScreenHandlers.registerScreenHandlers();
		ModItemGroups.registerItemGroups();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
	}
}