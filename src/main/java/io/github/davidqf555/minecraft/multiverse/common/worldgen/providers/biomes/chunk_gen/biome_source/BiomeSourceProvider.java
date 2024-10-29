package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.biome_source;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.BiomeFieldProvider;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeSourceProviderTypeRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.BiomeSource;

import java.util.function.Function;

public interface BiomeSourceProvider<T extends BiomeSource> extends BiomeFieldProvider<T> {

    Codec<BiomeSourceProvider<?>> CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeSourceProviderTypeRegistry.getRegistry().getCodec().dispatch(BiomeSourceProvider::getCodec, Function.identity()));

    Codec<? extends BiomeSourceProvider<T>> getCodec();

}
