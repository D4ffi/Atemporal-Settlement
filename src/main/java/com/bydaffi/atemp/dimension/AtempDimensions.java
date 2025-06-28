package com.bydaffi.atemp.dimension;

import com.bydaffi.atemp.AtemporalSettlement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

public class AtempDimensions {

    public static final RegistryKey<World> CAMP_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD,
            new Identifier(AtemporalSettlement.MOD_ID, "camp_dimension"));

    public static final RegistryKey<DimensionType> CAMP_DIMENSION_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            new Identifier(AtemporalSettlement.MOD_ID, "camp_dimension_type"));

    public static final RegistryKey<Biome> CAMP_BIOME_KEY = RegistryKey.of(RegistryKeys.BIOME,
            new Identifier(AtemporalSettlement.MOD_ID, "camp_biome"));

    public static void registerDimensions() {
        AtemporalSettlement.LOGGER.info("Registering dimensions for " + AtemporalSettlement.MOD_ID);
    }

    public static ServerWorld getCampDimension(MinecraftServer server) {
        return server.getWorld(CAMP_WORLD_KEY);
    }
}