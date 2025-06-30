package com.bydaffi.atemp.event;

import com.bydaffi.atemp.util.Manager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;

public class WorldEventHandler {
    
    public static void register() {
        // Save data when server is stopping
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            Manager.saveData();
        });
        
        // Save data when world is unloaded
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            if (world.getRegistryKey() == net.minecraft.world.World.OVERWORLD) {
                Manager.unloadWorld();
            }
        });
        
        // Initialize data when world is loaded
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == net.minecraft.world.World.OVERWORLD) {
                Manager.initializeWorld(world);
            }
        });
    }
}