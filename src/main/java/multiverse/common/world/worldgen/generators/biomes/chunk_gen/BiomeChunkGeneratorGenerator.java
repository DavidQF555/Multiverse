package multiverse.common.world.worldgen.generators.biomes.chunk_gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import multiverse.registration.custom.generator.biomes.BiomeChunkGeneratorGeneratorTypeRegistry;
import net.minecraft.world.level.chunk.ChunkGenerator;

import java.util.function.Function;

public interface BiomeChunkGeneratorGenerator<T extends ChunkGenerator> extends BiomeFieldGenerator<T> {

    Codec<BiomeChunkGeneratorGenerator<?>> CODEC = Codec.lazyInitialized(() -> BiomeChunkGeneratorGeneratorTypeRegistry.getRegistry().byNameCodec().dispatch(BiomeChunkGeneratorGenerator::getCodec, Function.identity()));

    MapCodec<? extends BiomeChunkGeneratorGenerator<T>> getCodec();

}
