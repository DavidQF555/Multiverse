package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.dim_type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionTypeGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.List;

public class WeightedDimensionTypeGenerator implements BiomeDimensionTypeGenerator {

    public static final Codec<WeightedDimensionTypeGenerator> CODEC = Entry.CODEC.listOf().xmap(WeightedDimensionTypeGenerator::new, val -> val.entries).fieldOf("entries").codec();
    private final List<Entry> entries;

    public WeightedDimensionTypeGenerator(List<Entry> entries) {
        if (entries.stream().mapToInt(Entry::weight).sum() <= 0 || entries.stream().mapToInt(Entry::weight).anyMatch(weight -> weight < 0)) {
            throw new IllegalArgumentException("Invalid weights");
        }
        this.entries = entries;
    }

    @Override
    public Holder<DimensionType> generate(RegistryAccess access, long seed, RandomSource random, MultiverseType type, HolderSet<Biome> biomes) {
        int total = entries.stream().mapToInt(Entry::weight).sum();
        int rand = random.nextInt(total);
        for (Entry entry : entries) {
            total -= entry.weight();
            if (rand >= total) {
                return entry.value();
            }
        }
        throw new RuntimeException("Should never get here");
    }

    @Override
    public Codec<? extends WeightedDimensionTypeGenerator> getCodec() {
        return BiomeDimensionTypeGeneratorTypeRegistry.WEIGHTED.get();
    }

    public record Entry(Holder<DimensionType> value, int weight) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                DimensionType.CODEC.fieldOf("value").forGetter(Entry::value),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 1).forGetter(Entry::weight)
        ).apply(inst, Entry::new));
    }

}
