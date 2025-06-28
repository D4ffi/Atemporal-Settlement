package com.bydaffi.atemp.item;

import com.bydaffi.atemp.AtemporalSettlement;
import com.bydaffi.atemp.util.PortalSpawner;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class CampingMace extends Item {

    public static String[] campableBlocks = {
        "Block{minecraft:campfire}",
        "Block{minecraft:soul_campfire}",
    };


    public CampingMace(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (canCreateCamp(context)) {
            boolean success = PortalSpawner.spawnPortalStructure(context);
            return success ? ActionResult.success(true) : ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }

    public boolean canCreateCamp(ItemUsageContext context) {
        String currentBlock = context.getWorld().getBlockState(context.getBlockPos()).getBlock().toString();
        return currentBlock.equals(campableBlocks[0]) || currentBlock.equals(campableBlocks[1]);
    }

    public boolean hasValidCampArea(ItemUsageContext context) {
        BlockPos campfirePos = context.getBlockPos();
        
        // Check 5x5x5 area around campfire (2 blocks in each direction)
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos checkPos = campfirePos.add(x, y, z);
                    
                    // Skip the campfire itself
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    
                    // Check if block is solid (not air or passable)
                    if (!context.getWorld().getBlockState(checkPos).isAir() && 
                        !context.getWorld().getBlockState(checkPos).isReplaceable()) {
                        return false;
                    }
                }
            }
        }
        
        // Check 3x3 area below campfire (1 block in each horizontal direction, 1 block down)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos checkPos = campfirePos.add(x, -1, z);
                
                // Must have solid ground below
                if (context.getWorld().getBlockState(checkPos).isAir() || 
                    context.getWorld().getBlockState(checkPos).isReplaceable()) {
                    return false;
                }
            }
        }
        
        return true;
    }




}
