package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeChunkGeneratorGeneratorTypeRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.chunk.ChunkGenerator;

public interface BiomeChunkGeneratorGenerator<T extends ChunkGenerator> extends BiomeFieldGenerator<T> {

    Codec<BiomeChunkGeneratorGenerator<?>> CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeChunkGeneratorGeneratorTypeRegistry.getRegistry().getCodec().dispatch(BiomeChunkGeneratorGenerator::getType, BiomeChunkGeneratorGeneratorType::getCodec));

    BiomeChunkGeneratorGeneratorType<? extends BiomeChunkGeneratorGenerator<T>> getType();

}
