package com.bydaffi.atemp.block.entity;

import com.bydaffi.atemp.AtemporalSettlement;
import com.bydaffi.atemp.block.AtempBlocks;
import com.bydaffi.atemp.block.CampAnchorBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class AtempBlockEntityTypes {

    /**
     * Método helper para registrar BlockEntityTypes
     */
    private static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(AtemporalSettlement.MOD_ID, path), blockEntityType);
    }

    /**
     * BlockEntityType para el Camp Anchor
     */
    public static final BlockEntityType<CampAnchorBlockEntity> CAMP_ANCHOR_BLOCK_ENTITY = register(
            "camp_anchor_entity",
            BlockEntityType.Builder.create(CampAnchorBlockEntity::new, AtempBlocks.CAMP_ANCHOR).build(null)
    );

    /**
     * Método de inicialización que debe ser llamado desde el ModInitializer
     */
    public static void initialize() {
        AtemporalSettlement.LOGGER.info("Registering Block Entity Types for Atemporal Settlement");
    }
}