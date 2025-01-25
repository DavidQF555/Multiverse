package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes;

import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.RandomSource;

public interface BiomeFieldProvider<T> {

    T provide(RegistryAccess access, long seed, RandomSource random, MultiverseType type, HolderSet<Biome> biomes);

}
