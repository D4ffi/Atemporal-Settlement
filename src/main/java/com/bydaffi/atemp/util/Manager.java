package com.bydaffi.atemp.util;

import net.minecraft.server.world.ServerWorld;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Manager {
    private static final Map<UUID, Settlement> playerSettlements = new HashMap<>();
    private static ServerWorld currentWorld = null;
    private static boolean dataLoaded = false;

    public static void addPlayerSettlement(UUID playerUuid, Settlement settlement) {
        playerSettlements.put(playerUuid, settlement);
        saveData();
    }

    public static Settlement getPlayerSettlement(UUID playerUuid) {
        return playerSettlements.get(playerUuid);
    }

    public static boolean hasPlayerSettlement(UUID playerUuid) {
        return playerSettlements.containsKey(playerUuid);
    }

    public static void removePlayerSettlement(UUID playerUuid) {
        playerSettlements.remove(playerUuid);
        saveData();
    }

    public static Collection<Settlement> getAllSettlements() {
        return playerSettlements.values();
    }
    
    public static void initializeWorld(ServerWorld world) {
        if (currentWorld != world || !dataLoaded) {
            currentWorld = world;
            loadData();
            dataLoaded = true;
        }
    }
    
    public static void saveData() {
        if (currentWorld != null) {
            WorldDataManager.saveSettlements(currentWorld, playerSettlements);
        }
    }
    
    private static void loadData() {
        if (currentWorld != null) {
            playerSettlements.clear();
            Map<UUID, Settlement> loadedData = WorldDataManager.loadSettlements(currentWorld);
            playerSettlements.putAll(loadedData);
        }
    }
    
    public static void unloadWorld() {
        saveData();
        currentWorld = null;
        dataLoaded = false;
        playerSettlements.clear();
    }
}