package com.example.logicgates.block;

import net.minecraft.util.StringRepresentable;

/**
 * Identifies which segment of a 9-wide multi-block gate this block represents.
 * Offsets are relative to the CENTER part, lateral to the gate's FACING direction.
 * LEFT = toward the gate's left when facing the output; RIGHT = toward the right.
 */
public enum GatePart implements StringRepresentable {
    LEFT_4("left_4",  -4),
    LEFT_3("left_3",  -3),
    LEFT_2("left_2",  -2),
    LEFT_1("left_1",  -1),
    CENTER("center",   0),
    RIGHT_1("right_1", 1),
    RIGHT_2("right_2", 2),
    RIGHT_3("right_3", 3),
    RIGHT_4("right_4", 4);

    private final String name;
    public final int offset; // lateral offset from center

    GatePart(String name, int offset) {
        this.name   = name;
        this.offset = offset;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    /** Returns the part whose lateral offset is the given value, or null if none. */
    public static GatePart fromOffset(int offset) {
        for (GatePart p : values()) {
            if (p.offset == offset) return p;
        }
        return null;
    }
}
