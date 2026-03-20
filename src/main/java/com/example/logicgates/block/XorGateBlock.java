package com.example.logicgates.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * XOR Gate: outputs 15 when EXACTLY ONE input is active (> 0).
 *
 * Orientation relative to FACING (the direction the input comes from):
 *   - Input 1 : FACING direction (rear of the gate)
 *   - Input 2 : either side (left or right — whichever is stronger)
 *   - Output  : FACING.getOpposite() (front of the gate)
 */
public class XorGateBlock extends DiodeBlock {

    public static final MapCodec<XorGateBlock> CODEC = simpleCodec(XorGateBlock::new);

    public XorGateBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, Boolean.FALSE)
        );
    }

    @Override
    public MapCodec<XorGateBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    protected int getDelay(BlockState state) {
        return 2;
    }

    @Override
    protected boolean shouldTurnOn(Level level, BlockPos pos, BlockState state) {
        boolean input1 = this.getInputSignal(level, pos, state) > 0;       // rear
        boolean input2 = this.getAlternateSignal(level, pos, state) > 0;   // sides
        return input1 ^ input2;
    }
}
