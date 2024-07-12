package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.FlatFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class FlatSeaLevelSelector implements SeaLevelSelector {

    public static final MapCodec<FlatSeaLevelSelector> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            IntRange.CODEC.fieldOf("range").forGetter(selector -> selector.range)
    ).apply(inst, FlatSeaLevelSelector::new));
    private static final Random RANDOM = new Random(0);
    private final IntRange range;

    public FlatSeaLevelSelector(IntRange range) {
        this.range = range;
    }

    @Override
    public SerializableFluidPicker getSeaLevel(BlockState fluid, long seed, int index) {
        RANDOM.setSeed(seed + 1000000L * index);
        return new FlatFluidPicker(range.getRandom(RANDOM), fluid);
    }

    @Override
    public MapCodec<? extends FlatSeaLevelSelector> codec() {
        return CODEC;
    }

}
