package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.dim_type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionTypeProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.List;
import java.util.Set;

public class WeightedDimensionTypeProvider implements BiomeDimensionTypeProvider {

    public static final Codec<WeightedDimensionTypeProvider> CODEC = Entry.CODEC.listOf().xmap(WeightedDimensionTypeProvider::new, val -> val.entries).fieldOf("entries").codec();
    private final List<Entry> entries;

    public WeightedDimensionTypeProvider(List<Entry> entries) {
        if (entries.stream().mapToInt(Entry::weight).sum() <= 0 || entries.stream().mapToInt(Entry::weight).anyMatch(weight -> weight < 0)) {
            throw new IllegalArgumentException("Invalid weights");
        }
        this.entries = entries;
    }

    @Override
    public Holder<DimensionType> provide(RegistryAccess access, long seed, RandomSource random, MultiverseType type, Set<ResourceKey<Biome>> biomes) {
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
    public Codec<? extends WeightedDimensionTypeProvider> getCodec() {
        return BiomeDimensionTypeProviderTypeRegistry.WEIGHTED.get();
    }

    public record Entry(Holder<DimensionType> value, int weight) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                DimensionType.CODEC.fieldOf("value").forGetter(Entry::value),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 1).forGetter(Entry::weight)
        ).apply(inst, Entry::new));
    }

}
