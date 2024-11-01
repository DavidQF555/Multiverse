package io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.util.IntRange;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.registration.custom.BiomeConfigRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record BiomeConfig(List<BiomeType> types, IntRange count) {

    public static final Codec<BiomeConfig> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BiomeType.CODEC.listOf().fieldOf("types").forGetter(BiomeConfig::types),
            IntRange.POSITIVE_CODEC.optionalFieldOf("count", IntRange.of(1, 1)).forGetter(BiomeConfig::count)
    ).apply(inst, BiomeConfig::new));
    public static final Codec<Holder<BiomeConfig>> CODEC = RegistryFileCodec.create(BiomeConfigRegistry.LOCATION, DIRECT_CODEC);

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

    public Pair<MultiverseType, Set<ResourceKey<Biome>>> selectRandom(Registry<Biome> registry, RandomSource rand) {
        Set<MultiverseType> biomesTypes = EnumSet.allOf(MultiverseType.class);
        Predicate<ResourceKey<Biome>> valid = key -> biomesTypes.stream().anyMatch(type -> type.is(key));
        List<BiomeType> types = types().stream().filter(type -> type.getBiomes(registry).stream().anyMatch(valid)).collect(Collectors.toList());
        if (types.isEmpty()) {
            return Pair.of(MultiverseType.OVERWORLD, Set.of(Biomes.THE_VOID));
        }
        Map<MultiverseType, Set<ResourceKey<Biome>>> partitions = new EnumMap<>(MultiverseType.class);
        int count = Math.min(types.size(), this.count.getRandom(rand));
        for (int i = 0; i < count; i++) {
            BiomeType type = selectRandom(rand, types);
            types.remove(type);
            for (ResourceKey<Biome> biome : type.getBiomes(registry)) {
                if (valid.test(biome)) {
                    for (MultiverseType mType : MultiverseType.values()) {
                        if (mType.is(biome)) {
                            Set<ResourceKey<Biome>> set;
                            if (partitions.containsKey(mType)) {
                                set = partitions.get(mType);
                            } else {
                                set = new HashSet<>();
                                partitions.put(mType, set);
                            }
                            set.add(biome);
                        }
                    }
                }
            }
        }
        Map.Entry<MultiverseType, Set<ResourceKey<Biome>>> entry = partitions.entrySet().stream().max(Comparator.comparingInt(v -> v.getValue().size())).orElseThrow();
        return Pair.of(entry.getKey(), entry.getValue());
    }

}
