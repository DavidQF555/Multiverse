package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionProviderTypeRegistry;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.function.Function;

public interface BiomeDimensionProvider extends BiomeFieldProvider<LevelStem> {

    Codec<BiomeDimensionProvider> CODEC = Codec.lazyInitialized(() -> BiomeDimensionProviderTypeRegistry.getRegistry().byNameCodec().dispatch(BiomeDimensionProvider::getCodec, Function.identity()));

    MapCodec<? extends BiomeDimensionProvider> getCodec();

}
