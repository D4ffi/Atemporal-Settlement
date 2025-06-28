package com.bydaffi.atemp.util;

import com.bydaffi.atemp.AtemporalSettlement;
import com.bydaffi.atemp.dimension.AtempDimensions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
        if (Manager.hasPlayerSettlement(player.getUuid())) {
            teleportToExistingSettlement(player);
        } else {
            createNewSettlement(player);
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

    private static void createNewSettlement(ServerPlayerEntity player) {
        ServerWorld campWorld = AtempDimensions.getCampDimension(Objects.requireNonNull(player.getServer()));
        if (campWorld == null) return;

        BlockPos spawnPos = findValidSpawnPosition(campWorld);
        spawnSmallCampStructure(campWorld, spawnPos);
        
        Settlement settlement = new Settlement(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        Manager.addPlayerSettlement(player.getUuid(), settlement);
        
        Vec3d teleportPos = new Vec3d(spawnPos.getX() + 5.5, spawnPos.getY() + 2, spawnPos.getZ() + 6.5);
        player.teleport(campWorld, teleportPos.x, teleportPos.y, teleportPos.z, player.getYaw(), player.getPitch());
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

            StructureTemplate template = templateManager.getTemplateOrBlank(structureId);
            if (template != null) {
                StructurePlacementData placementData = new StructurePlacementData();
                placementData.setRotation(BlockRotation.NONE);
                placementData.setMirror(BlockMirror.NONE);
                placementData.setIgnoreEntities(false);
                
                template.place(world, pos, pos, placementData, world.getRandom(), 2);
                AtemporalSettlement.LOGGER.info("Successfully placed mini camp structure at {}", pos);
            } else {
                AtemporalSettlement.LOGGER.warn("Could not load mini_camp structure template");
            }
        } catch (Exception e) {
            AtemporalSettlement.LOGGER.error("Failed to spawn mini camp structure", e);
        }
    }
}