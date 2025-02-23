package multiverse.common.world.worldgen.generators.biomes;

import multiverse.common.world.worldgen.MultiverseType;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.RandomSource;

public interface BiomeFieldGenerator<T> {

    T generate(RegistryAccess access, long seed, RandomSource random, MultiverseType type, HolderSet<Biome> biomes);

}
