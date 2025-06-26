package com.bydaffi.atemp.item;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class CopperMace extends CampingMace {
    public CopperMace(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return super.useOnBlock(context);
    }

    @Override
    public boolean canCreateCamp(ItemUsageContext context) {
        String currentBlock = context.getWorld().getBlockState(context.getBlockPos()).getBlock().toString();
        return currentBlock.equals(campableBlocks[0]);
    }
}
