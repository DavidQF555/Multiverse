package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.registration.custom.SeaLevelProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class WeightedSeaLevelProvider implements SeaLevelProvider {

    public static final Codec<WeightedSeaLevelProvider> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Entry.CODEC.listOf().fieldOf("selectors").forGetter(val -> val.selectors)
    ).apply(inst, WeightedSeaLevelProvider::new));
    private final List<Entry> selectors;

    protected WeightedSeaLevelProvider(List<Entry> selectors) {
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
    public Codec<? extends WeightedSeaLevelProvider> getCodec() {
        return SeaLevelProviderTypeRegistry.WEIGHTED.get();
    }

    protected record Entry(Holder<SeaLevelProvider> selector, int weight) {
        private static final Codec<Entry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                SeaLevelProvider.CODEC.fieldOf("selector").forGetter(Entry::selector),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 1).forGetter(Entry::weight)
        ).apply(inst, Entry::new));
    }

}
