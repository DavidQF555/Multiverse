package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.biome_source;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.biomes.LazyBiomeSource;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeSourceProviderTypeRegistry;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Function;

public interface BiomeSourceGenerator<T extends LazyBiomeSource> extends BiomeFieldGenerator<T> {

    Codec<BiomeSourceGenerator<?>> CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeSourceProviderTypeRegistry.getRegistry().getCodec().dispatch(BiomeSourceGenerator::getCodec, Function.identity()));

    Codec<? extends BiomeSourceGenerator<T>> getCodec();

}
