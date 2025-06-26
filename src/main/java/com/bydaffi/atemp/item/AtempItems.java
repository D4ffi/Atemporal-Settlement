package com.bydaffi.atemp.item;

import com.bydaffi.atemp.AtemporalSettlement;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class AtempItems {

    public static final Item COPPER_MACE = registerItem("copper_mace", new CopperMace(new FabricItemSettings()));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(AtemporalSettlement.MOD_ID,name), item);
    }

    public static void registerItems() {
        AtemporalSettlement.LOGGER.info("Items registered for Atemporal Settlement <3.");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(AtempItems::itemGroupTools);
    }

    private static void itemGroupTools(FabricItemGroupEntries entries) {
        entries.add(COPPER_MACE);
    }

}
