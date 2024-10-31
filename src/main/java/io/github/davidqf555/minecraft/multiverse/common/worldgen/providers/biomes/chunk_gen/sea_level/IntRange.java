package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public class IntRange {

    public static final Codec<IntRange> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("min").forGetter(IntRange::getMin),
            Codec.INT.fieldOf("max").forGetter(IntRange::getMax)
    ).apply(inst, IntRange::of));
    public static final Codec<IntRange> POSITIVE_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("min").forGetter(IntRange::getMin),
            ExtraCodecs.POSITIVE_INT.fieldOf("max").forGetter(IntRange::getMax)
    ).apply(inst, IntRange::of));
    private final int min, max;

    protected IntRange(int min, int max) {
        this.min = min;
        this.max = max;
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
