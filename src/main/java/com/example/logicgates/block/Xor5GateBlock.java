package com.example.logicgates.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** 5-input XOR gate: output is 15 when an ODD number of inputs are active. */
public class Xor5GateBlock extends MultiInputGateBlock {

    public static final MapCodec<Xor5GateBlock> CODEC = simpleCodec(Xor5GateBlock::new);

    public Xor5GateBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public MapCodec<Xor5GateBlock> codec() {
        return CODEC;
    }

    private static final GatePart[] PARTS = {
        GatePart.LEFT_4, GatePart.LEFT_3, GatePart.LEFT_2, GatePart.LEFT_1, GatePart.CENTER,
        GatePart.RIGHT_1, GatePart.RIGHT_2, GatePart.RIGHT_3, GatePart.RIGHT_4
    };
    private static final int[] OFFSETS = {-4, -2, 0, 2, 4};

    @Override protected GatePart[] getBodyParts()   { return PARTS; }
    @Override protected int[]      getInputOffsets() { return OFFSETS; }

    @Override
    protected boolean computeOutput(Level level, BlockPos centerPos, BlockState state) {
        int[] inputs = readInputSignals(level, centerPos, state);
        int active = 0;
        for (int sig : inputs) {
            if (sig > 0) active++;
        }
        return active == 1; // exactly one input active
    }
}
