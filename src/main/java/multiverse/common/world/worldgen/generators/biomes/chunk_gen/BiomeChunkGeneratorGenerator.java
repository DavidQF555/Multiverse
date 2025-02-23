package multiverse.common.world.worldgen.generators.biomes.chunk_gen;

import com.mojang.serialization.Codec;
import multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import multiverse.registration.custom.biomes.BiomeChunkGeneratorGeneratorTypeRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.chunk.ChunkGenerator;

import java.util.function.Function;

public interface BiomeChunkGeneratorGenerator<T extends ChunkGenerator> extends BiomeFieldGenerator<T> {

    Codec<BiomeChunkGeneratorGenerator<?>> CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeChunkGeneratorGeneratorTypeRegistry.getRegistry().getCodec().dispatch(BiomeChunkGeneratorGenerator::getCodec, Function.identity()));

    Codec<? extends BiomeChunkGeneratorGenerator<T>> getCodec();

}
