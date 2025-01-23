package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;

public record BiomeType(HolderSet<Biome> biomes, int weight) {

    public static final Codec<BiomeType> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistryCodecs.homogeneousList(Registry.BIOME_REGISTRY, true).fieldOf("biomes").forGetter(type -> type.biomes),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 1).forGetter(type -> type.weight)
    ).apply(inst, BiomeType::new));

    public BiomeType(HolderSet<Biome> biomes, int weight) {
        this.biomes = biomes;
        this.weight = weight;
        if (weight < 0) {
            throw new IllegalArgumentException("weight cannot be negative");
        }
    }

}
