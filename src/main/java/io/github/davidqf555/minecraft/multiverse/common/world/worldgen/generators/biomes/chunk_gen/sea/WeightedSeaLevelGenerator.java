package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.sea.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.registration.custom.SeaLevelGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class WeightedSeaLevelGenerator implements SeaLevelGenerator {

    public static final MapCodec<WeightedSeaLevelGenerator> CODEC = Entry.CODEC.listOf().xmap(WeightedSeaLevelGenerator::new, entry -> entry.selectors).fieldOf("selectors");
    private final List<Entry> selectors;

    protected WeightedSeaLevelGenerator(List<Entry> selectors) {
        this.selectors = selectors;
        if (selectors.stream().mapToInt(Entry::weight).anyMatch(val -> val < 0) || selectors.stream().mapToInt(Entry::weight).sum() <= 0) {
            throw new IllegalArgumentException("Invalid weights");
        }
    }

    @Override
    public SerializableFluidPicker getSeaLevel(BlockState block, RandomSource random) {
        int total = selectors.stream().mapToInt(Entry::weight).sum();
        int rand = random.nextInt(total);
        for (Entry entry : selectors) {
            total -= entry.weight();
            if (rand >= total) {
                return entry.selector().value().getSeaLevel(block, random);
            }
        }
        throw new RuntimeException();
    }

    @Override
    public MapCodec<? extends WeightedSeaLevelGenerator> getCodec() {
        return SeaLevelGeneratorTypeRegistry.WEIGHTED.get();
    }

    protected record Entry(Holder<SeaLevelGenerator> selector, int weight) {
        private static final Codec<Entry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                SeaLevelGenerator.CODEC.fieldOf("selector").forGetter(Entry::selector),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 1).forGetter(Entry::weight)
        ).apply(inst, Entry::new));
    }

}
