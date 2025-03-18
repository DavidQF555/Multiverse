package multiverse.common.world.worldgen.generators.biomes.chunk_gen;

import com.mojang.serialization.Codec;
import multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import multiverse.registration.custom.generator.biomes.BiomeChunkGeneratorGeneratorTypeRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.chunk.ChunkGenerator;

public interface BiomeChunkGeneratorGenerator<T extends ChunkGenerator> extends BiomeFieldGenerator<T> {

    Codec<BiomeChunkGeneratorGenerator<?>> CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeChunkGeneratorGeneratorTypeRegistry.getRegistry().getCodec().dispatch(BiomeChunkGeneratorGenerator::getType, BiomeChunkGeneratorGeneratorType::getCodec));

    BiomeChunkGeneratorGeneratorType<? extends BiomeChunkGeneratorGenerator<T>> getType();

}
