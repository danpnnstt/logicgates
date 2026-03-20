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
 * NOT Gate (Inverter): outputs 15 when the rear input is 0, and 0 when the rear input is active.
 * Side inputs are ignored.
 *
 * Orientation relative to FACING (the direction the input comes from):
 *   - Input  : FACING direction (rear of the gate)
 *   - Output : FACING.getOpposite() (front of the gate)
 */
public class NotGateBlock extends DiodeBlock {

    public static final MapCodec<NotGateBlock> CODEC = simpleCodec(NotGateBlock::new);

    public NotGateBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, Boolean.FALSE)
        );
    }

    @Override
    public MapCodec<NotGateBlock> codec() {
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
        // Inverts the rear input — side inputs are ignored
        return this.getInputSignal(level, pos, state) == 0;
    }
}
