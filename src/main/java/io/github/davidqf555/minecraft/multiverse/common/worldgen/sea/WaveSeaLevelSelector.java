package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.WaveFluidPicker;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class WaveSeaLevelSelector implements SeaLevelSelector {

    private static final Random RANDOM = new Random(0);
    private final IntRange center, amplitude, period;

    public WaveSeaLevelSelector(IntRange center, IntRange amplitude, IntRange period) {
        this.center = center;
        this.amplitude = amplitude;
        this.period = period;
    }

    @Override
    public SerializableFluidPicker getSeaLevel(BlockState block, long seed, int index) {
        RANDOM.setSeed(seed + index * 1000000L);
        return new WaveFluidPicker(block, center.getRandom(RANDOM), amplitude.getRandom(RANDOM), period.getRandom(RANDOM));
    }

}
