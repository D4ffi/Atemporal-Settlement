package com.bydaffi.atemp.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Manager {
    private static final Map<UUID, Settlement> playerSettlements = new HashMap<>();

    public static void addPlayerSettlement(UUID playerUuid, Settlement settlement) {
        playerSettlements.put(playerUuid, settlement);
    }

    public static Settlement getPlayerSettlement(UUID playerUuid) {
        return playerSettlements.get(playerUuid);
    }

    public static boolean hasPlayerSettlement(UUID playerUuid) {
        return playerSettlements.containsKey(playerUuid);
    }

    public static void removePlayerSettlement(UUID playerUuid) {
        playerSettlements.remove(playerUuid);
    }

    public static Collection<Settlement> getAllSettlements() {
        return playerSettlements.values();
    }
}