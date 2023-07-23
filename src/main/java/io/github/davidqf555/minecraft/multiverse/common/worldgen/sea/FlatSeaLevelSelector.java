package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;

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
    public Aquifer.FluidPicker getSeaLevel(BlockState fluid, long seed, int index) {
        RANDOM.setSeed(seed + 1000000L * index);
        return new FlatFluidPicker(range.getRandom(RANDOM), fluid);
    }

    private static class FlatFluidPicker implements Aquifer.FluidPicker {

        private final Aquifer.FluidStatus fluid;

        private FlatFluidPicker(int level, BlockState fluid) {
            this.fluid = new Aquifer.FluidStatus(level, fluid);
        }

        @Override
        public Aquifer.FluidStatus computeFluid(int x, int y, int z) {
            return fluid;
        }

    }

}
