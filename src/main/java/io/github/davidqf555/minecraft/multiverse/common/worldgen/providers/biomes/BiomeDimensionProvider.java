package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionProviderTypeRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.function.Function;

public interface BiomeDimensionProvider extends BiomeFieldProvider<LevelStem> {

    Codec<BiomeDimensionProvider> CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeDimensionProviderTypeRegistry.getRegistry().getCodec().dispatch(BiomeDimensionProvider::getCodec, Function.identity()));

    Codec<? extends BiomeDimensionProvider> getCodec();

}
