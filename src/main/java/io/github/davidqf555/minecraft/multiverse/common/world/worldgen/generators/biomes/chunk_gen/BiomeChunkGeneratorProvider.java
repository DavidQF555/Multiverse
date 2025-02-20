package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeChunkGeneratorProviderTypeRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.chunk.ChunkGenerator;

public interface BiomeChunkGeneratorProvider<T extends ChunkGenerator> extends BiomeFieldGenerator<T> {

    Codec<BiomeChunkGeneratorProvider<?>> CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeChunkGeneratorProviderTypeRegistry.getRegistry().getCodec().dispatch(BiomeChunkGeneratorProvider::getType, BiomeChunkGeneratorProviderType::getCodec));

    BiomeChunkGeneratorProviderType<? extends BiomeChunkGeneratorProvider<T>> getType();

}
