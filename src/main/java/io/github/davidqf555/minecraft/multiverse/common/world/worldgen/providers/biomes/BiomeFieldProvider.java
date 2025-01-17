package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes;

import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

import java.util.Set;

public interface BiomeFieldProvider<T> {

    T provide(RegistryAccess access, long seed, RandomSource random, MultiverseType type, Set<HolderSet<Biome>> biomes);

}
