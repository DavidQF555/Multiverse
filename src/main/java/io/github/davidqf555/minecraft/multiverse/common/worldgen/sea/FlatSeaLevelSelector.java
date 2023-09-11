package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.FlatFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class FlatSeaLevelSelector implements SeaLevelSelector {

    private static final Random RANDOM = new Random(0);
    private final IntRange range;

    protected FlatSeaLevelSelector(IntRange range) {
        this.range = range;
    }

    public static FlatSeaLevelSelector of(int min, int max) {
        return new FlatSeaLevelSelector(IntRange.of(min, max));
    }

    @Override
    public SerializableFluidPicker getSeaLevel(BlockState fluid, long seed, int index) {
        RANDOM.setSeed(seed + 1000000L * index);
        return new FlatFluidPicker(range.getRandom(RANDOM), fluid);
    }


}
