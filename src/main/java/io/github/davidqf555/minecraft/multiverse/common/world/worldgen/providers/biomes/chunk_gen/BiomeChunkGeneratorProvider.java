package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.BiomeFieldProvider;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeChunkGeneratorProviderTypeRegistry;
import net.minecraft.world.level.chunk.ChunkGenerator;

import java.util.function.Function;

public interface BiomeChunkGeneratorProvider<T extends ChunkGenerator> extends BiomeFieldProvider<T> {

    Codec<BiomeChunkGeneratorProvider<?>> CODEC = Codec.lazyInitialized(() -> BiomeChunkGeneratorProviderTypeRegistry.getRegistry().byNameCodec().dispatch(BiomeChunkGeneratorProvider::getCodec, Function.identity()));

    MapCodec<? extends BiomeChunkGeneratorProvider<T>> getCodec();

}
