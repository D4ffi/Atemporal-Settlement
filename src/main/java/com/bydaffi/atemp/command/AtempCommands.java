package com.bydaffi.atemp.command;

import com.bydaffi.atemp.AtemporalSettlement;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AtempCommands {

    public static final RegistryKey<World> CAMP_DIMENSION = RegistryKey.of(
            RegistryKeys.WORLD,
            new Identifier("atemp", "camp_dimension")
    );

    public static void register() {
        CommandRegistrationCallback.EVENT.register(AtempCommands::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
                                         CommandRegistryAccess registryAccess,
                                         CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("goCamp")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(AtempCommands::executeTeleportToCamp));

        // Comando de debug para listar dimensiones
        dispatcher.register(CommandManager.literal("listDimensions")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(AtempCommands::listDimensions));
    }

    private static int executeTeleportToCamp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try {
            ServerPlayerEntity player = source.getPlayerOrThrow();
            MinecraftServer server = source.getServer();

            // Debug: Listar todas las dimensiones disponibles
            AtemporalSettlement.LOGGER.info("Dimensiones disponibles:");
            for (RegistryKey<World> worldKey : server.getWorldRegistryKeys()) {
                AtemporalSettlement.LOGGER.info("- " + worldKey.getValue().toString());
            }

            ServerWorld campWorld = server.getWorld(CAMP_DIMENSION);

            if (campWorld == null) {
                AtemporalSettlement.LOGGER.error("La dimensión del campamento no se encontró: " + CAMP_DIMENSION.getValue());
                source.sendFeedback(() -> Text.literal("§cError: La dimensión del campamento no está disponible."), false);
                source.sendFeedback(() -> Text.literal("§cVerifica que los archivos JSON estén en la carpeta correcta."), false);
                return 0;
            }

            // Teleportar al jugador al spawn de la dimensión (0, 100, 0)
            Vec3d spawnPos = new Vec3d(0.5, 100, 0.5);
            player.teleport(campWorld, spawnPos.x, spawnPos.y, spawnPos.z, 0.0f, 0.0f);

            source.sendFeedback(() -> Text.literal("§a¡Teletransportado al campamento!"), true);
            return 1;

        } catch (Exception e) {
            AtemporalSettlement.LOGGER.error("Error al teletransportar: ", e);
            source.sendFeedback(() -> Text.literal("§cError al teletransportar: " + e.getMessage()), false);
            return 0;
        }
    }

    private static int listDimensions(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();

        source.sendFeedback(() -> Text.literal("§eDimensiones disponibles:"), false);

        for (RegistryKey<World> worldKey : server.getWorldRegistryKeys()) {
            String dimName = worldKey.getValue().toString();
            source.sendFeedback(() -> Text.literal("§7- " + dimName), false);
        }

        return 1;
    }
}