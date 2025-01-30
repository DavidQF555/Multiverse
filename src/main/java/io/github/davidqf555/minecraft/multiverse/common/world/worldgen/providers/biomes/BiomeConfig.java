package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.util.IntRange;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes.BiomeType;
import io.github.davidqf555.minecraft.multiverse.registration.custom.BiomeConfigRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

import java.util.*;

public record BiomeConfig(List<BiomeType> types, IntRange count) {

    public static final Codec<BiomeConfig> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BiomeType.CODEC.listOf().fieldOf("types").forGetter(BiomeConfig::types),
            IntRange.POSITIVE_CODEC.optionalFieldOf("count", IntRange.of(1, 1)).forGetter(BiomeConfig::count)
    ).apply(inst, BiomeConfig::new));
    public static final Codec<Holder<BiomeConfig>> CODEC = RegistryFileCodec.create(BiomeConfigRegistry.LOCATION, DIRECT_CODEC);

    private static BiomeType selectRandom(RandomSource random, List<BiomeType> types) {
        int total = types.stream().mapToInt(BiomeType::weight).sum();
        int selected = random.nextInt(total);
        for (BiomeType type : types) {
            total -= type.weight();
            if (total <= selected) {
                return type;
            }
        }
        throw new RuntimeException();
    }

    public Pair<MultiverseType, HolderSet<Biome>> selectRandom(RandomSource rand) {
        List<BiomeType> types = new ArrayList<>();
        outer:
        for (BiomeType type : this.types) {
            for (Holder<Biome> biome : type.biomes()) {
                for (MultiverseType mType : MultiverseType.values()) {
                    if (mType.is(biome)) {
                        types.add(type);
                        continue outer;
                    }
                }
            }
        }
        Map<MultiverseType, Set<Holder<Biome>>> partitions = new EnumMap<>(MultiverseType.class);
        int count = Math.min(types.size(), this.count.getRandom(rand));
        for (int i = 0; i < count; i++) {
            BiomeType type = selectRandom(rand, types);
            types.remove(type);
            for (MultiverseType mType : MultiverseType.values()) {
                if (type.biomes().stream().anyMatch(mType::is)) {
                    Set<Holder<Biome>> set;
                    if (partitions.containsKey(mType)) {
                        set = partitions.get(mType);
                    } else {
                        set = new HashSet<>();
                        partitions.put(mType, set);
                    }
                    type.biomes().forEach(set::add);
                }
            }
        }
        Map.Entry<MultiverseType, Set<Holder<Biome>>> max = null;
        for (Map.Entry<MultiverseType, Set<Holder<Biome>>> entry : partitions.entrySet()) {
            if (max == null || max.getValue().size() < entry.getValue().size()) {
                max = entry;
            }
        }
        if (max == null) {
            return Pair.of(MultiverseType.OVERWORLD, HolderSet.direct());
        }
        return Pair.of(max.getKey(), HolderSet.direct(List.copyOf(max.getValue())));
    }

}
