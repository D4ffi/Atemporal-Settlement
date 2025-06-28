package com.bydaffi.atemp.util;

import com.bydaffi.atemp.AtemporalSettlement;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PortalSpawner {

    public static boolean spawnPortalStructure(ItemUsageContext context) {
        if (!(context.getWorld() instanceof ServerWorld serverWorld)) {
            return false;
        }

        try {
            StructureTemplateManager templateManager = serverWorld.getStructureTemplateManager();
            Identifier structureId = new Identifier(AtemporalSettlement.MOD_ID, "portal");
            
            StructureTemplate template = templateManager.getTemplateOrBlank(structureId);
            if (template != null) {
                BlockPos spawnPos = context.getBlockPos();
                
                StructurePlacementData placementData = new StructurePlacementData();
                placementData.setRotation(BlockRotation.NONE);
                placementData.setMirror(BlockMirror.NONE);
                placementData.setIgnoreEntities(false);
                
                template.place(serverWorld, spawnPos, spawnPos, placementData, serverWorld.getRandom(), 2);
                AtemporalSettlement.LOGGER.info("Successfully placed portal structure at {}", spawnPos);
                
                return true;
            } else {
                AtemporalSettlement.LOGGER.warn("Could not load portal structure template");
                return false;
            }
        } catch (Exception e) {
            AtemporalSettlement.LOGGER.error("Failed to spawn portal structure", e);
            return false;
        }
    }
}