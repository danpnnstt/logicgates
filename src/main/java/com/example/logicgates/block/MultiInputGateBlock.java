package com.example.logicgates.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Base class for 5-input logic gate multi-blocks.
 *
 * Layout (FACING = direction the OUTPUT goes, e.g. NORTH):
 *
 *   [ O ]                         ← output dust goes here  (1 block behind center in FACING dir)
 *   [x x x x x x x x x]          ← 9 body blocks (LEFT_4 … CENTER … RIGHT_4)
 *   [I1]  [I2]  [I3]  [I4]  [I5] ← input dust positions   (1 block in front of center)
 *
 * "Lateral" = perpendicular to FACING direction, horizontally.
 * LEFT  = negative lateral offset when output faces NORTH/SOUTH (west side).
 * RIGHT = positive lateral offset (east side).
 *
 * The CENTER part holds the redstone logic. Slave parts have no logic.
 *
 * Subclasses declare their size by overriding getBodyParts() and getInputOffsets().
 * 3-input: body parts LEFT_2..RIGHT_2, inputs at offsets -2, 0, +2
 * 4-input: body parts LEFT_3..RIGHT_3, inputs at offsets -3, -1, +1, +3
 * 5-input: body parts LEFT_4..RIGHT_4, inputs at offsets -4, -2, 0, +2, +4
 */
public abstract class MultiInputGateBlock extends Block {

    public static final DirectionProperty FACING  = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty   POWERED  = BlockStateProperties.POWERED;
    public static final EnumProperty<GatePart> PART = EnumProperty.create("part", GatePart.class);

