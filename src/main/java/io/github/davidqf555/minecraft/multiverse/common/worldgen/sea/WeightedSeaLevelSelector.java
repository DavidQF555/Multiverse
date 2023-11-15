package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class WeightedSeaLevelSelector implements SeaLevelSelector {

    public static final Supplier<Codec<WeightedSeaLevelSelector>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> inst.group(
            Entry.CODEC.get().listOf().fieldOf("entries").flatXmap(WeightedSeaLevelSelector::nonempty, WeightedSeaLevelSelector::nonempty).forGetter(selector -> selector.selectors)
    ).apply(inst, WeightedSeaLevelSelector::new)));
    private static final Random RANDOM = new Random(0);
    private final List<Entry> selectors;

    public WeightedSeaLevelSelector(List<Entry> selectors) {
        this.selectors = selectors;
    }

    private static DataResult<List<Entry>> nonempty(List<Entry> entries) {
        if (entries.isEmpty()) {
            return DataResult.error("List cannot be empty");
        }
        return DataResult.success(entries);
    }

    @Override
    public SerializableFluidPicker getSeaLevel(BlockState block, long seed, int index) {
        int total = selectors.stream().map(Entry::weight).reduce(Integer::sum).orElseThrow();
        RANDOM.setSeed(seed + index * 6000000L);
        int rand = RANDOM.nextInt(total);
        for (Entry entry : selectors) {
            total -= entry.weight();
            if (rand >= total) {
                return entry.selector().getSeaLevel(block, seed, index);
            }
        }
        throw new RuntimeException();
    }

    @Override
    public Codec<? extends SeaLevelSelector> codec() {
        return CODEC.get();
    }

    public record Entry(SeaLevelSelector selector, int weight) {
        public static final Supplier<Codec<Entry>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> inst.group(
                SeaLevelSelector.CODEC.get().fieldOf("selector").forGetter(Entry::selector),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("weight", 1).forGetter(Entry::weight)
        ).apply(inst, Entry::new)));
    }

}
