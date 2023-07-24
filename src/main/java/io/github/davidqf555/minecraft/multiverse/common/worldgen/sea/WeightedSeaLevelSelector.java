package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;

import java.util.Arrays;
import java.util.Random;

public class WeightedSeaLevelSelector implements SeaLevelSelector {

    private static final Random RANDOM = new Random(0);
    private final SeaLevelSelector[] selectors;
    private final int[] weights;

    public WeightedSeaLevelSelector(SeaLevelSelector[] selectors, int[] weights) {
        if (selectors.length != weights.length) {
            throw new IllegalArgumentException();
        }
        this.selectors = selectors;
        this.weights = weights;
    }

    @Override
    public Aquifer.FluidPicker getSeaLevel(BlockState block, long seed, int index) {
        int total = Arrays.stream(weights).reduce(Integer::sum).orElseThrow();
        RANDOM.setSeed(seed + index * 6000000L);
        int rand = RANDOM.nextInt(total);
        for (int i = 0; i < selectors.length; i++) {
            total -= weights[i];
            if (rand >= total) {
                return selectors[i].getSeaLevel(block, seed, index);
            }
        }
        throw new RuntimeException();
    }

}
