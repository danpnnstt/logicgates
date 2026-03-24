package com.example.logicgates;

import com.example.logicgates.block.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(LogicGatesMod.MOD_ID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(LogicGatesMod.MOD_ID);

    // 2-input gate properties (thin flat block, like a comparator)
    private static BlockBehaviour.Properties gateProps() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.COMPARATOR);
    }

    // 5-input gate properties (solid slab, iron-like)
    private static BlockBehaviour.Properties bigGateProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .requiresCorrectToolForDrops()
                .strength(3.0f, 6.0f)
                .sound(SoundType.METAL)
                .noOcclusion();
    }

    // -------------------------------------------------------------------------
    // 2-input gates (AND, OR, XOR, NOT)
    // -------------------------------------------------------------------------

    public static final DeferredBlock<AndGateBlock> AND_GATE =
            BLOCKS.register("and_gate", () -> new AndGateBlock(gateProps()));

    public static final DeferredBlock<OrGateBlock> OR_GATE =
            BLOCKS.register("or_gate", () -> new OrGateBlock(gateProps()));

    public static final DeferredBlock<XorGateBlock> XOR_GATE =
            BLOCKS.register("xor_gate", () -> new XorGateBlock(gateProps()));

    public static final DeferredBlock<NotGateBlock> NOT_GATE =
            BLOCKS.register("not_gate", () -> new NotGateBlock(gateProps()));

    // -------------------------------------------------------------------------
    // 5-input gates
    // -------------------------------------------------------------------------

    public static final DeferredBlock<And5GateBlock> AND5_GATE =
            BLOCKS.register("and5_gate", () -> new And5GateBlock(bigGateProps()));

    public static final DeferredBlock<Or5GateBlock> OR5_GATE =
            BLOCKS.register("or5_gate", () -> new Or5GateBlock(bigGateProps()));

    public static final DeferredBlock<Xor5GateBlock> XOR5_GATE =
            BLOCKS.register("xor5_gate", () -> new Xor5GateBlock(bigGateProps()));

    // -------------------------------------------------------------------------
    // Block Items
    // -------------------------------------------------------------------------

    public static final DeferredItem<BlockItem> AND_GATE_ITEM =
            ITEMS.register("and_gate", () -> new BlockItem(AND_GATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> OR_GATE_ITEM =
            ITEMS.register("or_gate", () -> new BlockItem(OR_GATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> XOR_GATE_ITEM =
            ITEMS.register("xor_gate", () -> new BlockItem(XOR_GATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> NOT_GATE_ITEM =
            ITEMS.register("not_gate", () -> new BlockItem(NOT_GATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> AND5_GATE_ITEM =
            ITEMS.register("and5_gate", () -> new BlockItem(AND5_GATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> OR5_GATE_ITEM =
            ITEMS.register("or5_gate", () -> new BlockItem(OR5_GATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> XOR5_GATE_ITEM =
            ITEMS.register("xor5_gate", () -> new BlockItem(XOR5_GATE.get(), new Item.Properties()));
}
