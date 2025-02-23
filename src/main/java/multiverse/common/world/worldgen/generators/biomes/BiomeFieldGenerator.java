package multiverse.common.world.worldgen.generators.biomes;

import multiverse.common.world.worldgen.MultiverseType;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

public interface BiomeFieldGenerator<T> {

    T generate(RegistryAccess access, long seed, RandomSource random, MultiverseType type, HolderSet<Biome> biomes);

}
