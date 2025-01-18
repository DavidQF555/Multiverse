package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.biome_source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes.LazyMultiverseBiomeSource;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

import java.util.Set;

public class NoiseBiomeSourceProvider implements BiomeSourceProvider<LazyMultiverseBiomeSource> {

    public static final MapCodec<NoiseBiomeSourceProvider> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.INT.fieldOf("min_y").forGetter(val -> val.minY),
            Codec.INT.fieldOf("max_y").forGetter(val -> val.maxY)
    ).apply(inst, NoiseBiomeSourceProvider::new));
    private static final long OFFSET = 55555;
    private final int minY, maxY;

    public NoiseBiomeSourceProvider(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public LazyMultiverseBiomeSource provide(RegistryAccess access, long seed, RandomSource random, MultiverseType type, Set<HolderSet<Biome>> biomes) {
        return new LazyMultiverseBiomeSource(access.lookupOrThrow(Registries.BIOME), access.lookupOrThrow(Registries.DIMENSION_TYPE), minY, maxY, type, biomes, seed + OFFSET);
    }

    @Override
    public MapCodec<? extends NoiseBiomeSourceProvider> getCodec() {
        return CODEC;
    }

}
