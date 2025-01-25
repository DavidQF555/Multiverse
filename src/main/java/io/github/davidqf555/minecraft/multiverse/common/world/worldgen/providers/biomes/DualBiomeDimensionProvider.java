package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.BiomeChunkGeneratorProvider;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.dim_type.BiomeDimensionTypeProvider;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.RandomSource;

public class DualBiomeDimensionProvider implements BiomeDimensionProvider {

    public static final Codec<DualBiomeDimensionProvider> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BiomeChunkGeneratorProvider.CODEC.fieldOf("chunk").forGetter(val -> val.chunk),
            BiomeDimensionTypeProvider.CODEC.fieldOf("dim_type").forGetter(val -> val.type)
    ).apply(inst, DualBiomeDimensionProvider::new));
    private final BiomeChunkGeneratorProvider<?> chunk;
    private final Holder<BiomeDimensionTypeProvider> type;

    public DualBiomeDimensionProvider(BiomeChunkGeneratorProvider<?> chunk, Holder<BiomeDimensionTypeProvider> type) {
        this.chunk = chunk;
        this.type = type;
    }

    @Override
    public LevelStem provide(RegistryAccess access, long seed, RandomSource random, MultiverseType type, HolderSet<Biome> biomes) {
        ChunkGenerator gen = chunk.provide(access, seed, random, type, biomes);
        Holder<DimensionType> holder = this.type.value().provide(access, seed, random, type, biomes);
        return new LevelStem(holder, gen);
    }

    @Override
    public BiomeDimensionProviderType getType() {
        return BiomeDimensionProviderTypeRegistry.DUAL.get();
    }

}
