package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionProviderTypeRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.LevelStem;

public interface BiomeDimensionProvider extends BiomeFieldGenerator<LevelStem> {

    Codec<BiomeDimensionProvider> CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeDimensionProviderTypeRegistry.getRegistry().getCodec().dispatch(BiomeDimensionProvider::getType, BiomeDimensionGeneratorType::getCodec));

    BiomeDimensionGeneratorType getType();

}
