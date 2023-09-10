package io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BiomeType {

    public static final Codec<BiomeType> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(type -> type.biomes),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 1).forGetter(type -> type.weight)
    ).apply(inst, BiomeType::new));
    private final HolderSet<Biome> biomes;
    private final int weight;

    public BiomeType(HolderSet<Biome> biomes, int weight) {
        this.biomes = biomes;
        this.weight = weight;
        if (weight < 0) {
            throw new IllegalArgumentException("weight cannot be negative");
        }
    }

    public Set<ResourceKey<Biome>> getBiomes(Registry<Biome> registry) {
        List<Holder<Biome>> biomes = new ArrayList<>();
        this.biomes.unwrap()
                .ifRight(biomes::addAll)
                .ifLeft(key -> biomes.addAll(Lists.newArrayList(registry.getTagOrEmpty(key))));
        return biomes.stream()
                .filter(Holder::isBound)
                .map(Holder::value)
                .map(biome -> ResourceKey.create(Registry.BIOME_REGISTRY, biome.getRegistryName()))
                .collect(Collectors.toSet());
    }

    public int getWeight() {
        return weight;
    }

}
