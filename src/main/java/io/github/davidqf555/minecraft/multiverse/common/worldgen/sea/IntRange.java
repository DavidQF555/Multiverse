package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea;

import java.util.Random;

public class IntRange {

    private final int min, max;

    protected IntRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static IntRange of(int min, int max) {
        return new IntRange(min, max);
    }

    public int getRandom(Random random) {
        return getMin() + random.nextInt(getMax() - getMin() + 1);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

}
