package com.bydaffi.atemp.util;

import com.bydaffi.atemp.AtemporalSettlement;
import com.bydaffi.atemp.dimension.AtempDimensions;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3i;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;
import java.util.Random;

public class StructureSpawner {
    private static final int MIN_DISTANCE = 1000;
    private static final Random RANDOM = new Random();

    public static void createOrTeleportToSettlement(ServerPlayerEntity player) {
        // Initialize world data if not already done
        Manager.initializeWorld(player.getServerWorld());
        
        if (Manager.hasPlayerSettlement(player.getUuid())) {
            Settlement settlement = Manager.getPlayerSettlement(player.getUuid());
            if (settlement.campCreated()) {
                AtemporalSettlement.LOGGER.info("Teleporting to existing camp");
                teleportToExistingSettlement(player);
            } else {
                AtemporalSettlement.LOGGER.info("First visit - creating camp in dimension");
                createCampInDimension(player, settlement);
            }
        } else {
            AtemporalSettlement.LOGGER.error("No settlement found for player - this should not happen when using camp anchor");
        }
    }

    private static void teleportToExistingSettlement(ServerPlayerEntity player) {
        Settlement settlement = Manager.getPlayerSettlement(player.getUuid());
        ServerWorld campWorld = AtempDimensions.getCampDimension(Objects.requireNonNull(player.getServer()));
        
        if (campWorld != null) {
            Vec3d pos = new Vec3d(settlement.x() + 4.5, settlement.y() + 2, settlement.z() + 4.5);
            player.teleport(campWorld, pos.x, pos.y, pos.z, player.getYaw(), player.getPitch());
        }
    }

    private static void createCampInDimension(ServerPlayerEntity player, Settlement portalSettlement) {
        ServerWorld campWorld = AtempDimensions.getCampDimension(Objects.requireNonNull(player.getServer()));
        if (campWorld == null) {
            AtemporalSettlement.LOGGER.error("Camp dimension is null - cannot create camp");
            return;
        }

        BlockPos spawnPos = findValidSpawnPosition(campWorld);
        AtemporalSettlement.LOGGER.info("Creating camp in dimension for {} at {}", player.getName().getString(), spawnPos);
        
        spawnSmallCampStructure(campWorld, spawnPos);
        
        // Update settlement with camp dimension coordinates and mark as created
        Settlement updatedSettlement = new Settlement(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), true);
        Manager.addPlayerSettlement(player.getUuid(), updatedSettlement);
        
        Vec3d teleportPos = new Vec3d(spawnPos.getX() + 5.5, spawnPos.getY() + 2, spawnPos.getZ() + 6.5);
        player.teleport(campWorld, teleportPos.x, teleportPos.y, teleportPos.z, player.getYaw(), player.getPitch());
        
        AtemporalSettlement.LOGGER.info("Teleported {} to new camp at {}", player.getName().getString(), teleportPos);
    }

    private static BlockPos findValidSpawnPosition(ServerWorld world) {
        int attempts = 0;
        while (attempts < 100) {
            int x = RANDOM.nextInt(20000) - 10000;
            int z = RANDOM.nextInt(20000) - 10000;
            
            if (isPositionValid(x, z)) {
                int y = world.getTopY() - 10;
                return new BlockPos(x, y, z);
            }
            attempts++;
        }
        return new BlockPos(0, 100, 0);
    }

    private static boolean isPositionValid(int x, int z) {
        for (Settlement settlement : Manager.getAllSettlements()) {
            double distance = Math.sqrt(Math.pow(x - settlement.x(), 2) + Math.pow(z - settlement.z(), 2));
            if (distance < MIN_DISTANCE) {
                return false;
            }
        }
        return true;
    }

    private static void spawnSmallCampStructure(ServerWorld world, BlockPos pos) {
        try {
            StructureTemplateManager templateManager = world.getStructureTemplateManager();
            Identifier structureId = new Identifier(AtemporalSettlement.MOD_ID, "small_camp");
            StructureTemplate template = null;

            try {
                template = templateManager.getTemplate(structureId).orElse(null);
            } catch (Exception e) {
                // Fallback to getTemplateOrBlank
                template = templateManager.getTemplateOrBlank(structureId);
                if (template != null && template.getSize().equals(Vec3i.ZERO)) {
                    template = null; // It's a blank template
                }
            }
            
            if (template != null && !template.getSize().equals(Vec3i.ZERO)) {
                StructurePlacementData placementData = new StructurePlacementData();
                placementData.setRotation(BlockRotation.NONE);
                placementData.setMirror(BlockMirror.NONE);
                placementData.setIgnoreEntities(false);
                
                template.place(world, pos, pos, placementData, world.getRandom(), 2);
                AtemporalSettlement.LOGGER.info("Campamento creado correctamente en {}", pos);
            } else {
                AtemporalSettlement.LOGGER.error("Error al crear el campamento: no se pudo cargar la estructura small_camp");
            }
        } catch (Exception e) {
            AtemporalSettlement.LOGGER.error("Error al crear el campamento en {}", pos, e);
        }
    }

    public static void deletePlayerCamp(ServerPlayerEntity player, BlockPos anchorPos) {
        // Initialize world data if not already done
        Manager.initializeWorld(player.getServerWorld());
        
        if (!Manager.hasPlayerSettlement(player.getUuid())) {
            return;
        }
        
        // Delete portal structure in overworld around the anchor block
        deletePortalCamp(player.getServerWorld(), anchorPos);
        
        // Remove settlement from manager (this allows creating new portal)
        Manager.removePlayerSettlement(player.getUuid());
        AtemporalSettlement.LOGGER.info("Removed settlement data for player: {}", player.getName().getString());
    }

    private static void deleteCampInDimension(ServerWorld world, BlockPos pos) {
        // Delete a 12x12x8 area around the camp structure
        for (int x = -6; x <= 6; x++) {
            for (int y = -1; y <= 6; y++) {
                for (int z = -6; z <= 6; z++) {
                    BlockPos deletePos = pos.add(x, y, z);
                    world.setBlockState(deletePos, Blocks.AIR.getDefaultState());
                }
            }
        }
    }
    
    private static void deletePortalCamp(ServerWorld world, BlockPos anchorPos) {
        // Delete a 5x5x5 area around the anchor block (portal structure)
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 3; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos deletePos = anchorPos.add(x, y+1, z);
                    world.setBlockState(deletePos, Blocks.AIR.getDefaultState());
                }
            }
        }
        AtemporalSettlement.LOGGER.info("Deleted portal structure at {}", anchorPos);
    }
}