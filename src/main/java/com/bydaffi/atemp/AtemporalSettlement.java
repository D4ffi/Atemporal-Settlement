package com.bydaffi.atemp;

import com.bydaffi.atemp.block.AtempBlocks;
import com.bydaffi.atemp.block.entity.AtempBlockEntityTypes;  // Agregar esta importación
import com.bydaffi.atemp.command.AtempCommands;
import com.bydaffi.atemp.dimension.AtempDimensions;
import com.bydaffi.atemp.event.WorldEventHandler;
import com.bydaffi.atemp.item.AtempItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtemporalSettlement implements ModInitializer {
	public static final String MOD_ID = "atemp";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		AtempItems.registerItems();
		AtempBlocks.registerBlocks();
		AtempBlockEntityTypes.initialize(); // ¡AGREGAR ESTA LÍNEA!
		AtempCommands.register();
		AtempDimensions.registerDimensions();
		WorldEventHandler.register();

		LOGGER.info("Hello Fabric world!");
	}
}