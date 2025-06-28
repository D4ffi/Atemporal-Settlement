package com.bydaffi.atemp;

import com.bydaffi.atemp.block.AtempBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class AtemporalSettlementClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Configurar el render layer para transparencia
        BlockRenderLayerMap.INSTANCE.putBlock(AtempBlocks.CAMP_ANCHOR, RenderLayer.getCutout());
    }
}