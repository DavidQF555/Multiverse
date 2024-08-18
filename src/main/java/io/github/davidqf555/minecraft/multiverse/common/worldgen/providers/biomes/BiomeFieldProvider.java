package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.RandomSource;

import java.util.Set;

public interface BiomeFieldProvider<T> {

    T provide(RegistryAccess access, long seed, RandomSource random, MultiverseType type, Set<ResourceKey<Biome>> biomes);

}
