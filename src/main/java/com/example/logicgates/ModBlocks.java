package com.example.logicgates;

import com.example.logicgates.block.AndGateBlock;
import com.example.logicgates.block.NotGateBlock;
import com.example.logicgates.block.OrGateBlock;
import com.example.logicgates.block.XorGateBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(LogicGatesMod.MOD_ID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(LogicGatesMod.MOD_ID);

    // Copy the behavior properties from the vanilla comparator (thin, no collision, instabreak, etc.)
    private static BlockBehaviour.Properties gateProps() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.COMPARATOR);
    }

    // --- Blocks ---

    public static final DeferredBlock<AndGateBlock> AND_GATE =
            BLOCKS.register("and_gate", () -> new AndGateBlock(gateProps()));

    public static final DeferredBlock<OrGateBlock> OR_GATE =
            BLOCKS.register("or_gate", () -> new OrGateBlock(gateProps()));

    public static final DeferredBlock<XorGateBlock> XOR_GATE =
            BLOCKS.register("xor_gate", () -> new XorGateBlock(gateProps()));

    public static final DeferredBlock<NotGateBlock> NOT_GATE =
            BLOCKS.register("not_gate", () -> new NotGateBlock(gateProps()));

    // --- Block Items ---

    public static final DeferredItem<BlockItem> AND_GATE_ITEM =
            ITEMS.register("and_gate", () -> new BlockItem(AND_GATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> OR_GATE_ITEM =
            ITEMS.register("or_gate", () -> new BlockItem(OR_GATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> XOR_GATE_ITEM =
            ITEMS.register("xor_gate", () -> new BlockItem(XOR_GATE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> NOT_GATE_ITEM =
            ITEMS.register("not_gate", () -> new BlockItem(NOT_GATE.get(), new Item.Properties()));
}
