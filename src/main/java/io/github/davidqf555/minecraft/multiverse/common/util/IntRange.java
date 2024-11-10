package io.github.davidqf555.minecraft.multiverse.common.util;

import com.google.common.base.Functions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public class IntRange {

    public static final Codec<IntRange> CODEC = RecordCodecBuilder.<IntRange>create(inst -> inst.group(
            Codec.INT.fieldOf("min").forGetter(IntRange::getMin),
            Codec.INT.fieldOf("max").forGetter(IntRange::getMax)
    ).apply(inst, IntRange::of)).flatComapMap(Functions.identity(), range -> {
        if (range.getMin() > range.getMax()) {
            return DataResult.error("Minimum cannot be greater than maximum");
        }
        return DataResult.success(range);
    });
    public static final Codec<IntRange> POSITIVE_CODEC = RecordCodecBuilder.<IntRange>create(inst -> inst.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("min").forGetter(IntRange::getMin),
            ExtraCodecs.POSITIVE_INT.fieldOf("max").forGetter(IntRange::getMax)
    ).apply(inst, IntRange::of)).flatComapMap(Functions.identity(), range -> {
        if (range.getMin() > range.getMax()) {
            return DataResult.error("Minimum cannot be greater than maximum");
        }
        return DataResult.success(range);
    });
    private final int min, max;

    protected IntRange(int min, int max) {
        this.min = min;
        this.max = max;
        if (min > max) {
            throw new IllegalArgumentException("Minimum cannot be greater than maximum");
        }
    }

    public static IntRange of(int min, int max) {
        return new IntRange(min, max);
    }

    public int getRandom(RandomSource random) {
        return getMin() + random.nextInt(getMax() - getMin() + 1);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

}
