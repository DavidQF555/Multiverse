package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Random;

public class WeightedSeaLevelSelector implements SeaLevelSelector {

    private static final Random RANDOM = new Random(0);
    private final Map<SeaLevelSelector, Integer> selectors;

    public WeightedSeaLevelSelector(Map<SeaLevelSelector, Integer> selectors) {
        this.selectors = selectors;
    }

    @Override
    public SerializableFluidPicker getSeaLevel(BlockState block, long seed, int index) {
        int total = selectors.values().stream().reduce(Integer::sum).orElseThrow();
        RANDOM.setSeed(seed + index * 6000000L);
        int rand = RANDOM.nextInt(total);
        for (SeaLevelSelector selector : selectors.keySet()) {
            total -= selectors.get(selector);
            if (rand >= total) {
                return selector.getSeaLevel(block, seed, index);
            }
        }
        throw new RuntimeException();
    }

}
