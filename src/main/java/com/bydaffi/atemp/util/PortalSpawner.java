package com.bydaffi.atemp.util;

import com.bydaffi.atemp.AtemporalSettlement;
import com.bydaffi.atemp.block.CampAnchorBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class PortalSpawner {

    public static boolean spawnPortalStructure(ItemUsageContext context, UUID bindUuid) {
        if (!(context.getWorld() instanceof ServerWorld serverWorld)) {
            return false;
        }

        // Check if player already has a camp
        if (context.getPlayer() instanceof ServerPlayerEntity serverPlayer) {
            // Initialize world data if not already done
            Manager.initializeWorld(serverWorld);
            
            if (Manager.hasPlayerSettlement(serverPlayer.getUuid())) {
                serverPlayer.sendMessage(Text.literal("You already have an active camp! Destroy it first by right-clicking the camp anchor with a camping mace."), false);
                return false;
            }
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
                
                // Set the UUID in the camp anchor block entity
                setCampAnchorUuid(serverWorld, spawnPos, bindUuid);
                
                // Register the portal location as the player's camp
                if (context.getPlayer() instanceof ServerPlayerEntity serverPlayer) {
                    Settlement settlement = new Settlement(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                    Manager.addPlayerSettlement(serverPlayer.getUuid(), settlement);
                    serverPlayer.sendMessage(Text.literal("Portal camp created! Use the camp anchor to access your settlement."), false);
                }
                
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

    private static void setCampAnchorUuid(ServerWorld world, BlockPos portalPos, UUID bindUuid) {
        // Search for camp anchor blocks in a 5x5x5 area around the portal
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 3; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = portalPos.add(x, y, z);
                    BlockEntity blockEntity = world.getBlockEntity(checkPos);
                    if (blockEntity instanceof CampAnchorBlockEntity anchorEntity) {
                        anchorEntity.setBindUuid(bindUuid);
                        AtemporalSettlement.LOGGER.info("Set camp anchor UUID to {} at {}", bindUuid, checkPos);
                        return;
                    }
                }
            }
        }
        AtemporalSettlement.LOGGER.warn("Could not find camp anchor block to set UUID");
    }
}