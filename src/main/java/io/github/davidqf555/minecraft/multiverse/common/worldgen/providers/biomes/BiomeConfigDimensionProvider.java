package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.BiomeConfig;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.BiomeType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.DimensionProvider;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.DimensionProviderType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.IntRange;
import io.github.davidqf555.minecraft.multiverse.registration.custom.DimensionProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.RandomSource;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BiomeConfigDimensionProvider implements DimensionProvider {

    public static final Codec<BiomeConfigDimensionProvider> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BiomeConfig.CODEC.fieldOf("biomes").forGetter(val -> val.config),
            BiomeDimensionProvider.CODEC.fieldOf("dimension").forGetter(val -> val.provider),
            IntRange.CODEC.optionalFieldOf("count", IntRange.of(1, 1)).forGetter(val -> val.count)
    ).apply(inst, BiomeConfigDimensionProvider::new));
    private final Holder<BiomeConfig> config;
    private final BiomeDimensionProvider provider;
    private final IntRange count;

    public BiomeConfigDimensionProvider(Holder<BiomeConfig> config, BiomeDimensionProvider provider, IntRange count) {
        this.config = config;
        this.provider = provider;
        this.count = count;
    }

    private static BiomeType selectRandom(RandomSource random, List<BiomeType> types) {
        int total = types.stream().mapToInt(BiomeType::getWeight).sum();
        int selected = random.nextInt(total);
        for (BiomeType type : types) {
            total -= type.getWeight();
            if (total <= selected) {
                return type;
            }
        }
        throw new RuntimeException();
    }

    @Override
    public LevelStem createDimension(RegistryAccess access, long seed, RandomSource random) {
        BiomeConfig config = this.config.value();
        Registry<Biome> registry = access.registryOrThrow(Registry.BIOME_REGISTRY);
        Set<MultiverseType> biomesTypes = EnumSet.allOf(MultiverseType.class);
        Predicate<ResourceKey<Biome>> valid = key -> biomesTypes.stream().anyMatch(type -> type.is(key));
        List<BiomeType> types = config.getTypes().stream().filter(type -> type.getBiomes(registry).stream().anyMatch(valid)).collect(Collectors.toList());
        Set<ResourceKey<Biome>> biomes = new HashSet<>();
        if (types.isEmpty()) {
            biomes.add(Biomes.THE_VOID);
        } else {
            BiomeType type = selectRandom(random, types);
            types.remove(type);
            biomes.addAll(type.getBiomes(registry));
        }
        int count = Math.min(types.size(), this.count.getRandom(random));
        for (int i = 0; i < count; i++) {
            BiomeType type = selectRandom(random, types);
            types.remove(type);
            biomes.addAll(type.getBiomes(registry));
        }
        biomes.removeIf(valid.negate());
        Map<MultiverseType, Integer> counts = new EnumMap<>(MultiverseType.class);
        for (ResourceKey<Biome> biome : biomes) {
            for (MultiverseType type : biomesTypes) {
                if (type.is(biome)) {
                    counts.compute(type, (t, current) -> current == null ? 1 : current + 1);
                }
            }
        }
        MultiverseType type = counts.keySet().stream().max((i, j) -> counts.get(j) - counts.get(i)).orElseThrow();
        biomes.removeIf(key -> !type.is(key));
        return provider.provide(access, seed, random, type, biomes);
    }

    @Override
    public DimensionProviderType<? extends DimensionProvider> getType() {
        return DimensionProviderTypeRegistry.BIOME_CONFIG.get();
    }

}
