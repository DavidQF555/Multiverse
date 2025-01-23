package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.util.IntRange;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.registration.custom.BiomeConfigRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BiomeConfig extends ForgeRegistryEntry<BiomeConfig> {

    public static final Codec<BiomeConfig> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BiomeType.CODEC.listOf().fieldOf("types").forGetter(BiomeConfig::getTypes),
            IntRange.POSITIVE_CODEC.optionalFieldOf("count", IntRange.of(1, 1)).forGetter(BiomeConfig::getCount)
    ).apply(inst, BiomeConfig::new));
    public static final Codec<Holder<BiomeConfig>> CODEC = RegistryFileCodec.create(BiomeConfigRegistry.LOCATION, DIRECT_CODEC);
    private final List<BiomeType> types;
    private final IntRange count;

    public BiomeConfig(List<BiomeType> types, IntRange count) {
        this.types = types;
        this.count = count;
    }

    public List<BiomeType> getTypes() {
        return types;
    }

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

    public IntRange getCount() {
        return count;
    }

    public Pair<MultiverseType, Set<HolderSet<Biome>>> selectRandom(RandomSource rand, Holder<Biome> defaultBiome) {
        List<BiomeType> types = new ArrayList<>();
        outer:
        for (BiomeType type : getTypes()) {
            for (Holder<Biome> biome : type.biomes()) {
                for (MultiverseType mType : MultiverseType.values()) {
                    if (mType.is(biome)) {
                        types.add(type);
                        continue outer;
                    }
                }
            }
        }
        Set<HolderSet<Biome>> sets = new HashSet<>();
        int[] partitions = new int[MultiverseType.values().length];
        int count = Math.min(types.size(), this.count.getRandom(rand));
        for (int i = 0; i < count; i++) {
            BiomeType type = selectRandom(rand, types);
            types.remove(type);
            for (Holder<Biome> biome : type.biomes()) {
                for (MultiverseType mType : MultiverseType.values()) {
                    if (mType.is(biome)) {
                        partitions[mType.ordinal()]++;
                    }
                }
            }
            sets.add(type.biomes());
        }
        int max = -1;
        for (int i = 0; i < partitions.length; i++) {
            if (partitions[i] > 0 && (max == -1 || partitions[i] > partitions[max])) {
                max = i;
            }
        }
        if (max == -1) {
            return Pair.of(MultiverseType.OVERWORLD, Set.of(HolderSet.direct(List.of(defaultBiome))));
        }
        return Pair.of(MultiverseType.values()[max], sets);
    }

}
