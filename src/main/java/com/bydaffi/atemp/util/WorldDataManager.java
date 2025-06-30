package com.bydaffi.atemp.util;

import com.bydaffi.atemp.AtemporalSettlement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.world.ServerWorld;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldDataManager {
    private static final String DATA_FILE_NAME = "atemp_settlements.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    private static File getDataFile(ServerWorld world) {
        File worldDir = world.getServer().getSavePath(net.minecraft.util.WorldSavePath.ROOT).toFile();
        return new File(worldDir, DATA_FILE_NAME);
    }
    
    public static void saveSettlements(ServerWorld world, Map<UUID, Settlement> settlements) {
        try {
            File dataFile = getDataFile(world);
            
            // Convert UUID keys to strings for JSON serialization
            Map<String, Settlement> stringKeyMap = new HashMap<>();
            for (Map.Entry<UUID, Settlement> entry : settlements.entrySet()) {
                stringKeyMap.put(entry.getKey().toString(), entry.getValue());
            }
            
            try (FileWriter writer = new FileWriter(dataFile)) {
                GSON.toJson(stringKeyMap, writer);
                AtemporalSettlement.LOGGER.info("Saved {} settlements to {}", settlements.size(), dataFile.getPath());
            }
        } catch (IOException e) {
            AtemporalSettlement.LOGGER.error("Failed to save settlements data", e);
        }
    }
    
    public static Map<UUID, Settlement> loadSettlements(ServerWorld world) {
        Map<UUID, Settlement> settlements = new HashMap<>();
        File dataFile = getDataFile(world);
        
        if (!dataFile.exists()) {
            AtemporalSettlement.LOGGER.info("No settlements data file found, starting with empty data");
            return settlements;
        }
        
        try (FileReader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<String, Settlement>>(){}.getType();
            Map<String, Settlement> stringKeyMap = GSON.fromJson(reader, type);
            
            if (stringKeyMap != null) {
                // Convert string keys back to UUIDs
                for (Map.Entry<String, Settlement> entry : stringKeyMap.entrySet()) {
                    try {
                        UUID playerUuid = UUID.fromString(entry.getKey());
                        settlements.put(playerUuid, entry.getValue());
                    } catch (IllegalArgumentException e) {
                        AtemporalSettlement.LOGGER.warn("Invalid UUID in settlements data: {}", entry.getKey());
                    }
                }
                AtemporalSettlement.LOGGER.info("Loaded {} settlements from {}", settlements.size(), dataFile.getPath());
            }
        } catch (IOException e) {
            AtemporalSettlement.LOGGER.error("Failed to load settlements data", e);
        }
        
        return settlements;
    }
    
    public static void deleteDataFile(ServerWorld world) {
        File dataFile = getDataFile(world);
        if (dataFile.exists()) {
            if (dataFile.delete()) {
                AtemporalSettlement.LOGGER.info("Deleted settlements data file");
            } else {
                AtemporalSettlement.LOGGER.warn("Failed to delete settlements data file");
            }
        }
    }
}