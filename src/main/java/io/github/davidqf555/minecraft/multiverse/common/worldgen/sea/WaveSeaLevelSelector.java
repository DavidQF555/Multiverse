package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;

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
    public Aquifer.FluidPicker getSeaLevel(BlockState block, long seed, int index) {
        RANDOM.setSeed(seed + index * 1000000L);
        return new WaveFluidPicker(block, center.getRandom(RANDOM), amplitude.getRandom(RANDOM), period.getRandom(RANDOM));
    }

    private static class WaveFluidPicker implements Aquifer.FluidPicker {

        private final Aquifer.FluidStatus[][] states;

        private WaveFluidPicker(BlockState fluid, int center, int amplitude, int period) {
            states = new Aquifer.FluidStatus[period][period];
            for (int i = 0; i < period; i++) {
                float x = Mth.cos(Mth.TWO_PI * i / period) * amplitude / 2f;
                for (int j = 0; j < period; j++) {
                    float z = Mth.cos(Mth.TWO_PI * j / period) * amplitude / 2f;
                    states[i][j] = new Aquifer.FluidStatus(center + (int) (x + z + 0.5f), fluid);
                }
            }
        }

        @Override
        public Aquifer.FluidStatus computeFluid(int x, int y, int z) {
            return states[Math.floorMod(x, states.length)][Math.floorMod(z, states.length)];
        }

    }

}