    // Thin slab shape (same height as a comparator ~2px)
    protected static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);

    protected MultiInputGateBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING,  Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(PART,    GatePart.CENTER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, PART);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    // -------------------------------------------------------------------------
    // Placement
    // -------------------------------------------------------------------------

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction facing = ctx.getHorizontalDirection().getOpposite();
        return this.defaultBlockState()
                .setValue(FACING,  facing)
                .setValue(POWERED, false)
                .setValue(PART,    GatePart.CENTER);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos centerPos, BlockState state,
                            LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) return;
        Direction facing  = state.getValue(FACING);
        Direction lateral = getLateralDirection(facing); // positive lateral direction

        // Place the companion blocks (all body parts except CENTER)
        for (GatePart part : getBodyParts()) {
            if (part == GatePart.CENTER) continue;
            BlockPos partPos = centerPos.relative(lateral, part.offset);
            // Only place if the space is free (air/replaceable)
            if (level.getBlockState(partPos).canBeReplaced()) {
                level.setBlock(partPos,
                        this.defaultBlockState()
                                .setValue(FACING,  facing)
                                .setValue(POWERED, false)
                                .setValue(PART,    part),
                        Block.UPDATE_ALL);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Destruction — break the whole gate when any part is removed
    // -------------------------------------------------------------------------

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            removeAllParts(level, pos, state, player.isCreative());
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    private void removeAllParts(Level level, BlockPos brokenPos, BlockState brokenState, boolean creative) {
        Direction facing  = brokenState.getValue(FACING);
        Direction lateral = getLateralDirection(facing);
        GatePart  thisPart = brokenState.getValue(PART);

        // Find the center pos from this part's offset
        BlockPos centerPos = brokenPos.relative(lateral, -thisPart.offset);

        for (GatePart part : getBodyParts()) {
            if (part == thisPart) continue; // already being broken
            BlockPos partPos = centerPos.relative(lateral, part.offset);
            BlockState partState = level.getBlockState(partPos);
            if (partState.getBlock() == this && partState.getValue(PART) == part) {
                if (creative) {
                    level.removeBlock(partPos, false);
                } else {
                    level.destroyBlock(partPos, false); // no drops for companion parts
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Redstone — only the CENTER part outputs a signal
    // -------------------------------------------------------------------------

    @Override
    public boolean isSignalSource(BlockState state) {
        return state.getValue(PART) == GatePart.CENTER;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
        if (state.getValue(PART) != GatePart.CENTER) return 0;
        if (!state.getValue(POWERED)) return 0;
        // Output only in the FACING direction
        return dir == state.getValue(FACING).getOpposite() ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
        return getSignal(state, level, pos, dir);
    }

    // -------------------------------------------------------------------------
    // Neighbor updates
    // Any part that detects a neighbor change finds the CENTER and recalculates.
    // This is necessary because inputs I1/I2/I4/I5 are adjacent to slave parts
    // (LEFT_4, LEFT_2, RIGHT_2, RIGHT_4), not to the CENTER block.
    // -------------------------------------------------------------------------

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) return;

        GatePart part = state.getValue(PART);
        BlockPos centerPos;
        BlockState centerState;

        if (part == GatePart.CENTER) {
            centerPos   = pos;
            centerState = state;
        } else {
            // Locate the center block from this slave's offset
            Direction facing  = state.getValue(FACING);
            Direction lateral = getLateralDirection(facing);
            centerPos   = pos.relative(lateral, -part.offset);
            centerState = level.getBlockState(centerPos);
            // Bail out if center has been removed or replaced
            if (!(centerState.getBlock() instanceof MultiInputGateBlock)
                    || centerState.getValue(PART) != GatePart.CENTER) return;
        }

        recalculate(level, centerPos, centerState);
    }

    protected void recalculate(Level level, BlockPos centerPos, BlockState state) {
        boolean shouldPower = computeOutput(level, centerPos, state);
        if (state.getValue(POWERED) != shouldPower) {
            level.setBlock(centerPos, state.setValue(POWERED, shouldPower), Block.UPDATE_ALL);
        }
    }

    /**
     * Reads the 5 input signals.
     * Input positions are 1 block in front of the gate body (opposite to FACING),
     * at lateral offsets -4, -2, 0, +2, +4 from the CENTER.
     *
     * Signal reading mirrors DiodeBlock: first try level.getSignal (covers levers,
     * repeaters, etc.), then fall back to RedStoneWireBlock.POWER so that redstone
     * dust that isn't physically connected toward the gate still registers.
     */
    protected int[] readInputSignals(Level level, BlockPos centerPos, BlockState state) {
        Direction facing  = state.getValue(FACING);
        Direction front   = facing.getOpposite(); // inputs come from this direction
        Direction lateral = getLateralDirection(facing);

        int[] inputOffsets = getInputOffsets();
        int[] inputs = new int[inputOffsets.length];
        for (int i = 0; i < 5; i++) {
            // inputPos is 1 block in front of the corresponding body block
            BlockPos inputPos = centerPos.relative(front).relative(lateral, inputOffsets[i]);
            // Ask the block at inputPos what signal it provides toward the gate (facing direction)
            int sig = level.getSignal(inputPos, facing);
            // If the signal is 0, check whether it's unpowered-but-existing redstone wire
            if (sig == 0) {
                BlockState bs = level.getBlockState(inputPos);
                if (bs.is(Blocks.REDSTONE_WIRE)) {
                    sig = bs.getValue(RedStoneWireBlock.POWER);
                }
            }
            inputs[i] = sig;
        }
        return inputs;
    }

    /** Returns every GatePart that belongs to this gate's body, including CENTER. */
    protected abstract GatePart[] getBodyParts();

    /** Returns the lateral offsets (from CENTER) at which inputs are read, in order I1..In. */
    protected abstract int[] getInputOffsets();

    /** Subclasses implement the gate truth table here. */
    protected abstract boolean computeOutput(Level level, BlockPos centerPos, BlockState state);

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the direction that positive lateral offset maps to, given FACING.
     * FACING NORTH → lateral is EAST
     * FACING SOUTH → lateral is WEST
     * FACING EAST  → lateral is SOUTH
     * FACING WEST  → lateral is NORTH
     */
    public static Direction getLateralDirection(Direction facing) {
        return switch (facing) {
            case NORTH -> Direction.EAST;
            case SOUTH -> Direction.WEST;
            case EAST  -> Direction.SOUTH;
            case WEST  -> Direction.NORTH;
            default    -> Direction.EAST;
        };
    }
}
