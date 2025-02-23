package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.biome_source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes.LazyBiomeSource;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeSourceGeneratorTypeRegistry;

import java.util.function.Function;

public interface BiomeSourceGenerator<T extends LazyBiomeSource> extends BiomeFieldGenerator<T> {

    Codec<BiomeSourceGenerator<?>> CODEC = Codec.lazyInitialized(() -> BiomeSourceGeneratorTypeRegistry.getRegistry().byNameCodec().dispatch(BiomeSourceGenerator::getCodec, Function.identity()));

    MapCodec<? extends BiomeSourceGenerator<T>> getCodec();

}
