package com.bydaffi.atemp.block;

import com.bydaffi.atemp.AtemporalSettlement;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class AtempBlocks {

    public static final Block CAMP_ANCHOR = registerBlock("camp_anchor",
            new CampAnchorBlock(FabricBlockSettings.copyOf(Blocks.CAMPFIRE)
                    .sounds(BlockSoundGroup.WOOD)
                    .strength(2.0f)
                    .luminance(15)
                    .nonOpaque()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(AtemporalSettlement.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, new Identifier(AtemporalSettlement.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerBlocks() {
        AtemporalSettlement.LOGGER.info("Registering blocks for " + AtemporalSettlement.MOD_ID);
    }
}