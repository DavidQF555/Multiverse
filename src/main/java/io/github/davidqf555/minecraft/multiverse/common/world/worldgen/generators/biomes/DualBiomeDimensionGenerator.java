package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.BiomeChunkGeneratorGenerator;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.dim_type.BiomeDimensionTypeGenerator;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.RandomSource;

public class DualBiomeDimensionGenerator implements BiomeDimensionGenerator {

    public static final Codec<DualBiomeDimensionGenerator> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BiomeChunkGeneratorGenerator.CODEC.fieldOf("chunk").forGetter(val -> val.chunk),
            BiomeDimensionTypeGenerator.CODEC.fieldOf("dim_type").forGetter(val -> val.type)
    ).apply(inst, DualBiomeDimensionGenerator::new));
    private final BiomeChunkGeneratorGenerator<?> chunk;
    private final Holder<BiomeDimensionTypeGenerator> type;

    public DualBiomeDimensionGenerator(BiomeChunkGeneratorGenerator<?> chunk, Holder<BiomeDimensionTypeGenerator> type) {
        this.chunk = chunk;
        this.type = type;
    }

    @Override
    public LevelStem generate(RegistryAccess access, long seed, RandomSource random, MultiverseType type, HolderSet<Biome> biomes) {
        ChunkGenerator gen = chunk.generate(access, seed, random, type, biomes);
        Holder<DimensionType> holder = this.type.value().generate(access, seed, random, type, biomes);
        return new LevelStem(holder, gen);
    }

    @Override
    public BiomeDimensionGeneratorType getType() {
        return BiomeDimensionGeneratorTypeRegistry.DUAL.get();
    }

}
