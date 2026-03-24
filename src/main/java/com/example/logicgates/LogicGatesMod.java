package com.example.logicgates;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(LogicGatesMod.MOD_ID)
public class LogicGatesMod {

    public static final String MOD_ID = "logicgates";

    public LogicGatesMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);

        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ModBlocks.AND_GATE_ITEM);
            event.accept(ModBlocks.OR_GATE_ITEM);
            event.accept(ModBlocks.XOR_GATE_ITEM);
            event.accept(ModBlocks.NOT_GATE_ITEM);
            event.accept(ModBlocks.AND5_GATE_ITEM);
            event.accept(ModBlocks.OR5_GATE_ITEM);
            event.accept(ModBlocks.XOR5_GATE_ITEM);
        }
    }
}
